package vartas.discord.bot.api.environment;

import net.dv8tion.jda.core.entities.TextChannel;
import vartas.reddit.CommentInterface;
import vartas.reddit.SubmissionInterface;
import vartas.reddit.SubredditInterface;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

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
public interface RedditInterface {
    /**
     * Uses the pushshift API to retrieve more than the past 1000 submissions.
     * @param subreddit the subreddit the submissions are from.
     * @param start the inclusively oldest submission in the interval.
     * @param end the inclusively newest submission in the interval.
     * @return the submissions in the subreddit within the given interval.
     */
    Optional<List<SubmissionInterface>> pushshift(String subreddit, Instant start, Instant end);
    /**
     * @param subreddit the subreddit the submissions are from.
     * @param start the inclusively oldest submission in the interval.
     * @param end the inclusively newest submission in the interval.
     * @return the submissions in the subreddit within the given interval.
     */
    Optional<List<SubmissionInterface>> submission(String subreddit, Instant start, Instant end);
    /**
     * @param subreddit the name of the subreddit.
     * @return the subreddit instance with that name.
     */
    Optional<SubredditInterface> subreddit(String subreddit);
    /**
     * @param date the date the submissions were submitted.
     * @param subreddit the subreddit the submissions are from.
     * @return a list of all submissions from that subreddit on that specific date.
     */
    List<? extends SubmissionInterface> loadSubmission(Instant date, String subreddit);
    /**
     * @param date the date the submissions were submitted.
     * @param subreddit the subreddit the submissions are from
     * @return a list of all comments in that subreddit during the specified date.
     */
    List<? extends CommentInterface> loadComment(Instant date, String subreddit);

    /**
     * Stores the comments on the disk.
     * @param date the date the submissions were submitted.
     * @param subreddit the subreddit the submissions are from
     * @param comments the comments that are stored.
     */
    void storeComment(Instant date, String subreddit, Collection<? extends CommentInterface> comments);

    /**
     * Stores the comments on the disk.
     * @param date the date the submissions were submitted.
     * @param subreddit the subreddit the submissions are from
     * @param submissions the submissions that are stored.
     */
    void storeSubmission(Instant date, String subreddit, Collection<? extends SubmissionInterface> submissions);
    /**
     * Makes the program post submissions from the subreddit in the specified channel.
     * In addition, it will also update the guild configuration file to memorize the change.
     * @param subreddit the name of the subreddit.
     * @param channel the textchannel where new submissions are posted.
     */
    void add(String subreddit, TextChannel channel);
    /**
     * Removes a channel from the set of all channels where new submissions from this subreddit are posted.
     * In addition, it will also update the guild configuration file to memorize the change.
     * @param subreddit the name of the subreddit.
     * @param channel the channel that is removed from the set.
     */
    void remove(String subreddit, TextChannel channel);
}
