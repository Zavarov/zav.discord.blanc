package vartas.discord.bot.api.threads;

/*
 * Copyright (C) 2019 Zavarov
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

import com.google.common.collect.Lists;
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
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Semaphore;
import java.util.stream.Collectors;

public class SubredditFeed{
    /**
     * Only one process at a time is allowed to modify the channel list.
     */
    protected final Semaphore mutex = new Semaphore(1);
    /**
     * The log for this class.
     */
    protected final Logger log = JDALogger.getLog(this.getClass().getSimpleName());
    /**
     * We keep the latest submission as a bound for the next request.
     */
    protected SubmissionInterface latestSubmission;
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
    protected List<SubmissionInterface> request(){
        //The lastest submission is not initialized during the first request
        Instant start = latestSubmission == null ? Instant.now() : latestSubmission.getCreated().toInstant();
        Instant end = Instant.now().minusSeconds(60);

        List<SubmissionInterface> submissions;

        //Handel Reddit API exceptions
        try{
            submissions = environment.submission(subreddit, start, end).orElse(Collections.emptyList());
        }catch(UnresolvableRequestException e){
            mutex.acquireUninterruptibly();
            for(TextChannel channel : channels)
                environment.communicator(channel).execute(() -> environment.remove(subreddit, channel));
            mutex.release();

            submissions = Collections.emptyList();
        }

        //The submissions are sorted, so the newest one is on the first entry
        latestSubmission = submissions.isEmpty() ? latestSubmission : submissions.get(0);

        //Return the reverse so that we can send the oldest one first.
        return Lists.reverse(submissions);
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
                        log.warn("Couldn't send a submission from "+subreddit, e);
                        mutex.acquireUninterruptibly();
                        environment.communicator(channel).execute(() -> environment.remove(subreddit, channel));
                        mutex.release();
                    }catch(ErrorResponseException e){
                        log.warn("Couldn't send a submission from "+subreddit, e);
                        ErrorResponse response = e.getErrorResponse();
                        if(response == ErrorResponse.UNKNOWN_GUILD || response == ErrorResponse.UNKNOWN_CHANNEL) {
                            mutex.acquireUninterruptibly();
                            environment.communicator(channel).execute(() -> environment.remove(subreddit, channel));
                            mutex.release();
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
