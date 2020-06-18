package vartas.discord.blanc.visitor;

import org.atteo.evo.inflector.English;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vartas.discord.blanc.*;
import vartas.reddit.Submission;
import vartas.reddit.Subreddit;
import vartas.reddit.UnsuccessfulRequestException;

import javax.annotation.Nonnull;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Iterator;
import java.util.List;

/**
 * This visitor class is responsible for requesting the latest submissions
 * from all registered subreddits in the internal structure.
 * <br>
 * In order to detect new submissions, an internal {@link Instant}
 * of the most recent {@link Submission} is kept. During each cycle,
 * all submissions past this date are considered to be new. In addition,
 * the {@link Instant} of the latest {@link Submission} becomes the new
 * date for the succeeding cycle.
 */
@Nonnull
public class RedditVisitor implements ArchitectureVisitor {
    /**
     * This class's {@link Logger}, logging the individual phases of the request.
     */
    @Nonnull
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    /**
     * The hook point for submitting new {@link Submission submissions}.
     */
    @Nonnull
    private final ServerHookPoint discordHookPoint;
    /**
     * The hook point for receiving new {@link Submission submissions}.
     */
    @Nonnull
    private final vartas.reddit.Client redditClient;
    /**
     * The creation time of the most recent {@link Submission}.
     */
    @Nonnull
    private Instant inclusiveFrom = Instant.now();
    /**
     * The time when the current cycle was started.
     */
    @Nonnull
    private Instant exclusiveTo = inclusiveFrom;

    /**
     * Initializes the visitor.
     * @param discordHookPoint the hook point for submitting new {@link Submission submissions}
     * @param redditClient the hook point for receiving new {@link Submission submissions}
     */
    public RedditVisitor
            (
                    @Nonnull ServerHookPoint discordHookPoint,
                    @Nonnull vartas.reddit.Client redditClient
            )
    {
        this.discordHookPoint = discordHookPoint;
        this.redditClient = redditClient;
    }

    /**
     * Update {@link #exclusiveTo} with the current time, to indicate the start of a new cycle.
     * @param shard the current {@link Shard}.
     */
    @Override
    public void visit(@Nonnull ShardTOP shard){
        log.info("Visiting shard {}.", shard.getId());

        //Keep the dates synchronized between multiple shards.
        if(shard.getId() == 0) {
            //Submissions need to be at least one minute old
            exclusiveTo = Instant.now().minus(1, ChronoUnit.MINUTES);
        }
    }

    /**
     * Update {@link #inclusiveFrom} to avoid requesting submissions multiple times.
     * @param shard the current {@link Shard}.
     */
    @Override
    public void endVisit(@Nonnull ShardTOP shard){
        //Keep the dates synchronized between multiple shards.
        if(shard.getId() == 0)
            inclusiveFrom = exclusiveTo;
    }

    /**
     * Log when entering a guild.
     * @param guild the current {@link Guild}.
     */
    @Override
    public void visit(@Nonnull GuildTOP guild){
        log.info("Visiting guild {}", guild.getName());
    }

    /**
     * Request the latest submissions from all registered subreddits in this channel and submit them.
     * In case one of the requests failed, either due to an error on either clients, unregister the subreddit.
     * @param textChannel the current {@link TextChannel}.
     */
    @Override
    public void visit(@Nonnull TextChannel textChannel){
        log.info("Visiting text channel {}", textChannel.getName());
        Iterator<String> iterator = textChannel.iteratorSubreddits();
        List<Submission> submissions;
        Subreddit subreddit;

        while(iterator.hasNext()){
            try {
                subreddit = redditClient.getSubreddits(iterator.next());

                submissions = subreddit.getSubmissions(inclusiveFrom, exclusiveTo);

                log.info("{} new {} between {} and {}",
                        submissions.size(),
                        English.plural("submission", submissions.size()),
                        inclusiveFrom,
                        exclusiveTo
                );

                //Post the individual submissions
                for (Submission submission : submissions)
                    discordHookPoint.send(textChannel, submission);
            //Reddit Exceptions
            } catch( UnsuccessfulRequestException e) {
                log.warn(Errors.UNSUCCESSFUL_REDDIT_REQUEST.toString(), e);
            } catch(vartas.reddit.TimeoutException e) {
                log.warn(Errors.REDDIT_TIMEOUT.toString(), e);
            //Catch unknown Exception to continue with the Thread, even after an a failure.
            //This also includes a possible HTTP exception, indicating an unresolvable request
            } catch(Exception e){
                log.warn(Errors.REFUSED_REDDIT_REQUEST.toString(), e);
                iterator.remove();
            }
        }
    }
}
