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

package vartas.discord.entities;

import com.google.common.base.Preconditions;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.internal.utils.JDALogger;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import vartas.discord.Command;
import vartas.discord.CommandBuilder;
import vartas.discord.EntityAdapter;
import vartas.discord.listener.ActivityListener;
import vartas.discord.listener.InteractiveMessageListener;
import vartas.discord.listener.MiscListener;
import vartas.discord.message.InteractiveMessage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.security.auth.login.LoginException;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.regex.Pattern;

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
    private final LoadingCache<Guild, Configuration> guilds;
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
    private final BlacklistListener blacklist;
    /**
     * The listener responsible for parsing and scheduling the bot commands.
     */
    @Nonnull
    private final CommandListener command;
    /**
     * The listener for all miscellaneous events.
     */
    @Nonnull
    private final MiscListener misc;
    /**
     * The JDA over the current shard.
     */
    @Nonnull
    private final JDA jda;
    /**
     * The cluster instance managing the global functionality.
     * All nodes share the same cluster.
     */
    @Nonnull
    private final Cluster cluster;

    /**
     * Initializes a fresh shard.
     * @param shardId the shard id.
     */
    public Shard(int shardId, @Nonnull Credentials credentials, @Nonnull EntityAdapter adapter, @Nonnull Cluster cluster) throws LoginException, InterruptedException {
        this.cluster = cluster;
        this.jda = createJda(shardId, credentials);
        this.activity = new ActivityListener(jda, credentials.getActivityUpdateInterval());
        this.messages = new InteractiveMessageListener(credentials.getInteractiveMessageLifetime());
        this.blacklist = new BlacklistListener();
        this.command = new CommandListener(credentials.getGlobalPrefix());
        this.misc = new MiscListener(this);
        this.guilds = CacheBuilder.newBuilder().expireAfterAccess(1, TimeUnit.HOURS).build(CacheLoader.from(guild -> adapter.configuration(guild, this)));

        jda.addEventListener(activity);
        jda.addEventListener(messages);
        jda.addEventListener(blacklist);
        jda.addEventListener(command);
        jda.addEventListener(misc);

        executor.scheduleAtFixedRate(activity, 0, credentials.getActivityUpdateInterval(), TimeUnit.MINUTES);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //                                                                                                                //
    //   Internal                                                                                                     //
    //                                                                                                                //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * @return the cluster the shard belongs to
     * @deprecated This method is used for the time being, until the cluster is accessible via the visitor pattern
     */
    @Deprecated
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
    @Nonnull
    public Runnable shutdown() {
        jda.shutdown();
        executor.shutdown();
        log.info("Shutting down shard "+jda.getShardInfo().getShardString()+".");
        return () -> {
            try{
                executor.awaitTermination(5, TimeUnit.SECONDS);
            }catch(InterruptedException e){
                log.error(e.getMessage());
                executor.shutdownNow();
            }
        };
    }

    /**
     * Schedules the runnable to be executed and an unspecified point in time.
     * @param runnable the corresponding runnable
     */
    public void schedule(@Nonnull Runnable runnable){
        executor.submit(runnable);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //                                                                                                                //
    //   Discord                                                                                                      //
    //                                                                                                                //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Creates the builder for transforming the received messages into commands.
     * @return the command builder
     */
    @Nonnull
    protected abstract CommandBuilder createCommandBuilder();
    /**
     * Creates a new JDA instance for this shard.
     * @param shardId the id of this shard
     * @param credentials the credentials containing the login information
     * @return the JDA instance over this shard
     * @throws LoginException if the JDA couldn't log in
     * @throws InterruptedException if the JDA was interrupted during the login process
     */
    @Nonnull
    protected abstract JDA createJda(int shardId, @Nonnull Credentials credentials) throws LoginException, InterruptedException;
    /**
     * Queues an action.
     * @param action the corresponding action
     * @param <T> the respond type of the action
     */
    public <T> void queue(@Nonnull RestAction<T> action){
        queue(action, null);
    }
    /**
     * Queues an action.
     * @param action the corresponding action
     * @param success the consumer used upon success
     * @param <T> the respond type of the action
     */
    public <T> void queue(@Nonnull RestAction<T> action, @Nullable Consumer<? super T> success){
        queue(action, success, null);
    }

    /**
     * Queues an action.
     * @param action the corresponding action
     * @param success the consumer used upon success
     * @param failure the consumer used upon failure
     * @param <T> the respond type of the action
     */
    public <T> void queue(@Nonnull RestAction<T> action, @Nullable Consumer<? super T> success, @Nullable Consumer<? super Throwable> failure){
        action.queue(success, failure);
    }

    /**
     * Queues an interactive message.<br>
     * Upon completion, it will add the arrows for going through the different pages.
     * @param channel the target channel the message is sent in
     * @param interactiveMessage the interactive message
     */
    public void queue(@Nonnull MessageChannel channel, @Nonnull InteractiveMessage interactiveMessage){
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
    /**
     * The hook point for the visitor pattern.
     * @param visitor the visitor traversing through the status
     */
    public void accept(@Nonnull Visitor visitor){
        visitor.handle(this);
    }

    /**
     * The status visitor.
     */
    public interface Visitor extends ActivityListener.Visitor, InteractiveMessageListener.Visitor, MiscListener.Visitor, Configuration.Visitor{
        /**
         * The method that is invoked before the sub-nodes are handled.
         * @param jda the corresponding JDA
         */
        default void visit(@Nonnull JDA jda){}

        /**
         * The method that is invoked to handle all sub-nodes.
         * @param jda the corresponding JDA
         */
        default void traverse(@Nonnull JDA jda){}

        /**
         * The method that is invoked after the sub-nodes have been handled.
         * @param jda the corresponding JDA
         */
        default void endVisit(@Nonnull JDA jda){}

        /**
         * The top method of the JDA visitor, calling the remaining visitor methods.
         * The order in which the methods are called is
         * <ul>
         *      <li>visit</li>
         *      <li>traverse</li>
         *      <li>endvisit</li>
         * </ul>
         * @param jda the corresponding JDA
         */
        default void handle(@Nonnull JDA jda) {
            visit(jda);
            traverse(jda);
            endVisit(jda);
        }

        /**
         * The method that is invoked before the sub-nodes are handled.
         * @param shard the corresponding shard
         */
        default void visit(@Nonnull Shard shard){}

        /**
         * The method that is invoked to handle all sub-nodes.
         * @param shard the corresponding shard
         */
        default void traverse(@Nonnull Shard shard){
            this.handle(shard.jda);
            shard.activity.accept(this);
            this.handle(shard.blacklist);
            this.handle(shard.command);
            shard.messages.accept(this);
            shard.misc.accept(this);
            //Force-load every configuration
            shard.jda.getGuilds().stream().map(shard.guilds::getUnchecked).forEach(configuration -> configuration.accept(this));
        }

        /**
         * The method that is invoked after the sub-nodes have been handled.
         * @param shard the corresponding shard
         */
        default void endVisit(@Nonnull Shard shard){}

        /**
         * The top method of the shard visitor, calling the remaining visitor methods.
         * The order in which the methods are called is
         * <ul>
         *      <li>visit</li>
         *      <li>traverse</li>
         *      <li>endvisit</li>
         * </ul>
         * @param shard the corresponding shard
         */
        default void handle(@Nonnull Shard shard) {
            visit(shard);
            traverse(shard);
            endVisit(shard);
        }
        /**
         * The method that is invoked before the sub-nodes are handled.
         * @param commandListener the corresponding listener
         */
        default void visit(@Nonnull CommandListener commandListener){}

        /**
         * The method that is invoked to handle all sub-nodes.
         * @param commandListener the corresponding listener
         */
        default void traverse(@Nonnull CommandListener commandListener){}

        /**
         * The method that is invoked after the sub-nodes have been handled.
         * @param commandListener the corresponding listener
         */
        default void endVisit(@Nonnull CommandListener commandListener){}

        /**
         * The top method of the listener visitor, calling the remaining visitor methods.
         * The order in which the methods are called is
         * <ul>
         *      <li>visit</li>
         *      <li>traverse</li>
         *      <li>endvisit</li>
         * </ul>
         * @param commandListener the corresponding listener
         */
        default void handle(@Nonnull CommandListener commandListener){
            visit(commandListener);
            traverse(commandListener);
            endVisit(commandListener);
        }

        /**
         * The method that is invoked before the sub-nodes are handled.
         * @param blacklistListener the corresponding listener
         */
        default void visit(@Nonnull BlacklistListener blacklistListener){}

        /**
         * The method that is invoked to handle all sub-nodes.
         * @param blacklistListener the corresponding listener
         */
        default void traverse(@Nonnull BlacklistListener blacklistListener) {}

        /**
         * The method that is invoked after the sub-nodes have been handled.
         * @param blacklistListener the corresponding listener
         */
        default void endVisit(@Nonnull BlacklistListener blacklistListener){}

        /**
         * The top method of the listener visitor, calling the remaining visitor methods.
         * The order in which the methods are called is
         * <ul>
         *      <li>visit</li>
         *      <li>traverse</li>
         *      <li>endvisit</li>
         * </ul>
         * @param blacklistListener the corresponding listener
         */
        default void handle(@Nonnull BlacklistListener blacklistListener){
            visit(blacklistListener);
            traverse(blacklistListener);
            endVisit(blacklistListener);
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //                                                                                                                //
    //   Command Listener                                                                                             //
    //                                                                                                                //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * This listener is responsible for parsing the received messages and transforming them into commands,
     * whenever possible.
     */
    @Nonnull
    public class CommandListener extends ListenerAdapter {
        /**
         * The global prefix that is valid in all guilds and private messages.
         */
        @Nonnull
        private final String globalPrefix;
        /**
         * The log for this class
         */
        @Nonnull
        private final Logger log = JDALogger.getLog(this.getClass());
        /**
         * The builder for transforming the messages into commands.
         */
        @Nonnull
        private final CommandBuilder builder;

        /**
         * Initializes a fresh listener.
         * @param globalPrefix the global prefix
         * @throws NullPointerException if {@code shard}, {@code builder} or {@code globalPrefix} is null
         */
        public CommandListener(@Nonnull String globalPrefix) throws NullPointerException{
            this.builder = createCommandBuilder();
            this.globalPrefix = globalPrefix;
        }

        /**
         * A message was received on either a private channel or a guild channel.
         * @param event the corresponding event.
         * @throws NullPointerException if {@code event} is null
         */
        @Override
        public void onMessageReceived(@Nonnull MessageReceivedEvent event) throws NullPointerException{
            Preconditions.checkNotNull(event);
            User author = event.getAuthor();
            Message message = event.getMessage();

            //Ignore all bot messages
            if(author.isBot())
                return;
            //Return on success
            if(parseWithGlobalPrefix(message))
                return;
            //Check the guild prefix if inside a guild
            if (message.isFromGuild())
                parseWithGuildPrefix(message);
        }

        /**
         * Checks if the message starts with the global prefix. If so, the prefix-free message content is parsed.
         * @param message the received message
         * @return true if the message content starts with the global prefix
         * @throws NullPointerException if {@code message} is null
         */
        private boolean parseWithGlobalPrefix(@Nonnull Message message) throws NullPointerException{
            Preconditions.checkNotNull(message);
            String content = message.getContentRaw();

            if(content.startsWith(globalPrefix)){
                parse(StringUtils.removeStart(content, globalPrefix), message);
                return true;
            }else{
                return false;
            }
        }

        /**
         * Checks if the message starts with the guild prefix. If so, the prefix-free message content is parsed.
         * @param message the received message
         * @throws NullPointerException if {@code message} is null
         */
        private void parseWithGuildPrefix(@Nonnull Message message){
            Preconditions.checkNotNull(message);
            Guild guild = message.getGuild();
            Configuration configuration = guilds.getUnchecked(guild);
            String prefix = configuration.getPrefix().orElse(null);
            String content = message.getContentRaw();

            if(prefix != null && content.startsWith(prefix))
                parse(StringUtils.removeStart(content, prefix), message);
        }

        /**
         * Parses the prefix-free message content and schedules it, upon success.<br>
         * In case of an error, the error message is posted in the same channel the message was received in.
         * @param prefixFreeContent the prefix-free message content
         * @param message the received message
         * @throws NullPointerException if {@code prefixFreeContent} or {@code message} is null
         */
        private void parse(@Nonnull String prefixFreeContent, @Nonnull Message message) throws NullPointerException{
            Preconditions.checkNotNull(prefixFreeContent);
            Preconditions.checkNotNull(message);
            MessageChannel channel = message.getChannel();
            try {
                Command command = builder.build(prefixFreeContent, message);
                schedule(() -> new CommandWrapper(command, channel).accept(message, Shard.this));
                log.info("Executed "+command.getClass().getSimpleName());
            }catch(RuntimeException e){
                e.printStackTrace();
                String errorMessage = e.toString();
                log.error(errorMessage);
                queue(channel.sendMessage(errorMessage));
            }
        }
    }

    /**
     * This class is wrapped around the parsed command and will catch any instance of {@link Throwable}.
     * The caught error message is sent to the channel, to notify the user that something went wrong,
     * then the error is rethrown.
     */
    @Nonnull
    private static class CommandWrapper {
        /**
         * The parsed command.
         */
        @Nonnull
        private final Command command;
        /**
         * The channel the command is sent in.
         */
        @Nonnull
        private final MessageChannel channel;

        /**
         * Creates a fresh wrapper.
         * @param command the command
         * @param channel the target channel
         * @throws NullPointerException if {@code command} or {@code channel} is null
         */
        public CommandWrapper(@Nonnull Command command, @Nonnull MessageChannel channel) throws NullPointerException{
            this.command = command;
            this.channel = channel;
        }

        /**
         * Attempts to execute the command.
         * Upon failure, the error message is sent to the target channel instead, then the error is rethrown.
         */
        public void accept(Message message, Shard shard){
            try{
                command.accept(message, shard);
            }catch(Throwable e){
                e.printStackTrace();
                String errorMessage = e.toString();
                shard.queue(channel.sendMessage(errorMessage));
                throw e;
            }
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //                                                                                                                //
    //   Blacklist Listener                                                                                           //
    //                                                                                                                //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * This listener applies the blacklist pattern on all received guild messages.
     * If a message matches the pattern, the program will attempt to remove it automatically.
     * An exception to this are messages sent by this program, which will be ignored.
     */
    @Nonnull
    public class BlacklistListener extends ListenerAdapter {
        /**
         * Checks if the message contains any blacklisted words.
         * If the message was sent from the author, nothing happens. Otherwise the pattern from the configuration
         * is retrieved, to assure that it isn't outdated, and compared to the message content.
         * On a match, the message is attempted to be deleted.
         * @param event the corresponding event.
         */
        @Override
        public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event){
            Message message = event.getMessage();
            SelfUser self = event.getJDA().getSelfUser();
            User author = event.getAuthor();
            //Ignore everything this bot posts
            if(self.equals(author))
                return;

            Configuration configuration = guilds.getUnchecked(event.getGuild());
            Optional<Pattern> patternOpt = configuration.getPattern();
            //Delete the message on a match
            patternOpt.ifPresent(pattern -> {
                if(pattern.matcher(message.getContentRaw()).matches())
                    queue(message.delete());
            });
        }
    }
}
