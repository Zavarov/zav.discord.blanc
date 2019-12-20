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
import vartas.discord.bot.visitor.ShardVisitor;

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
public abstract class Shard {
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
     * The listener responsible for reacting to interactive messages and deleting them, when they haven't been
     * used for a arbitrary but fixed amount of time.
     */
    @Nonnull
    private final InteractiveMessageListener messages;
    /**
     * The activity tracker for all message.
     */
    @Nonnull
    private final ActivityListener activity;
    /**
     * The listener responsible for filtering all blacklisted words.
     */
    @Nonnull
    private BlacklistListener blacklist;
    /**
     * The listener responsible for parsing and scheduling the bot commands.
     */
    @Nonnull
    private CommandListener command;
    /**
     * The JDA over the current shard.
     */
    @Nonnull
    private final JDA jda;
    /**
     * All configuration files of the guilds in this shard.
     */
    @Nonnull
    private final LoadingCache<Guild, Configuration> guilds;
    /**
     * The adapter for parsing the local data files.
     */
    @Nonnull
    private final EntityAdapter adapter;
    /**
     * A reference to all user ranks.
     */
    @Nonnull
    private final Rank rank;
    /**
     * The cluster instance managing the global functionality.
     * All nodes share the same cluster.
     */
    @Nonnull
    private final Cluster cluster;

    /**
     * The credentials containing all login-information, as well as some constants.
     */
    @Nonnull
    private final Credentials credentials;

    /**
     * Initializes a fresh shard.
     * @param shardId the shard id.
     * @throws NullPointerException if {@code args} is null
     * @throws LoginException if the provided token is invalid
     * @throws InterruptedException if the program was interrupted while logging in
     */
    public Shard(int shardId) throws NullPointerException, LoginException, InterruptedException {
        this.adapter = createEntityAdapter();
        this.credentials = adapter.credentials();
        this.rank = adapter.rank();
        this.jda = createJda(shardId, credentials);
        this.cluster = createCluster();
        this.activity = new ActivityListener(jda, credentials.getActivityUpdateInterval());
        this.messages = new InteractiveMessageListener(credentials);
        this.blacklist = new BlacklistListener(this);
        this.command = new CommandListener(this, createCommandBuilder(), credentials.getGlobalPrefix());

        //
        this.guilds = CacheBuilder.newBuilder().expireAfterAccess(1, TimeUnit.HOURS).build(CacheLoader.from(this::create));

        //Load the configuration for each guild
        jda.getGuilds().forEach(this::guild);

        jda.addEventListener(activity);
        jda.addEventListener(messages);
        jda.addEventListener(blacklist);
        jda.addEventListener(command);
        jda.addEventListener(new MiscListener(this));

        executor.schedule(activity, credentials.getActivityUpdateInterval(), TimeUnit.MINUTES);
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
        new UnloadConfiguration(configuration).handle(getCluster());
    }
    private Configuration create(Guild guild){
        System.out.println(getCluster());
        System.out.println(cluster);
        Configuration configuration = adapter.configuration(guild, this);
        new LoadConfiguration(configuration).handle(getCluster());
        return configuration;
    }
    @Nonnull
    public Cluster getCluster(){
        return cluster;
    }
    public void store(Configuration configuration){
        adapter.store(configuration);
    }
    public void store(Rank rank){
        adapter.store(rank);
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
    public void accept(ShardVisitor visitor){
        visitor.handle(rank);
        visitor.handle(credentials);
        visitor.handle(activity);
        visitor.handle(blacklist);
        guilds.asMap().values().forEach(visitor::handle);
    }

    protected abstract CommandBuilder createCommandBuilder();
    protected abstract EntityAdapter createEntityAdapter();
    protected abstract JDA createJda(int shardId, Credentials credentials) throws LoginException, InterruptedException;
    protected abstract Cluster createCluster();

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
}
