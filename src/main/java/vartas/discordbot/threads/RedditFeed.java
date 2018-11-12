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
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import java.io.IOException;
import java.time.Instant;
import vartas.xml.XMLServer;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
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
import vartas.discordbot.comm.Environment;
import vartas.discordbot.messages.SubmissionMessage;

/**
 * This class deals with receiving new submissions from subreddits and posting
 * them in the specified channels.
 * @author u/Zavarov
 */
public class RedditFeed implements Runnable, Killable{
    /**
     * The executor that calls this object every minute.
     */
    protected final ScheduledExecutorService executor;
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
     * The runtime of the program.
     */
    protected final Environment environment;
    /**
     * @param environment the runtime of the program.
     */
    public RedditFeed(Environment environment){
        
        this.environment = environment;
        executor = Executors.newSingleThreadScheduledExecutor(
            new ThreadFactoryBuilder().setNameFormat("Reddit Feed Executor").build());
        executor.scheduleAtFixedRate(RedditFeed.this, 1, 1, TimeUnit.MINUTES);
        log.info("Reddit feeds created.");
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
            history.computeIfAbsent(s,k -> System.currentTimeMillis());
            log.info(String.format("Added the subreddit %s for the guild %s.",s,guild.getName()));
        });
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
    }
    /**
     * @param subreddit a subreddit.
     * @param channel a textchannel.
     * @return true if the (subreddit, channel) pair can receive new submissions.
     */
    public boolean containsFeed(String subreddit, TextChannel channel){
        return posts.containsEntry(subreddit, channel);
    }
    /**
     * Generates all the messages for the latest submissions.
     * @param subreddit the subreddit the new submissions are retrieved from.
     * @return all submissions that have been submitted since the last time.
     */
    public synchronized Map<Submission,MessageBuilder> generateMessages(String subreddit){
        Instant end = Instant.now().minusSeconds(60);
        Instant start = Instant.ofEpochMilli(history.computeIfAbsent(subreddit, (k) -> System.currentTimeMillis()));
        List<Submission> submissions;
        //
        try{
            submissions = environment.submission(subreddit, start, end);
            /*
            RedditBot bot = new RedditBot(environment.credentials(),environment.adapter());
            submissions = bot.getClient().subreddit(subreddit)
                .posts()
                .sorting(SubredditSort.NEW)
                .limit(Paginator.RECOMMENDED_MAX_LIMIT)
                .timePeriod(TimePeriod.HOUR)
                .build();
            */
        }catch(NetworkException e){
            int error = e.getRes().getCode();
            //The subreddit either doesn't exist anymore or can't be accessed
            if(error == HttpStatus.SC_FORBIDDEN || error == HttpStatus.SC_NOT_FOUND){
                posts.get(subreddit).forEach(channel -> {
                    environment.comm(channel).submit(new ErrorHandling(subreddit,channel,e));
                });
            }else{
                log.warn(e.getMessage());
            }
            submissions = Lists.newArrayList();
        }
        
        //Update the timestamp of the newest submission
        if(!submissions.isEmpty()){
            history.put(subreddit, submissions.get(0).getCreated().getTime());
        }
        
        //Create a message for each submission
        return Lists.reverse(submissions)
                .stream()
                .collect(Collectors.toMap(
                        k->k,
                        SubmissionMessage::create,
                        (u,v) -> {throw new IllegalStateException("Two identical submissions encountered.");}, 
                        LinkedHashMap::new));
    }
    /**
     * Posts all new submissions that have been posted since the last iteration.
     */
    @Override
    public synchronized void run(){
        try{
            //All subreddits and the channels submissions are posted in.
            posts.asMap().forEach((subreddit,textchannels) -> {
                //Submissions from a single subreddit
                Map<Submission,MessageBuilder> messages = generateMessages(subreddit);
                //Post in every channel
                textchannels.forEach(channel -> {
                    //Post every message each
                    messages.entrySet().stream()
                        .filter(e -> channel.isNSFW() || !SubmissionMessage.isNsfw(e.getKey()))
                        .map(e -> e.getValue())
                        .forEach(message -> {
                            try{
                                environment.comm(channel).send(channel, message, null, new ErrorHandling(subreddit, channel));
                            }catch(InsufficientPermissionException permission){
                                log.info(String.format("Removed the channel %s due to a lack of permission.", channel.getName()));
                                environment.comm(channel).submit(new ErrorHandling(subreddit,channel,permission));
                            }
                        });
                });
                log.info(String.format("Posted %d new %s from r/%s",messages.size(),English.plural("submission", messages.size()),subreddit));
            });
        //Just in case we forgot to check something
        }catch(Exception e){
            log.error("Unexpected error encountered.", e);
        }
    }
    /**
     * Terminates all executors.
     */
    @Override
    public void shutdown() {
        executor.shutdownNow();
        log.info("Reddit feed shut down.");
    }
    /**
     * A internal class for when a message wasn't sent successfully.
     */
    public class ErrorHandling implements Consumer<Throwable>, Runnable{
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
        public ErrorHandling(String subreddit, TextChannel channel){
            this(subreddit,channel,null);
        }
        /**
         * @param subreddit the subreddit we received the posts from.
         * @param channel the channel we tried to send a message in.
         * @param throwable the error that caused this.
         */
        public ErrorHandling(String subreddit, TextChannel channel, Throwable throwable){
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
            boolean update = false;
            try{
                if(t instanceof ErrorResponseException){
                    ErrorResponse error = ((ErrorResponseException)t).getErrorResponse();
                    if(error == ErrorResponse.UNKNOWN_CHANNEL || error == ErrorResponse.UNKNOWN_GUILD){
                        removeFeed(subreddit,channel);
                        update = true;
                    }
                }else if(t instanceof InsufficientPermissionException){
                    removeFeed(subreddit,channel);
                    update = true;
                }else if(t instanceof NetworkException){
                    removeFeed(subreddit,channel);
                    update = true;
                }
                //only update when something changed
                if(update){
                    environment.comm(channel.getGuild()).server(channel.getGuild()).removeRedditFeed(subreddit, channel);
                    environment.comm(channel.getGuild()).update(channel.getGuild());
                }
                log.warn(t.getMessage());
            //Couldn't remove feeds
            }catch(IOException | InterruptedException e){
                log.error(e.getMessage());
            }
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