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

package vartas.discord.bot;

import com.google.common.base.Preconditions;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;
import com.google.common.collect.Tables;
import net.dv8tion.jda.internal.utils.JDALogger;
import org.atteo.evo.inflector.English;
import org.slf4j.Logger;
import vartas.discord.bot.entities.Cluster;
import vartas.discord.bot.internal.RemoveSubredditFeed;
import vartas.discord.bot.internal.SendSubmission;
import vartas.reddit.RedditSnowflake;
import vartas.reddit.Submission;

import javax.annotation.Nonnull;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * This class deals with receiving new submissions from subreddits and posting
 * them in the specified channels.
 */
public class RedditFeed implements Runnable{
    /**
     * A table containing all registered subreddit feeds.<br>
     * The {@code rows} are all registered <b>subreddits</b>, the {@code columns} are the <b>caches</b> associated
     * with the specified subreddits and the {@code values} are the registered channels and their respective guilds
     * associated with the given subreddit.<br>
     */
    private Table<String, SubmissionCache, Map<Long, Long>> subreddits = Tables.synchronizedTable(HashBasedTable.create());
    /**
     * The log for this class.
     */
    protected final Logger log = JDALogger.getLog(this.getClass().getSimpleName());
    /**
     * The runtime of the program.
     */
    protected final Cluster cluster;

    public RedditFeed(Cluster cluster) throws NoSuchElementException {
        this.cluster = cluster;
        log.debug("Reddit feeds created.");
    }

    public synchronized void add(String subredditName, long guildId, long channelId){
        //Create a new cache & map if the subreddit is registered for the first time
        if(!subreddits.containsRow(subredditName))
            subreddits.put(subredditName, new SubmissionCache(), Maps.newConcurrentMap());

        //We technically only have one cache per subreddit
        subreddits.rowMap().get(subredditName).values().forEach(map -> map.put(channelId, guildId));
        log.debug("Added subreddit '"+subredditName+"'.");
    }

    public synchronized void remove(String subredditName, long channelId){
        //Abort if the subreddit isn't registered
        if(!subreddits.containsRow(subredditName))
            return;

        //We technically only have one cache per subreddit
        subreddits.rowMap().get(subredditName).values().forEach(map -> map.remove(channelId));

        //If no more channels are left -> Remove the entry
        if(subreddits.rowMap().get(subredditName).isEmpty())
            subreddits.rowMap().remove(subredditName);
        log.debug("Removed subreddit '"+subredditName+"'.");
    }

    @Override
    public void run() {
        log.info(String.format("Visiting %d %s.", subreddits.size(), English.plural("subreddit", subreddits.size())));
        subreddits.cellSet().forEach(cell -> update(cell.getRowKey(), cell.getColumnKey(), cell.getValue()));
    }

    public void accept(Visitor visitor){
        visitor.handle(this);
    }

    public interface Visitor{
        default void visit(@Nonnull RedditFeed redditFeed){}

        default void traverse(@Nonnull RedditFeed redditFeed) {}

        default void endVisit(@Nonnull RedditFeed redditFeed){}

        default void handle(@Nonnull RedditFeed redditFeed) throws NullPointerException{
            Preconditions.checkNotNull(redditFeed);
            visit(redditFeed);
            traverse(redditFeed);
            endVisit(redditFeed);
        }
    }

    /**
     * Requests the latest submissions from the specified subreddit and posts all new submissions.
     * @param subredditName the name of the subreddit the submissions are received from
     * @param cache the submission cache associated with the subreddit
     * @param channels the channel ids of all channels the submissions are posted in
     */
    public void update(String subredditName, SubmissionCache cache, Map<Long, Long> channels){
        try {
            List<Submission> submissions = receive(subredditName, cache);
            send(submissions, channels);
            if (submissions.size() > 0)
                log.info(String.format("Posted %d new %s from r/%s.", submissions.size(), English.plural("submission", submissions.size()), subredditName));
        }catch(IllegalArgumentException e){
            log.error(e.getMessage());
            //Remove all channels linked to the subreddit
            channels.forEach((channelId, guildId) -> cluster.accept(new RemoveSubredditFeed(subredditName, guildId, channelId)));
        //"If any execution of the task encounters an exception, subsequent executions are suppressed" my ass
        }catch(Exception e){
            log.error("Unhandled exception caught from "+subredditName,e);
        }
    }

    /**
     * Requests all submissions that are at least one minute old, but not older than three minutes.
     * The associated cache is updated with the new submissions and returns those that haven't been received before.
     * @param subredditName the name of the subreddit the submissions are received from
     * @param cache the submission cache associated with the subreddit
     * @return all newly received submissions
     */
    private List<Submission> receive(String subredditName, SubmissionCache cache){
        LocalDateTime now = LocalDateTime.now(ZoneId.of("UTC"));
        //Submissions should be at least 1 minute old so that the author can flair them correctly
        LocalDateTime cacheEnd = now.minusMinutes(1);
        //Go back 2 minutes instead of 1 since we can't assume the interval to be exact
        LocalDateTime cacheStart = now.minusMinutes(3);
        return cache.request(subredditName, cacheStart, cacheEnd);
    }

    /**
     * Sends the {@code submissions} in the specified {@code text channels}.
     * @param submissions the newly received submissions
     * @param channels the channel ids of all channels the submission is posted in
     */
    private void send(List<Submission> submissions, Map<Long, Long> channels){
        for(Submission submission : submissions)
            send(submission, channels);
    }

    /**
     * Sends the {@code submission} in the specified {@code text channels}.
     * @param submission the newly received submission
     * @param channels the channel ids of all channels the submission is posted in
     */
    private void send(Submission submission, Map<Long, Long> channels){
        channels.forEach((channelId, guildId) -> send(submission, channelId, guildId));
    }

    /**
     * Sends the {@code submission} in the specified {@code text channel}.
     * @param submission the newly received submission
     * @param channelId the channel id of the channel the submission is posted in
     * @param guildId the guild id associated with the channel id
     */
    private void send(Submission submission, long channelId, long guildId){
        int shardId = cluster.getShardId(guildId);
        cluster.accept(new SendSubmission(shardId, guildId, channelId, submission));
    }

    /**
     * Periodically, we retrieve the latest requested submissions from the individual subreddits. However,
     * we are not able to tell, which of the received submissions have already been posted. Hence why we
     * keep track of them temporarily in a cache.<br>
     * The cache has to be larger than the period during which new submissions are requested, otherwise we
     * risk posting duplicates.
     */
    private class SubmissionCache {
        /**
         *  Contains the most recent received submissions and
         *  their corresponding Discord message.
         */
        private final Cache<Submission, Submission> cache;

        /**
         * Creates a fresh submission cache. In order to avoid duplicates, the cache will store all submissions
         * that have been made in the past ten minutes.
         */
        public SubmissionCache(){
            //We only need a few minutes to avoid duplicates
            this.cache = CacheBuilder.newBuilder().expireAfterWrite(10, TimeUnit.MINUTES).build();
        }

        /**
         * Adds all submissions to the cache and updates duplicates.
         * @param submissions the newly requested submissions.
         * @return a list of submissions that have been newly added.
         */
        private List<Submission> update(Collection<Submission> submissions){
            LinkedList<Submission> result = new LinkedList<>();

            for(Submission submission : submissions){
                if(!cache.asMap().containsKey(submission))
                    result.add(submission);
                cache.put(submission, submission);
            }
            return result;
        }

        /**
         * Requests submissions between the given intervals and stores them in the local cache.
         * @param subredditName the subreddit name associated with the cache
         * @param start the (exclusive) minimum age of the submissions
         * @param end the (exclusive) maximum age of the submissions
         * @return a sorted list of all new submission messages
         * @throws IllegalArgumentException if the subreddit is not available
         */
        public List<Submission> request(String subredditName, LocalDateTime start, LocalDateTime end) throws IllegalArgumentException{
            Collection<Submission> submissions = cluster.submission(subredditName, start, end).orElseThrow();
            List<Submission> result = update(submissions);
            result.sort(Comparator.comparing(RedditSnowflake::getCreated));
            return result;
        }
    }
}