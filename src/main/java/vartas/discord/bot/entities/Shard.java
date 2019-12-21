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

import com.google.common.base.Preconditions;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.internal.utils.JDALogger;
import org.slf4j.Logger;
import vartas.discord.bot.CommandBuilder;
import vartas.discord.bot.EntityAdapter;
import vartas.discord.bot.internal.LoadConfiguration;
import vartas.discord.bot.internal.UnloadConfiguration;
import vartas.discord.bot.listener.*;
import vartas.discord.bot.message.InteractiveMessage;

import javax.annotation.Nonnull;
import javax.security.auth.login.LoginException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * This class represents the instance of this bot on a single shard.<br>
 * Each shard represents an isolated program, meaning that one shard is not aware of all other shards.<br>
 * Meaning that if information has to be shared across multiple shards, it has to be done via an external scope.
 */
public abstract class Shard implements Cluster.ClusterVisitor{
    /**
     * The id associated with this shard.
     */
    private final int shardId;
    /**
     * The logger for the communicator.
     */
    @Nonnull
    private final Logger log = JDALogger.getLog(this.getClass());
    /**
     * The executor for all parallel tasks that are executed by this bot. This will mostly be used for the commands.
     * It is also used for updating the activity chart, hence why it will contain at least one thread.
     */
    @Nonnull
    private ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
    /**
     * All configuration files of the guilds in this shard.
     */
    @Nonnull
    private final LoadingCache<Guild, Configuration> guilds = CacheBuilder.newBuilder().expireAfterAccess(1, TimeUnit.HOURS).build(CacheLoader.from(this::create));
    /**
     * The listener responsible for reacting to interactive messages and deleting them, when they haven't been
     * used for a arbitrary but fixed amount of time.
     */
    private InteractiveMessageListener messages;
    /**
     * The activity tracker for all message.
     */
    private ActivityListener activity;
    /**
     * The listener responsible for filtering all blacklisted words.
     */
    private BlacklistListener blacklist;
    /**
     * The listener responsible for parsing and scheduling the bot commands.
     */
    private CommandListener command;
    /**
     * The listener for all miscellaneous events.
     */
    private MiscListener misc;
    /**
     * The JDA over the current shard.
     */
    private JDA jda;
    /**
     * The cluster instance managing the global functionality.
     * All nodes share the same cluster.
     */
    private Cluster cluster;

    private EntityAdapter adapter;

    /**
     * Initializes a fresh shard.
     * @param shardId the shard id.
     */
    public Shard(int shardId){
        this.shardId = shardId;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //                                                                                                                //
    //   Internal                                                                                                     //
    //                                                                                                                //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Loads the configuration file from the disk that is associated with the specified {@code guild}.
     * If no such file exists, a fresh configuration is returned.
     * @param guild the guild associated with the configuration file.
     * @return the configuration for the specified guild
     */
    public Configuration guild(Guild guild){
        return guilds.getUnchecked(guild);
    }
    public void remove(Guild guild){
        Configuration configuration = guild(guild);
        getCluster().accept(new UnloadConfiguration(configuration));
    }
    private Configuration create(Guild guild){
        Configuration configuration = adapter.configuration(guild, this);
        getCluster().accept(new LoadConfiguration(configuration));
        return configuration;
    }
    @Nonnull
    public Cluster getCluster(){
        return cluster;
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
                executor.shutdownNow();
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
    protected abstract CommandBuilder createCommandBuilder();
    protected abstract JDA createJda(int shardId, Credentials credentials) throws LoginException, InterruptedException;

    public JDA jda(){
        return jda;
    }

    public <T> void queue(RestAction<T> action){
        queue(action, null);
    }

    public <T> void queue(RestAction<T> action, Consumer<? super T> success){
        queue(action, success, null);
    }

    public <T> void queue(RestAction<T> action, Consumer<? super T> success, Consumer<? super Throwable> failure){
        action.queue(success, failure);
    }

    public void queue(MessageChannel channel, InteractiveMessage interactiveMessage){
        MessageEmbed message = interactiveMessage.build();
        Consumer<Message> onSuccess = (received) -> {
            messages.add(received, interactiveMessage);
            queue(received.addReaction(InteractiveMessage.ARROW_LEFT));
            queue(received.addReaction(InteractiveMessage.ARROW_RIGHT));
        };
        queue(channel.sendMessage(message), onSuccess);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //                                                                                                                //
    //   Initialization                                                                                               //
    //                                                                                                                //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void visit(@Nonnull Cluster cluster){
        this.cluster = cluster;
    }

    @Override
    public void endVisit(@Nonnull Cluster cluster){
        cluster.accept(misc);
    }

    @Override
    public void visit(@Nonnull EntityAdapter adapter){
        this.adapter = adapter;
    }

    @Override
    public void visit(@Nonnull Credentials credentials){
        try {
            this.jda = createJda(shardId, credentials);
            this.activity = new ActivityListener(jda, credentials.getActivityUpdateInterval());
            this.messages = new InteractiveMessageListener(credentials);
            this.blacklist = new BlacklistListener(this);
            this.command = new CommandListener(this, createCommandBuilder(), credentials.getGlobalPrefix());
            this.misc = new MiscListener(this);

            //Load the configuration for each guild
            jda.getGuilds().forEach(this::guild);

            jda.addEventListener(activity);
            jda.addEventListener(messages);
            jda.addEventListener(blacklist);
            jda.addEventListener(command);
            jda.addEventListener(misc);

            executor.schedule(activity, credentials.getActivityUpdateInterval(), TimeUnit.MINUTES);
        }catch(LoginException | InterruptedException e){
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //                                                                                                                //
    //   Visitor                                                                                                      //
    //                                                                                                                //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void accept(Visitor visitor){
        visitor.handle(this);
    }

    public interface Visitor extends
            ActivityListener.Visitor,
            BlacklistListener.Visitor,
            CommandListener.Visitor,
            InteractiveMessageListener.Visitor,
            MiscListener.Visitor,
            Configuration.Visitor{

        default void visit(int shardId, @Nonnull Shard shard){}

        default void traverse(int shardId, @Nonnull Shard shard) throws NullPointerException{
            Preconditions.checkNotNull(shard);
            shard.activity.accept(this);
            shard.blacklist.accept(this);
            shard.command.accept(this);
            shard.messages.accept(this);
            shard.misc.accept(this);
            //Force-load every configuration
            shard.jda().getGuilds().stream().map(shard::guild).forEach(configuration -> configuration.accept(this));
        }

        default void endVisit(int shardId, @Nonnull Shard shard){}

        default void handle(@Nonnull Shard shard) throws NullPointerException{
            Preconditions.checkNotNull(shard);
            int shardId = shard.jda().getShardInfo().getShardId();
            visit(shardId, shard);
            traverse(shardId, shard);
            endVisit(shardId, shard);
        }
    }
}
