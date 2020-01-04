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
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import net.dv8tion.jda.internal.utils.JDALogger;
import org.atteo.evo.inflector.English;
import org.slf4j.Logger;
import vartas.discord.bot.entities.Cluster;
import vartas.discord.bot.entities.Configuration;
import vartas.discord.bot.entities.Shard;
import vartas.discord.bot.internal.RemoveSubredditFeed;
import vartas.discord.bot.internal.SendSubmission;
import vartas.reddit.RedditSnowflake;
import vartas.reddit.Submission;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * This class deals with receiving new submissions from subreddits and posting
 * them in the specified channels.
 */
public class SubredditFeed implements Runnable{
    private final LoadingCache<String, SubmissionCache> cache;
    /**
     * The log for this class.
     */
    protected final Logger log = JDALogger.getLog(this.getClass().getSimpleName());
    /**
     * The runtime of the program.
     */
    protected final Cluster cluster;

    public SubredditFeed(Cluster cluster) throws NoSuchElementException {
        this.cache = CacheBuilder.newBuilder().expireAfterAccess(1, TimeUnit.HOURS).build(CacheLoader.from(SubmissionCache::new));
        this.cluster = cluster;
        log.debug("Reddit feeds created.");
    }

    @Override
    public void run() {
        log.info(String.format("Visiting %d %s.", cache.size(), English.plural("subreddit", (int)cache.size())));
        cluster.accept(new Update());
    }

    public void accept(Visitor visitor){
        visitor.handle(this);
    }

    public interface Visitor{
        default void visit(@Nonnull SubredditFeed subredditFeed){}
        default void traverse(@Nonnull SubredditFeed subredditFeed){}
        default void endVisit(@Nonnull SubredditFeed subredditFeed){}
        default void handle(@Nonnull SubredditFeed subredditFeed){
            visit(subredditFeed);
            traverse(subredditFeed);
            endVisit(subredditFeed);
        }
    }

    /**
     * Periodically, we retrieve the latest requested submissions from the individual subreddits. However,
     * we are not able to tell, which of the received submissions have already been posted. Hence why we
     * keep track of them temporarily in a cache.<br>
     * The cache has to be larger than the period during which new submissions are requested, otherwise we
     * risk posting duplicates.
     */
    @Nonnull
    private class SubmissionCache {
        /**
         *  Contains the most recent received submissions and
         *  their corresponding Discord message.
         */
        @Nonnull
        private final Cache<Submission, Submission> cache;
        /**
         * The subreddit associated with the cache.
         */
        @Nonnull
        private final String subredditName;

        /**
         * Creates a fresh submission cache. In order to avoid duplicates, the cache will store all submissions
         * that have been made in the past ten minutes.
         * @param subredditName the name of the subreddit associated with the cache
         */
        public SubmissionCache(@Nonnull String subredditName){
            this.subredditName = subredditName;
            //We only need a few minutes to avoid duplicates
            this.cache = CacheBuilder.newBuilder().expireAfterWrite(10, TimeUnit.MINUTES).build();
        }

        /**
         * Adds all submissions to the cache and updates duplicates.
         * @param submissions the newly requested submissions.
         * @return a list of submissions that have been newly added.
         */
        @Nonnull
        private List<Submission> update(@Nonnull Collection<Submission> submissions){
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
         * @param start the (exclusive) minimum age of the submissions
         * @param end the (exclusive) maximum age of the submissions
         * @return a sorted list of all new submission messages
         * @throws IllegalArgumentException if the subreddit is not available
         */
        @Nonnull
        public List<Submission> request(@Nonnull LocalDateTime start, @Nonnull LocalDateTime end) throws IllegalArgumentException{
            Collection<Submission> submissions = cluster.submission(subredditName, start, end).orElseThrow();
            List<Submission> result = update(submissions);
            result.sort(Comparator.comparing(RedditSnowflake::getCreated));
            return result;
        }
    }

    /**
     * This class represents one cycle of the runnable.
     * In each cycle, the latest submissions over all registered subreddits are posted and cached for the next
     * cycle.
     */
    @Nonnull
    private class Update implements Cluster.Visitor{
        /**
         * The internal cache of the cycle.
         * It associates all subreddits that are registered in at least one configuration file with all new submissions
         * that haven't been posted before.
         */
        @Nonnull
        private final Map<String, List<Submission>> localSubmissions = new HashMap<>();
        /**
         * The shard of the current configuration files.
         */
        @Nullable
        private Shard shard;
        /**
         * Visits all subreddits registered in the corresponding guild configuration.
         * If it is the first time the subreddit has been accessed during this cycle, the latest submissions are
         * requested and then cached. Otherwise the cached entries will be reused.<br>
         * This avoids the additional overhead, when requesting from the same subreddit multiple times.
         * @param configuration the configuration file of a Discord guild
         */
        @Override
        public void visit(@Nonnull Configuration configuration){
            configuration.resolve(Configuration.LongType.SUBREDDIT).forEach((subredditName, channelId) -> {
                try {
                    List<Submission> submissions = localSubmissions.computeIfAbsent(subredditName, this::receive);
                    send(submissions, configuration.getGuildId(), channelId);
                    if (submissions.size() > 0)
                        log.info(String.format("Posted %d new %s from r/%s in guild %d.", submissions.size(), English.plural("submission", submissions.size()), subredditName, configuration.getGuildId()));
                }catch(IllegalArgumentException e){
                    log.error(e.getMessage());
                    //Remove all channels linked to the subreddit
                    cluster.accept(new RemoveSubredditFeed(subredditName));
                    //"If any execution of the task encounters an exception, subsequent executions are suppressed" my ass
                }catch(Exception e){
                    log.error("Unhandled exception caught from "+subredditName, e);
                }
            });
        }

        /**
         * Stores the local shard as a reference for the individual configurations.
         * @param shard the current shard that is visited
         */
        @Override
        public void visit(@Nonnull Shard shard){
            this.shard = shard;
        }

        /**
         * Clears the local reference to the shard to avoid using it in a different shard.
         * @param shard the current shard that has been visited
         */
        @Override
        public void endVisit(@Nonnull Shard shard){
            this.shard = null;
        }

        /**
         * Requests all submissions that are at least one minute old, but not older than three minutes.
         * The associated cache is updated with the new submissions and returns those that haven't been received before.
         * @param subredditName the name of the subreddit the submissions are received from
         * @return all newly received submissions
         */
        @Nonnull
        private List<Submission> receive(@Nonnull String subredditName){
            LocalDateTime now = LocalDateTime.now(ZoneId.of("UTC"));
            //Submissions should be at least 1 minute old so that the author can flair them correctly
            LocalDateTime cacheEnd = now.minusMinutes(1);
            //Go back 2 minutes instead of 1 since we can't assume the interval to be exact
            LocalDateTime cacheStart = now.minusMinutes(3);
            return cache.getUnchecked(subredditName).request(cacheStart, cacheEnd);
        }

        /**
         * Sends the {@code submissions} in the specified {@code text channels}.
         * @param submissions the newly received submissions
         * @param guildId the guild id associated with the channel id
         * @param channelId the channel id of the channel the submission is posted in
         */
        private void send(@Nonnull List<Submission> submissions, long guildId, long channelId){
            for(Submission submission : submissions)
                send(submission, guildId, channelId);
        }

        /**
         * Sends the {@code submission} in the specified {@code text channel}.
         * @param submission the newly received submission
         * @param guildId the guild id associated with the channel id
         * @param channelId the channel id of the channel the submission is posted in
         * @throws NullPointerException if {@link #shard} is null
         */
        private void send(@Nonnull Submission submission, long guildId, long channelId) throws NullPointerException{
            Preconditions.checkNotNull(shard);
            shard.accept(new SendSubmission(submission, guildId, channelId));
        }
    }
}