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
import mpi.MPI;
import mpi.MPIException;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.internal.utils.JDALogger;
import org.slf4j.Logger;
import vartas.discord.bot.CommandBuilder;
import vartas.discord.bot.EntityAdapter;
import vartas.discord.bot.listener.*;
import vartas.discord.bot.mpi.MPIAdapter;
import vartas.discord.bot.mpi.MPIObserver;
import vartas.discord.bot.mpi.command.MPICommand;
import vartas.discord.bot.visitor.ShardVisitor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * This class represents the instance of this bot on a single shard.<br>
 * Each shard represents an isolated program, meaning that one shard is not aware of all other shards.<br>
 * Meaning that if information has to be shared across multiple shards, it has to be done via an external scope.
 */
public abstract class Shard extends MPIAdapter {
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
    private final LoadingCache<Long, Configuration> guilds;
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
     * For anyone but the master node, this instance should be null.
     * The slave nodes have to access the cluster over MPI via {@link #send(int, MPICommand, Serializable)}.
     */
    @Nullable
    private final Cluster cluster;

    /**
     * Initializes the MPI node, then the JDA on a single shard.<br>
     * The shard id is equivalent to the rank of the MPI node.
     * @param args the arguments passed to the executable
     * @throws MPIException if the MPI node couldn't be initialized
     */
    public Shard(@Nonnull String[] args) throws MPIException, NullPointerException {
        super(args);
        this.adapter = createEntityAdapter();
        this.jda = createJda();
        this.cluster = createCluster();

        Credentials credentials = adapter.credentials();

        this.rank = adapter.rank();
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
        mpi.scheduleAtFixedRate(new MPIObserver(this), 0, 1, TimeUnit.SECONDS);
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
        return guild(guild.getIdLong());
    }
    public Configuration guild(long guildId){
        return guilds.getUnchecked(guildId);
    }
    public void remove(Guild guild){
        remove(guild.getIdLong());
    }
    public void remove(long guildId){
        Configuration config = guild(guildId);
        //Delete file
        adapter.delete(config);
        //Remove pattern
        blacklist.remove(guildId);
    }
    private Configuration create(long guildId){
        Configuration configuration = adapter.configuration(guildId, this);

        blacklist.set(configuration);

        return configuration;
    }
    public Optional<Cluster> getCluster(){
        return Optional.ofNullable(cluster);
    }
    public void store(Configuration configuration, Guild context){
        adapter.store(configuration, context);
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
    public Runnable shutdown() throws MPIException {
        MPI.Finalize();
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
        getCluster().ifPresent(visitor::handle);
        guilds.asMap().values().forEach(visitor::handle);
        visitor.handle(rank);
    }

    protected abstract CommandBuilder createCommandBuilder();
    protected abstract EntityAdapter createEntityAdapter();
    protected abstract JDA createJda();
    protected abstract Cluster createCluster();

    @Override
    public <T extends Serializable> void send(int shardId, MPICommand<T> command, T object){
        mpi.submit(new MPISend<>(shardId, command, object));
    }

    private class MPISend<T extends Serializable> implements Runnable{
        private final int shardId;
        private final MPICommand<T> command;
        private final T object;

        private MPISend(int shardId, MPICommand<T> command, T object){
            this.shardId = shardId;
            this.command = command;
            this.object = object;
        }

        @Override
        public void run(){
            try{
                //Sending to oneself would cause a deadlock
                if(shardId == myRank)
                    command.accept(Shard.this, object);
                else
                    command.send(shardId, object);
            }catch(MPIException e){
                e.printStackTrace();
            }
        }
    }

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
}
