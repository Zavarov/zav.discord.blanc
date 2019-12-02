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

package vartas.discord.bot.reddit;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.internal.utils.JDALogger;
import org.atteo.evo.inflector.English;
import org.slf4j.Logger;
import vartas.discord.bot.entities.BotGuild;
import vartas.discord.bot.entities.DiscordEnvironment;
import vartas.discord.bot.message.SubmissionMessage;
import vartas.discord.bot.visitor.DiscordEnvironmentVisitor;
import vartas.reddit.RedditSnowflake;
import vartas.reddit.Submission;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class SubmissionCache {
    /**
     * The log for this class.
     */
    protected final Logger log = JDALogger.getLog(this.getClass().getSimpleName());
    /**
     *  Contains the most recent received submissions and
     *  their corresponding Discord message.
     */
    protected Cache<Submission, MessageBuilder> cache;
    /**
     * The subreddit the submissions belong to.
     */
    protected String subreddit;
    /**
     * The global environment.
     */
    protected DiscordEnvironment environment;

    /**
     * @param subreddit the subreddit submissions are requested from.
     */
    public SubmissionCache(String subreddit, DiscordEnvironment environment){
        //We only need a few minutes to avoid duplicates
        this.cache = CacheBuilder.newBuilder().expireAfterWrite(10, TimeUnit.MINUTES).build();
        this.subreddit = subreddit;
        this.environment = environment;
    }

    /**
     * Transforms the submission into a Discord message and sorts the entries
     * by their creation date.
     * @param submissions the newly requested submissions.
     * @return a list containing the messages for the individual submissions.
     */
    private List<MessageBuilder> toMessage(List<Submission> submissions){
        return submissions
                .stream()
                .sorted(Comparator.comparing(RedditSnowflake::getCreated))
                .map(submission -> cache.asMap().get(submission))
                .collect(Collectors.toList());
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
            cache.put(submission, SubmissionMessage.create(submission));
        }

        return result;
    }


    /**
     * Requests submissions between the given intervals and stores them in the local cache.
     * @param start the (exclusive) minimum age of the submissions.
     * @param end the (exclusive) maximum age of the submissions.
     * @return a sorted list of all new submission messages.
     */
    public List<MessageBuilder> request(LocalDateTime start, LocalDateTime end){
        try{
            log.debug(String.format("requesting [%s, %s] from '%s'", start, end, subreddit));
            Collection<Submission> submissions = environment.submission(subreddit, start, end).orElseThrow();
            log.debug(String.format("%d %s retrieved.", submissions.size(), English.plural("submission", submissions.size())));

            List<Submission> result = update(submissions);
            return toMessage(result);
        //Submissions are impossible to acccess
        }catch(IllegalArgumentException e){
            log.error(e.getMessage());
            new RemoveSubredditVisitor().accept();
            return Collections.emptyList();
        }
    }

    private class RemoveSubredditVisitor implements DiscordEnvironmentVisitor{
        public void accept(){
            environment.accept(this);
        }
        @Override
        public void handle(BotGuild group){
            environment.schedule(() -> {
                group.remove(BotGuild.SUBREDDIT, subreddit);
                group.store();
            });
        }
    }
}
