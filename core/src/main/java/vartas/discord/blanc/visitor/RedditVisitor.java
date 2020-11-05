/*
 * Copyright (c) 2020 Zavarov
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

package vartas.discord.blanc.visitor;

import org.atteo.evo.inflector.English;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vartas.discord.blanc.$visitor.ArchitectureVisitor;
import vartas.discord.blanc.*;
import vartas.reddit.ApiException;
import vartas.reddit.ClientException;
import vartas.reddit.Submission;
import vartas.reddit.Subreddit;

import javax.annotation.Nonnull;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * This visitor traverses through all guilds and their corresponding text channels. If a Reddit feed is assigned to one
 * of the text channels, the latest submissions of the corresponding {@link Subreddit} are fetched and then posted in
 * the channel itself. The visitor has to be called periodically, in order to retrieve new submissions in real-time.
 * <p>
 * Internally, an {@link Instant} is used to keep track of which submissions have already been posted. It contains the
 * last time the visitor has been executed, which means that every {@link Submission}, that is newer than this time step
 * is new and has not been posted before.
 */
@Nonnull
public class RedditVisitor implements ArchitectureVisitor {
    /**
     * This class's {@link Logger}, logging the individual phases of the request.
     */
    @Nonnull
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    /**
     * The hook point for receiving new {@link Submission submissions}.
     */
    @Nonnull
    private final vartas.reddit.Client redditClient;
    /**
     * The last time this visitor has been executed.
     */
    @Nonnull
    private Instant previousExecution = Instant.now();
    /**
     * The minimum age of a {@link Submission}. We allow a certain grace period for each {@link Submission}, in order
     * to allow the author to correctly flair and tag the post.
     */
    @Nonnull
    private Instant minimumAge = previousExecution;

    /**
     * Initializes the visitor.
     * @param redditClient The hook point for receiving new {@link Submission submissions}
     */
    public RedditVisitor(@Nonnull vartas.reddit.Client redditClient){
        this.redditClient = redditClient;
    }

    /**
     * In order to keep the minimum age synchronized between multiple shards, {@link #minimumAge} is only updated to the
     * current time for the first shard. All succeeding shards use exactly the same time. This is to avoid missing out
     * on submissions that have been made between the small time difference between the processing the individual
     * shards.
     * @param shard The current {@link Shard}.
     */
    @Override
    public void visit(@Nonnull Shard shard){
        log.trace("Visiting shard {}.", shard.getId());

        //Keep the dates synchronized between multiple shards.
        if(shard.getId() == 0) {
            //Take the timestamp from the last cycle
            previousExecution = minimumAge;
            //Submissions need to be at least one minute old
            minimumAge = Instant.now().minus(1, ChronoUnit.MINUTES);
        }
    }

    /**
     * Log when entering a guild.
     * @param guild The current {@link Guild}.
     */
    @Override
    public void handle(@Nonnull Guild guild){
        log.trace("Visiting guild {}", guild.getName());
        for(TextChannel textChannel : guild.retrieveTextChannels()) {
            log.trace("Visiting text channel {}", textChannel.getName());
            for (String subreddit : textChannel.getSubreddits()) {
                request(subreddit, textChannel::send, name -> this.removeSubreddit(name, guild, textChannel));
            }
            for(Webhook webhook : textChannel.retrieveWebhooks()) {
                log.trace("Visiting webhook {}", webhook.getName());
                for (String subreddit : webhook.getSubreddits()) {
                    request(subreddit, webhook::send, name -> this.removeSubreddit(name, guild, webhook));
                }
            }
        }
    }

    /**
     * Retrieves all submissions in the specified {@link Subreddit} that have been made between
     * {@link #previousExecution} and {@link #minimumAge} and forwards them to the corresponding {@link TextChannel}.
     * @param name The name of the {@link Subreddit} the submissions are retrieved from.
     * @param onSuccess The action that is performed for each retrieved {@link Submission}. It forwards a
     *                  {@link Submission} to the corresponding {@link TextChannel}.
     * @param onFailure The action that is performed if the {@link Subreddit} and consequently the submissions couldn't
     *                  be retrieved. It removes the feed from the {@link TextChannel}, so that they are skipped in
     *                  future execution instead of throwing errors.
     */
    private void request
    (
            @Nonnull String name,
            @Nonnull BiConsumer<Subreddit, Submission> onSuccess,
            @Nonnull Consumer<String> onFailure
    )
    {
        Optional<Subreddit> subredditOpt = getSubreddit(name, onFailure);
        subredditOpt.ifPresent(subreddit -> postSubmission(subreddit, onSuccess));
    }

    /**
     * Retrieves all submissions in the specified {@link Subreddit} that have been made between
     * {@link #previousExecution} and {@link #minimumAge} and forwards them to the corresponding {@link TextChannel}.
     * At this point, the {@link Subreddit} has already been successfully retrieved, meaning that the submissions should
     * be visible to the application.
     * @param subreddit The {@link Subreddit} instance matching the subreddit name specified in the {@link TextChannel}.
     * @param onSuccess The action that is performed for each retrieved {@link Submission}. It forwards a
     *                  {@link Submission} to the corresponding {@link TextChannel}.
     */
    private void postSubmission
    (
            @Nonnull Subreddit subreddit,
            @Nonnull BiConsumer<Subreddit, Submission> onSuccess
    )
    {

        try {
            List<Submission> submissions = subreddit.getSubmissions(previousExecution, minimumAge);

            log.trace("{} new {} between {} and {}",
                    submissions.size(),
                    English.plural("submission", submissions.size()),
                    previousExecution,
                    minimumAge
            );

            //Post the individual submissions
            for (Submission submission : submissions)
                onSuccess.accept(subreddit, submission);
        } catch(ClientException e) {
            log.warn(Errors.REDDIT_CLIENT_ERROR.toString(), e);
        } catch(ApiException e) {
            log.warn(Errors.REDDIT_API_ERROR.toString(), e);
        } catch(Exception e) {
            log.warn(Errors.UNKNOWN_RESPONSE.toString(), e);
        }
    }

    /**
     * Retrieves the {@link Subreddit} with the matching name. If the {@link Subreddit} couldn't be retrieved, the
     * corresponding entry in the {@link TextChannel} is removed, to prevent this error to appear again in future
     * requests.
     * @param subreddit The name of the {@link Subreddit}.
     * @param onFailure The action that is performed if the {@link Subreddit} and consequently the submissions couldn't
     *                  be retrieved. It removes the feed from the {@link TextChannel}, so that they are skipped in
     *                  future execution instead of throwing errors.
     * @return An {@link Optional} containing the {@link Subreddit} instance whose name matches the provided argument.
     *         If no {@link Subreddit} with such a name exists or the subreddit isn't accessible,
     *         {@link Optional#empty()} is returned.
     */
    @Nonnull
    private Optional<Subreddit> getSubreddit(@Nonnull String subreddit, @Nonnull Consumer<String> onFailure){
        try{
            return Optional.of(redditClient.getSubreddits(subreddit));
        }catch(Exception e){
            log.error(Errors.INVALID_SUBREDDIT.toString(), e);
            onFailure.accept(subreddit);
            return Optional.empty();
        }
    }

    private void removeSubreddit(String subreddit, Guild guild, TextChannel textChannel){
        textChannel.removeSubreddits(subreddit);
        Shard.write(guild, textChannel);
    }

    private void removeSubreddit(String subreddit, Guild guild, Webhook webhook){
        webhook.removeSubreddits(subreddit);
        Shard.write(guild, webhook);
    }
}
