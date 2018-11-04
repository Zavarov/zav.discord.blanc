/*
 * Copyright (C) 2017 u/Zavarov
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

package vartas.discordbot.threads;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import vartas.xml.XMLServer;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.dean.jraw.http.NetworkException;
import net.dean.jraw.models.Submission;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.exceptions.ErrorResponseException;
import net.dv8tion.jda.core.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.core.requests.ErrorResponse;
import net.dv8tion.jda.core.utils.JDALogger;
import org.apache.http.HttpStatus;
import org.atteo.evo.inflector.English;
import org.slf4j.Logger;
import vartas.discordbot.DiscordBot;
import vartas.discordbot.messages.SubmissionMessage;
import vartas.reddit.RedditBot;
import vartas.reddit.SubmissionWrapper;

/**
 * This class deals with receiving new submissions from subreddits and posting
 * them in the specified channels.
 * @author u/Zavarov
 */
public class RedditFeed implements Runnable, Killable{
    /**
     * A submission needs to be at least 1 minute old before it gets posted.
     */
    protected static final long MINIMUM_SUBMISSION_AGE = 60000;
    /**
     * The executor that calls this object every minute.
     */
    protected final ScheduledExecutorService executor;
    /**
     * The executor that deals with all the errors that occur during the execution.
     */
    protected final ExecutorService error_handler = Executors.newSingleThreadExecutor();
    /**
     * The entitiy that is connected to Reddit.
     */
    protected final RedditBot reddit;
    /**
     * The log for this class.
     */
    protected final Logger log = JDALogger.getLog(this.getClass().getSimpleName());
    /**
     * The subreddits and the textchannel we post new submissions in.
     */
    protected final Multimap<String,TextChannel> posts = HashMultimap.create();
    /**
     * The timestamp of the latest submission in for each subreddit.
     */
    protected final Map<String,Long> history = new Object2LongOpenHashMap<>();
    /**
     * The requester for the submissions.
     */
    protected SubmissionWrapper wrapper;
    /**
     * The function that returns the bot instance for every guild.
     */
    protected Function<Guild, DiscordBot> server;
    /**
     * @param reddit the entity connected to Reddit.
     * @param server the function that provides the bot instance for every guild.
     */
    public RedditFeed(RedditBot reddit, Function<Guild, DiscordBot> server){
        this.reddit = reddit;
        this.server = server;
        wrapper = new SubmissionWrapper(reddit);
        log.info("Reddit feeds created.");
        
        executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(RedditFeed.this, 1, 1, TimeUnit.MINUTES);
    }
    /**
     * Adds all textchannel in the specified guild that are marked in the configuration file.
     * @param server the server file for the guild.
     * @param guild the guild.
     */
    public synchronized void addSubreddits(XMLServer server, Guild guild){
        server.getRedditFeeds(guild).forEach( (s,t) -> {
            posts.put(s,t);
            //The history starts from this point onwards
            history.put(s,System.currentTimeMillis());
        });
        log.info(String.format("Added the subreddits of the guild %s.",guild.getName()));
    }
    /**
     * Adds a new textchannel to the list of feeds. New submission will be posted there.
     * @param subreddit the subreddit.
     * @param channel the textchannel.
     */
    public synchronized void addFeed(String subreddit, TextChannel channel){
        //Initializes a timestamp
        history.computeIfAbsent(subreddit, t -> System.currentTimeMillis());
        posts.put(subreddit, channel);
    }
    /**
     * Removes a textchannel from the list of feeds. New submissions won't be posted there anymore.
     * @param subreddit the subreddit.
     * @param channel the textchannel.
     */
    public synchronized void removeFeed(String subreddit, TextChannel channel){
        posts.remove(subreddit,channel);
        //Also remove the timestamp, if this was the last channel linked to the subreddit
        if(!posts.containsKey(subreddit)){
            history.remove(subreddit);
        }
        //Update the XML file
        DiscordBot bot = server.apply(channel.getGuild());
        XMLServer server = bot.getServer(channel.getGuild());
        server.removeRedditFeed(subreddit, channel);
        bot.updateServer(channel.getGuild());
    }
    /**
     * Generates all the messages for the latest submissions.
     * @param subreddit the subreddit the new submissions are retrieved from.
     * @return all submissions that have been submitted since the last time.
     */
    public synchronized Map<Submission,MessageBuilder> generateMessages(String subreddit){
        List<Submission> submissions = requestSubmissions(subreddit);
        //Update the timestamp of the newest submission
        if(!submissions.isEmpty()){
            history.put(subreddit, submissions.get(0).getCreated().getTime());
        }
        
        //Create a message for each submission
        Function<Submission,Submission> key = Function.identity();
        Function<Submission,MessageBuilder> value = s -> SubmissionMessage.create(s);
        BinaryOperator<MessageBuilder> error = (u,v) -> {throw new IllegalStateException();};
        return Lists.reverse(submissions)
                .stream()
                .collect(Collectors.toMap(key,value,error, LinkedHashMap::new));
    }
    /**
     * Retrieves the most recent submissions in the specified subreddit and
     * deals with all errors that might happen in the process.
     * @param subreddit the subreddit the new submissions are retrieved from.
     * @return all submissions that have been submitted since the last time.
     */
    public synchronized List<Submission> requestSubmissions(String subreddit){
        //Start at the oldest submission we know of.
        long start = history.computeIfAbsent(subreddit, (k) -> System.currentTimeMillis());
        //Only accept posts that are older than the minimal age
        long end = System.currentTimeMillis() - MINIMUM_SUBMISSION_AGE;
        wrapper.parameter(subreddit, new Date(start), new Date(end));
        
        try{
            return wrapper.request();
        }catch(NetworkException e){
            int error = e.getRes().getCode();
            //The subreddit either doesn't exist anymore or can't be accessed
            if(error == HttpStatus.SC_FORBIDDEN || error == HttpStatus.SC_NOT_FOUND){
                posts.get(subreddit).forEach(channel -> {
                    error_handler.submit(new Error(subreddit,channel,e));
                });
            }
            return Lists.newArrayList();
        }
    }
    /**
     * Posts all new submissions that have been posted since the last iteration.
     */
    @Override
    public synchronized void run(){
        try{
        posts.asMap().forEach( (subreddit,textchannels) -> {
            Map<Submission,MessageBuilder> messages = generateMessages(subreddit);
            textchannels.forEach(channel -> {
                messages.entrySet().stream()
                        .filter(e -> channel.isNSFW() || !SubmissionMessage.isNsfw(e.getKey()))
                        .map(e -> e.getValue())
                        .forEach(message -> {
                            try{
                                DiscordBot.sendMessage(channel,message,null, new Error(subreddit, channel));
                            }catch(InsufficientPermissionException permission){
                                log.info(String.format("Removed the channel %s due to a lack of permission.", channel.getName()));
                                error_handler.submit(new Error(subreddit,channel,permission));
                            }
                        });
            });
            log.info(String.format("Posted %d new %s from r/%s",messages.size(),English.plural("submission", messages.size()),subreddit));
        });
        //Just in case we forgot to check something
        }catch(RuntimeException e){
            log.error("Unexpected error encountered.", e);
        }
    }
    /**
     * Terminates all executors.
     */
    @Override
    public void shutdown() {
        executor.shutdownNow();
        error_handler.shutdown();
    }
    /**
     * A internal class for when a message wasn't sent successfully.
     */
    private class Error implements Consumer<Throwable>, Runnable{
        /**
         * The channel we tried to send a message in.
         */
        protected final TextChannel channel;
        /**
         * The subreddit we're currently in.
         */
        protected final String subreddit;
        /**
         * The error that caused this.
         */
        protected final Throwable throwable;
        /**
         * @param subreddit the subreddit we received the posts from.
         * @param channel the channel we tried to send a message in.
         */
        public Error(String subreddit, TextChannel channel){
            this(subreddit,channel,null);
        }
        /**
         * @param subreddit the subreddit we received the posts from.
         * @param channel the channel we tried to send a message in.
         * @param throwable the error that caused this.
         */
        public Error(String subreddit, TextChannel channel, Throwable throwable){
            this.channel = channel;
            this.subreddit = subreddit;
            this.throwable = throwable;
        }
        /**
         * When the channel doesn't exist anymore or if the bot lacks the permission to post it the channel,
         * it'll be removed from the map.
         * @param t the exception that has been thrown.
         */
        @Override
        public void accept(Throwable t){
            if(t instanceof ErrorResponseException){
                ErrorResponse error = ((ErrorResponseException)t).getErrorResponse();
                if(error == ErrorResponse.UNKNOWN_CHANNEL || error == ErrorResponse.UNKNOWN_GUILD){
                    removeFeed(subreddit,channel);
                }
            }else if(t instanceof InsufficientPermissionException){
                removeFeed(subreddit,channel);
            }else if(t instanceof NetworkException){
                removeFeed(subreddit,channel);
            }
            log.warn(t.getMessage());
        }
        /**
         * Executes the consumer with the specified throwable
         */
        @Override
        public void run() {
            accept(throwable);
        }
    }
}