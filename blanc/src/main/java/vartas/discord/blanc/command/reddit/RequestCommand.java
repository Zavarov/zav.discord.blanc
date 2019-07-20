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
package vartas.discord.blanc.command.reddit;

import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import org.atteo.evo.inflector.English;
import vartas.discord.bot.api.communicator.CommunicatorInterface;
import vartas.discord.bot.api.environment.RedditInterface;
import vartas.discord.bot.command.entity._ast.ASTEntityType;
import vartas.reddit.CommentInterface;
import vartas.reddit.SubmissionInterface;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Semaphore;
import java.util.function.Consumer;

/**
 * This command requests the data from a subreddit over the crawler.
 */
public class RequestCommand extends RequestCommandTOP implements Consumer<Message>{
    /**
     * The maximal number we try to request data before we give up.
     */
    private static final int MAX_RETRY = 13;
    /**
     * The default message for each requested day.
     */
    private static final String MESSAGE = "Requesting data from r/%s over %d %s.\n%d %s and %d %s have been requested so far.\nThis message will be updated until completion.";
    /**
     * Due to the long time it takes, only one request at a time is allowed.
     */
    protected static final Semaphore MUTEX = new Semaphore(1);
    /**
     * The start of the interval.
     */
    Instant from;
    /**
     * The end of the interval.
     */
    Instant to;
    /**
     * The number of days in the interval.
     */
    long days;
    /**
     * The number of requested comments so far.
     */
    int commentsAmount = 0;
    /**
     * The number of requested submissions so far.
     */
    int submissionsAmount = 0;
    /**
     * The subreddit data is requested from.
     */
    String subreddit;
    public RequestCommand(Message source, CommunicatorInterface communicator, List<ASTEntityType> parameters) throws IllegalArgumentException, IllegalStateException {
        super(source, communicator, parameters);
    }

    /**
     * Requests the session and then requests the data for each day.
     */
    @Override
    public void run(){
        if(MUTEX.tryAcquire()){
            from = fromSymbol.resolve().get().toInstant();
            to = toSymbol.resolve().get().toInstant();
            days = RedditInterface.countDays(from, to);
            subreddit = subredditSymbol.resolve();

            MessageBuilder message = new MessageBuilder();
            message.append(String.format(MESSAGE,
                    subreddit,
                    days, English.plural("day", (int) days),
                    commentsAmount, English.plural("comment", commentsAmount),
                    submissionsAmount, English.plural("submission", submissionsAmount)));

            communicator.send(channel, message, this);
        }else{
            communicator.send(channel, "A request is currently being processed. Try again later.");
        }
    }
    /**
     * The user has been informed that the request has been recognized and that
     * the program is now proceeding with requesting all the data.
     * @param t the message that was sent.
     */
    @Override
    public void accept(Message t) {
        Instant current = from, next;
        int counter = 0;
        try {
            while (!current.isAfter(to)) {
                log.info(String.format("Requesting data from r/%s on the %s", subreddit, current));
                next = current.plus(Duration.ofDays(1));

                log.info("Requesting submissions");
                Optional<List<SubmissionInterface>> submissions = environment.pushshift(subreddit, current, next);

                if (submissions.isPresent()) {
                    log.info("Requesting comments");
                    Optional<List<CommentInterface>> comments = environment.comment(submissions.get());
                    if (comments.isPresent()) {
                        log.info("Storing data");
                        RedditInterface.storeSubmission(current, subreddit, submissions.get());
                        RedditInterface.storeComment(current, subreddit, comments.get());

                        commentsAmount += comments.get().size();
                        submissionsAmount += submissions.get().size();
                        counter = 0;
                    } else if (counter == MAX_RETRY) {
                        String error = String.format("%d %s failed in a row, giving up.", counter, English.plural("request", counter));
                        log.warn(error);
                        communicator.send(t.editMessage(error));
                    } else {
                        counter++;
                        log.info(String.format("Comment request failed, %d attempts remain.", MAX_RETRY - counter));
                        continue;
                    }
                } else if (counter == MAX_RETRY) {
                    String error = String.format("%d %s failed in a row, giving up.", counter, English.plural("request", counter));
                    log.warn(error);
                    communicator.send(t.editMessage(error));
                } else {
                    counter++;
                    continue;
                }

                days--;
                communicator.send(t.editMessage(String.format(MESSAGE,
                        subreddit,
                        days, English.plural("day", (int) days),
                        commentsAmount, English.plural("comment", commentsAmount),
                        submissionsAmount, English.plural("submission", submissionsAmount))));

                current = next;
            }
        }catch(Exception e){
            communicator.send(t.editMessage(e.getClass().getSimpleName()+":"+e.getMessage()));
        }finally {
            MUTEX.release();
        }
    }
}