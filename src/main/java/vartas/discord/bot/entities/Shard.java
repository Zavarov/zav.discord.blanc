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

import javax.annotation.Nonnull;
import javax.security.auth.login.LoginException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * This class represents the instance of this bot on a single shard.<br>
 * Each shard represents an isolated program, meaning that one shard is not aware of all other shards.<br>
 * Meaning that if information has to be shared across multiple shards, it has to be done via an external scope.<br>
 * Note that the constructor does not load any configuration. This has be done separately via visitors.
 * The separation of construction and initialization was taken in order to make it easier adapting to custom changes
 * to the shard. Rather than several methods, we only need a single visitor to load all elements.
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
    /**
     * The adapter for loading the guild configurations
     */
    private EntityAdapter adapter;

    /**
     * Initializes a fresh shard.
     * @param shardId the shard id.
     */
    public Shard(int shardId, Credentials credentials, EntityAdapter adapter, Cluster cluster) throws LoginException, InterruptedException {
        this.adapter = adapter;
        this.cluster = cluster;
        this.jda = createJda(shardId, credentials);
        this.activity = new ActivityListener(jda, credentials.getActivityUpdateInterval());
        this.messages = new InteractiveMessageListener(credentials.getInteractiveMessageLifetime());
        this.blacklist = new BlacklistListener(this);
        this.command = new CommandListener(this, createCommandBuilder(), credentials.getGlobalPrefix());
        this.misc = new MiscListener(this);

        jda.addEventListener(activity);
        jda.addEventListener(messages);
        jda.addEventListener(blacklist);
        jda.addEventListener(command);
        jda.addEventListener(misc);

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
    public Configuration guild(@Nonnull Guild guild){
        return guilds.getUnchecked(guild);
    }
    public void remove(@Nonnull Guild guild){
        Configuration configuration = guild(guild);
        getCluster().accept(new UnloadConfiguration(this, configuration));
    }
    @Nonnull
    public Cluster getCluster(){
        return cluster;
    }
    private Configuration create(@Nonnull Guild guild){
        Configuration configuration = adapter.configuration(guild, this);
        getCluster().accept(new LoadConfiguration(this, configuration));
        return configuration;
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
    //   Visitor                                                                                                      //
    //                                                                                                                //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void accept(Visitor visitor){
        visitor.handle(this);
    }

    public static class ConfigurationVisitor implements Visitor{
        private Visitor realThis = this;
        @Override
        public void setRealThis(Visitor realThis){
            this.realThis = realThis;
        }
        @Override
        public Visitor getRealThis(){
            return realThis;
        }
        @Override
        public void traverse(@Nonnull Shard shard){
            //Force-load every configuration
            shard.jda.getGuilds().stream().map(shard::guild).forEach(configuration -> configuration.accept(getRealThis()));
        }
    }

    public static class ShardVisitor implements Visitor{
        private Visitor realThis = this;
        @Override
        public void setRealThis(Visitor realThis){
            this.realThis = realThis;
        }
        @Override
        public Visitor getRealThis(){
            return realThis;
        }
        @Override
        public void traverse(@Nonnull Shard shard){
            getRealThis().handle(shard.jda);
            shard.activity.accept(getRealThis());
            shard.blacklist.accept(getRealThis());
            shard.command.accept(getRealThis());
            shard.messages.accept(getRealThis());
            shard.misc.accept(getRealThis());
        }
    }

    public abstract static class VisitorDelegator implements Visitor{
        private ShardVisitor shardVisitor;
        private ConfigurationVisitor configurationVisitor;
        private Visitor realThis = this;

        @Override
        public void setRealThis(Visitor realThis){
            this.realThis = realThis;
            if(configurationVisitor != null && configurationVisitor != getRealThis())
                configurationVisitor.setRealThis(realThis);
            if(shardVisitor != null && shardVisitor != getRealThis())
                shardVisitor.setRealThis(realThis);
        }

        @Override
        public Visitor getRealThis(){
            return realThis;
        }

        public void setShardVisitor(ShardVisitor shardVisitor){
            this.shardVisitor = shardVisitor;
            this.shardVisitor.setRealThis(getRealThis());
        }

        public void setConfigurationVisitor(ConfigurationVisitor configurationVisitor){
            this.configurationVisitor = configurationVisitor;
            this.configurationVisitor.setRealThis(getRealThis());
        }

        public void visit(@Nonnull Shard shard){
            if(shardVisitor != null && shardVisitor != getRealThis())
                shardVisitor.visit(shard);
            if(configurationVisitor != null && configurationVisitor != getRealThis())
                configurationVisitor.visit(shard);
        }

        public void traverse(@Nonnull Shard shard){
            if(shardVisitor != null && shardVisitor != getRealThis())
                shardVisitor.traverse(shard);
            if(configurationVisitor != null && configurationVisitor != getRealThis())
                configurationVisitor.traverse(shard);
        }

        public void endVisit(@Nonnull Shard shard){
            if(shardVisitor != null && shardVisitor != getRealThis())
                shardVisitor.endVisit(shard);
            if(configurationVisitor != null && configurationVisitor != getRealThis())
                configurationVisitor.endVisit(shard);
        }
    }

    /**
     * The frame of the shard visitor. By default, this visitor will only visit the shard and not its entries.<br>
     */
    public interface Visitor extends ActivityListener.Visitor, BlacklistListener.Visitor, CommandListener.Visitor, InteractiveMessageListener.Visitor, MiscListener.Visitor, Configuration.Visitor{
        default void setRealThis(Visitor realThis){
            throw new UnsupportedOperationException();
        }
        default Visitor getRealThis(){
            throw new UnsupportedOperationException();
        }

        default void visit(@Nonnull JDA jda){}
        default void traverse(@Nonnull JDA jda){}
        default void endVisit(@Nonnull JDA jda){}
        default void handle(@Nonnull JDA jda) {
            visit(jda);
            traverse(jda);
            endVisit(jda);
        }

        default void visit(@Nonnull Shard shard){}
        default void traverse(@Nonnull Shard shard){}
        default void endVisit(@Nonnull Shard shard){}
        default void handle(@Nonnull Shard shard) {
            visit(shard);
            traverse(shard);
            endVisit(shard);
        }
    }
}
