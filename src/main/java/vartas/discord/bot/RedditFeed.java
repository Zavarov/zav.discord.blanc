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

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import net.dv8tion.jda.internal.utils.JDALogger;
import org.atteo.evo.inflector.English;
import org.slf4j.Logger;
import vartas.discord.bot.entities.Cluster;
import vartas.discord.bot.entities.Shard;
import vartas.discord.bot.mpi.command.MPISendSubmission;
import vartas.discord.bot.mpi.serializable.MPISubmission;
import vartas.discord.bot.visitor.RedditFeedVisitor;
import vartas.reddit.RedditSnowflake;
import vartas.reddit.Submission;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * This class deals with receiving new submissions from subreddits and posting
 * them in the specified channels.
 */
public class RedditFeed implements Runnable{
    private Map<String, SubredditFeed> subreddits = Maps.newConcurrentMap();
    /**
     * The log for this class.
     */
    protected final Logger log = JDALogger.getLog(this.getClass().getSimpleName());
    /**
     * The runtime of the program.
     */
    protected final Cluster cluster;
    protected final Shard shard;
    /**
     * @param shard the runtime of the program.
     */
    public RedditFeed(Cluster cluster, Shard shard) throws NoSuchElementException {
        this.shard = shard;
        this.cluster = cluster;
        log.debug("Reddit feeds created.");
    }

    public void accept(RedditFeedVisitor visitor){
        for(Map.Entry<String,SubredditFeed> entry : subreddits.entrySet())
            visitor.handle(entry.getKey(), entry.getValue());
    }

    public void add(String subreddit){
        log.debug("Added subreddit '"+subreddit+"'.");
        subreddits.putIfAbsent(subreddit, new SubredditFeed(subreddit));
    }

    public void remove(String subreddit){
        log.debug("Removed subreddit '"+subreddit+"'.");
        Optional.ofNullable(subreddits.getOrDefault(subreddit, null)).ifPresent(subredditFeed -> subredditFeed.channels.forEach(subredditFeed::remove));
    }

    @Override
    public void run() {
        log.info(String.format("Visiting %d %s.", subreddits.size(), English.plural("subreddit", subreddits.size())));
        subreddits.values().forEach(SubredditFeed::update);
    }

    public class SubredditFeed {
        /**
         * The log for this class.
         */
        protected final Logger log = JDALogger.getLog(this.getClass().getSimpleName());
        /**
         * The subreddit that is covered by this feed.
         */
        protected String subreddit;
        /**
         * The underlying cache of previously received submissions.
         */
        protected SubmissionCache cache;
        /**
         * All text channels new submissions are posted in.<br>
         * In order to avoid looking through all configuration files during each iteration,
         * the set should be a life view of all registered channels.<br>
         * Meaning that every time the configuration files are updated, the change should
         * be registered in this feed as well.
         */
        protected Multimap<Long, Long> channels = Multimaps.synchronizedSetMultimap(HashMultimap.create());

        public SubredditFeed(String subreddit){
            this.subreddit = subreddit;
            this.cache = new SubmissionCache(subreddit);
        }

        public void add(long guildId, long channelId){
            channels.put(guildId, channelId);
            log.debug(String.format("Channel '%d' from guild '%d' added for subreddit '%s'.", channelId, guildId, subreddit));
        }

        public void remove(long guildId, long channelId){
            channels.remove(guildId, channelId);
            //Remove the entire feed if no more channels remain
            if(channels.isEmpty()) {
                //Remove from the map to not end in an infinite loop w/ remove(subreddit) calling remove(guildId, channelId)
                subreddits.remove(subreddit);
            }
            log.debug(String.format("Channel '%d' from guild '%d' removed for subreddit '%s'.", channelId, guildId, subreddit));
        }

        public void update(){
            try{
                List<Submission> submissions = receive();

                log.debug(String.format("%d new %s in r/%s.", submissions.size(), English.plural("submission", submissions.size()), subreddit));

                send(submissions);

                if(submissions.size() > 0)
                    log.info(String.format("Posted %d new %s from r/%s.", submissions.size(), English.plural("submission", submissions.size()),subreddit));
                //"If any execution of the task encounters an exception, subsequent executions are suppressed" my ass
            }catch(Exception t){
                log.error("Unhandled exception caught from "+subreddit,t);
            }
        }

        private List<Submission> receive(){
            LocalDateTime now = LocalDateTime.now(ZoneId.of("UTC"));
            //Submissions should be at least 1 minute old so that the author can flair them correctly
            LocalDateTime cacheEnd = now.minusMinutes(1);
            //Go back 2 minutes instead of 1 since we can't assume the interval to be exact
            LocalDateTime cacheStart = now.minusMinutes(3);

            return cache.request(cacheStart, cacheEnd);
        }

        private void send(List<Submission> submissions){
            for(Submission submission : submissions)
                send(submission);
        }

        private void send(Submission submission){
            channels.forEach((guildId, channelId) -> send(submission, guildId, channelId));
        }

        private void send(Submission submission, long guildId, long channelId){
            int shardId = shard.getShardId(guildId);
            MPISendSubmission.MPISendCommand command = MPISendSubmission.createSendCommand(shardId);
            MPISubmission object = new MPISubmission(submission, guildId, channelId);

            shard.send(command, object);
        }
    }

    public class SubmissionCache {
        /**
         * The log for this class.
         */
        protected final Logger log = JDALogger.getLog(this.getClass().getSimpleName());
        /**
         *  Contains the most recent received submissions and
         *  their corresponding Discord message.
         */
        protected Cache<Submission, Submission> cache;
        /**
         * The subreddit the cache requests from.
         */
        protected String subreddit;

        /**
         * @param subreddit the subreddit submissions are requested from.
         */
        public SubmissionCache(String subreddit){
            //We only need a few minutes to avoid duplicates
            this.cache = CacheBuilder.newBuilder().expireAfterWrite(10, TimeUnit.MINUTES).build();
            this.subreddit = subreddit;
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
         * @param start the (exclusive) minimum age of the submissions.
         * @param end the (exclusive) maximum age of the submissions.
         * @return a sorted list of all new submission messages.
         */
        public List<Submission> request(LocalDateTime start, LocalDateTime end){
            try{
                log.debug(String.format("requesting [%s, %s] from '%s'", start, end, subreddit));
                Collection<Submission> submissions = cluster.submission(subreddit, start, end).orElseThrow();
                log.debug(String.format("%d %s retrieved.", submissions.size(), English.plural("submission", submissions.size())));

                List<Submission> result = update(submissions);
                result.sort(Comparator.comparing(RedditSnowflake::getCreated));
                return result;
                //Submissions are impossible to acccess
            }catch(IllegalArgumentException e){
                log.error(e.getMessage());
                //Remove the subreddit and all channels
                remove(subreddit);
                return Collections.emptyList();
            }
        }
    }
}