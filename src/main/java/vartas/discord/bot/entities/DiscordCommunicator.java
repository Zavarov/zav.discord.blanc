/*
 * Copyright (c) 2019 Zavarov
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package vartas.discord.bot.entities;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.internal.utils.JDALogger;
import org.slf4j.Logger;
import vartas.discord.bot.CommandBuilder;
import vartas.discord.bot.EntityAdapter;
import vartas.discord.bot.listener.*;
import vartas.discord.bot.message.InteractiveMessage;
import vartas.discord.bot.visitor.DiscordCommunicatorVisitor;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;

public class DiscordCommunicator {
    protected ExecutorService executor = Executors.newWorkStealingPool();
    /**
     * The logger for the communicator.
     */
    protected Logger log = JDALogger.getLog(this.getClass().getSimpleName());
    /**
     * The tracker that removes interactive message that are too old.
     */
    protected final InteractiveMessageListener messages;
    /**
     * The activity tracker for all message.
     */
    protected final ActivityListener activity;
    /**
     * The listener responsible for filtering all blacklisted words.
     */
    protected BlacklistListener blacklist;
    /**
     * The listener responsible for parsing and scheduling the bot commands.
     */
    protected CommandListener command;
    /**
     * The environment of the program.
     */
    protected final DiscordEnvironment environment;
    /**
     * The JDA that this communicator uses.
     */
    protected final JDA jda;
    /**
     * All configuration files of the guilds in this shard.
     */
    protected final LoadingCache<Guild, BotGuild> guilds;
    /**
     * The adapter for parsing the local data files.
     */
    protected final EntityAdapter adapter;
    /**
     * Initializes all necessary tasks for the communicator in this shard.
     * @param environment the environment of the program
     * @param jda the JDA that this communicator uses.
     * @param builder the builder for generating the commands from the calls
     */
    public DiscordCommunicator(DiscordEnvironment environment, JDA jda, Function<DiscordCommunicator, CommandBuilder> builder, EntityAdapter adapter){
        this.environment = environment;
        this.jda = jda;
        this.activity = new ActivityListener(this);
        this.messages = new InteractiveMessageListener(environment.config());
        this.blacklist = new BlacklistListener(this);
        this.command = new CommandListener(this, builder.apply(this));
        this.adapter = adapter;
        this.guilds = CacheBuilder.newBuilder().expireAfterAccess(1, TimeUnit.HOURS).build(CacheLoader.from(g -> adapter.guild(g, this)));

        //Load the configuration for each guild
        jda.getGuilds().forEach(this::guild);

        jda.addEventListener(activity);
        jda.addEventListener(messages);
        jda.addEventListener(blacklist);
        jda.addEventListener(command);
        jda.addEventListener(new MiscListener(this));

        environment.schedule(activity, environment.config().getActivityUpdateInterval(), TimeUnit.MINUTES);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //                                                                                                                //
    //   Internal                                                                                                     //
    //                                                                                                                //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public DiscordEnvironment environment(){
        return environment;
    }
    public BotGuild guild(Guild guild){
        return guilds.getUnchecked(guild);
    }
    public void remove(Guild guild){
        BotGuild config = guild(guild);
        //Delete file
        adapter.delete(config);
        //Remove prefix
        command.remove(guild);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //                                                                                                                //
    //   Threads                                                                                                      //
    //                                                                                                                //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * @return the task that will await the termination of all threads of this shard.
     */
    public Runnable shutdown() {
        jda.shutdown();
        executor.shutdown();
        log.info("Shutting down shard "+jda.getShardInfo().getShardString()+".");
        return () -> {
            try{
                executor.awaitTermination(1, TimeUnit.MINUTES);
            }catch(InterruptedException e){
                log.error(e.getMessage());
            }
        };
    }
    public void schedule(Runnable runnable){
        executor.submit(runnable);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //                                                                                                                //
    //   Discord                                                                                                      //
    //                                                                                                                //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public JDA jda(){
        return jda;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //                                                                                                                //
    //   Send                                                                                                         //
    //                                                                                                                //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void send(MessageChannel channel, String message){
        MessageBuilder builder = new MessageBuilder();
        builder.setContent(message);
        send(channel, builder);
    }
    public void send(MessageChannel channel, MessageEmbed embed){
        MessageBuilder builder = new MessageBuilder();
        builder.setEmbed(embed);
        send(channel, builder);
    }
    public void send(MessageChannel channel, BufferedImage image) throws IllegalArgumentException{
        try{
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            ImageIO.write(image, "png", output);

            ByteArrayInputStream input = new ByteArrayInputStream(output.toByteArray());
            send(channel.sendFile(input, "image.png"));
        }catch(IOException e){
            throw new IllegalArgumentException(e);
        }
    }
    public void send(MessageChannel channel, File file){
        send(channel.sendFile(file));
    }
    public void send(MessageChannel channel, InteractiveMessage message){
        MessageBuilder builder = new MessageBuilder();
        builder.setEmbed(message.build());
        send(channel, builder, received -> {
            send(received.addReaction(InteractiveMessage.ARROW_LEFT));
            send(received.addReaction(InteractiveMessage.ARROW_RIGHT));
            messages.add(received, message);
        });
    }
    public void send(MessageChannel channel, MessageBuilder message, Consumer<Message> success, Consumer<Throwable> failure) {
        Message m = message.stripMentions(jda()).build();
        send(channel.sendMessage(m),success,failure);
    }
    public void send(MessageChannel channel, MessageBuilder message, Consumer<Message> success){
        send(channel, message, success, null);
    }
    public void send(MessageChannel channel, MessageBuilder message){
        send(channel, message, null);
    }
    public <T> void send(RestAction<T> action, Consumer<T> success, Consumer<Throwable> failure){
        schedule(() -> action.queue(success, failure));
    }
    public <T> void send(RestAction<T> action, Consumer<T> success){
        send(action, success, null);
    }
    public <T> void send(RestAction<T> action){
        send(action, null);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //                                                                                                                //
    //   Visitor                                                                                                      //
    //                                                                                                                //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void accept(DiscordCommunicatorVisitor visitor){
        guilds.asMap().values().forEach(visitor::handle);
        visitor.handle(command);
        visitor.handle(blacklist);
        visitor.handle(activity);
    }
}
