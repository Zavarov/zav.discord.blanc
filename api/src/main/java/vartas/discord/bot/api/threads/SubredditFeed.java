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

package vartas.discord.bot.api.threads;

import com.google.common.collect.Sets;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.exceptions.ErrorResponseException;
import net.dv8tion.jda.core.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.core.requests.ErrorResponse;
import net.dv8tion.jda.core.utils.JDALogger;
import org.atteo.evo.inflector.English;
import org.slf4j.Logger;
import vartas.discord.bot.api.environment.EnvironmentInterface;
import vartas.discord.bot.api.message.SubmissionMessage;
import vartas.reddit.SubmissionInterface;
import vartas.reddit.UnresolvableRequestException;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.Semaphore;
import java.util.stream.Collectors;

public class SubredditFeed{
    /**
     *  Contains all submissions from the last iteration to prevent duplicate submissions.
     */
    protected Set<SubmissionInterface> memory = Collections.emptySet();
    /**
     * Only one process at a time is allowed to modify the channel list.
     */
    protected final Semaphore mutex = new Semaphore(1);
    /**
     * The log for this class.
     */
    protected final Logger log = JDALogger.getLog(this.getClass().getSimpleName());
    /**
     * A list of all textchannels that are notified by a new submission.
     */
    protected Set<TextChannel> channels = new HashSet<>();
    /**
     * The subreddit that is watched by this class.
     */
    protected String subreddit;
    /**
     * The Discord environment of the bot.
     */
    protected EnvironmentInterface environment;

    public SubredditFeed(String subreddit, EnvironmentInterface environment){
        this.subreddit = subreddit;
        this.environment = environment;
    }

    public void addTextChannel(TextChannel channel){
        mutex.acquireUninterruptibly();
        channels.add(channel);
        mutex.release();
    }

    public void removeTextChannel(TextChannel channel){
        mutex.acquireUninterruptibly();
        channels.remove(channel);
        mutex.release();
    }

    public Set<TextChannel> getTextChannels(){
        return Collections.unmodifiableSet(channels);
    }

    /**
     * Returns a reversed list of all submissions, beginning with the oldest one.
     * Generates all the message for the latest submissions.
     * @return all submissions that have been submitted since the last time.
     */
    protected SortedSet<SubmissionInterface> request(){
        //Go back 1:30 minutes to have some leeway
        Instant start = Instant.now().minus(150, ChronoUnit.SECONDS);

        //Submissions need to be at least 1 minute old so that the user can flag them correctly
        Instant end = Instant.now().minus(60, ChronoUnit.SECONDS);

        //We request the submissions between 90 seconds and update every 60 seconds -> 30 seconds buffer

        SortedSet<SubmissionInterface> submissions;

        //Handel Reddit API exceptions
        try{
            submissions = environment.submission(subreddit, start, end).orElseGet(TreeSet::new);
        }catch(UnresolvableRequestException e){
            mutex.acquireUninterruptibly();
            for(TextChannel channel : channels)
                environment.communicator(channel).execute(() -> environment.remove(subreddit, channel));
            mutex.release();

            submissions = Collections.emptySortedSet();
        }

        //Since the set is ordered, the oldest one will be first.
        submissions = new TreeSet<>(Sets.filter(submissions, submission -> !memory.contains(submission)));
        memory = submissions;

        return submissions;
    }
    /**
     * Generates all the message for the latest submissions.
     * @return all submissions that have been submitted since the last time.
     */
    protected List<MessageBuilder> requestMessage(){
        return request().stream().map(SubmissionMessage::create).collect(Collectors.toList());
    }

    public void update(){
        log.info("Request submissions from "+subreddit);
        try{
            List<MessageBuilder> submissions = requestMessage();

            for(MessageBuilder submission : submissions){
                //Use an iterator since we might remove channels.
                for(TextChannel channel : channels){
                    //Handle Discord exceptions
                    try {
                        environment.communicator(channel).send(channel, submission);
                    }catch(InsufficientPermissionException e){
                        log.warn("Couldn't send a submission from "+subreddit, e.getMessage());
                        environment.communicator(channel).execute(() -> environment.remove(subreddit, channel));
                    }catch(ErrorResponseException e){
                        log.warn("Couldn't send a submission from "+subreddit, e.getMessage());
                        ErrorResponse response = e.getErrorResponse();
                        if(response == ErrorResponse.UNKNOWN_GUILD || response == ErrorResponse.UNKNOWN_CHANNEL) {
                            environment.communicator(channel).execute(() -> environment.remove(subreddit, channel));
                        }
                    }
                }
            }

            if(submissions.size() > 0)
                log.info(String.format("Posted %d new %s from r/%s", submissions.size(), English.plural("submission", submissions.size()),subreddit));
        //"If any execution of the task encounters an exception, subsequent executions are suppressed" my ass
        }catch(Throwable t){
            log.error("Unhandled exception caught from "+subreddit,t);
        }
    }
}
