package vartas.discord.bot.api.environment;

import org.junit.After;
import org.junit.Test;
import vartas.reddit.CommentInterface;
import vartas.reddit.SubmissionInterface;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

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
public class RedditInterfaceTest {
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @After
    public void tearDown(){
        new File("pushshift/subreddit/2001-01-01.com").delete();
        new File("pushshift/subreddit/2001-01-01.sub").delete();
    }


    @Test
    public void testLoadComment() throws ParseException {
        List<? extends CommentInterface> comments = RedditInterface.loadComment(dateFormat.parse("2000-01-01").toInstant(), "subreddit");

        assertThat(comments).hasSize(1);

        CommentInterface comment = comments.get(0);

        assertThat(comment.getAuthor()).isEqualTo("author");
        assertThat(comment.getId()).isEqualTo("id");
        assertThat(comment.getSubreddit()).isEqualTo("subreddit");
        assertThat(comment.getScore()).isEqualTo(1);
        assertThat(comment.getSubmission()).isEqualTo("submission");
        assertThat(comment.getSubmissionTitle()).isEqualTo("submissionTitle");
    }

    @Test
    public void testLoadSubmission() throws ParseException{
        List<? extends SubmissionInterface> submissions = RedditInterface.loadSubmission(dateFormat.parse("2000-01-01").toInstant(), "subreddit");

        assertThat(submissions).hasSize(1);

        SubmissionInterface submission = submissions.get(0);

        assertThat(submission.getAuthor()).isEqualTo("author");
        assertThat(submission.getId()).isEqualTo("id");
        assertThat(submission.getLinkFlairText()).contains("linkFlairText");
        assertThat(submission.getSubreddit()).isEqualTo("subreddit");
        assertThat(submission.isNsfw()).isTrue();
        assertThat(submission.isSpoiler()).isTrue();
        assertThat(submission.getScore()).isEqualTo(1);
        assertThat(submission.getTitle()).isEqualTo("title");
        assertThat(submission.getCreated()).isEqualTo(new Date(1L));
        assertThat(submission.getSelfText()).contains("selfText");
        assertThat(submission.getThumbnail()).contains("thumbnail");
        assertThat(submission.getUrl()).isEqualTo("url");
    }

    @Test
    public void testStoreComment() throws ParseException{
        assertThat(new File("pushshift/subreddit/2001-01-01.com")).doesNotExist();

        List<? extends CommentInterface> comments = RedditInterface.loadComment(dateFormat.parse("2000-01-01").toInstant(), "subreddit");

        RedditInterface.storeComment(dateFormat.parse("2001-01-01").toInstant(), "subreddit", comments);

        assertThat(new File("pushshift/subreddit/2001-01-01.com")).exists();
    }

    @Test
    public void testStoreSubmission() throws ParseException{
        assertThat(new File("pushshift/subreddit/2001-01-01.sub")).doesNotExist();

        List<? extends SubmissionInterface> submissions = RedditInterface.loadSubmission(dateFormat.parse("2000-01-01").toInstant(), "subreddit");

        RedditInterface.storeSubmission(dateFormat.parse("2001-01-01").toInstant(), "subreddit", submissions);

        assertThat(new File("pushshift/subreddit/2001-01-01.sub")).exists();
    }

    @Test
    public void testCountDays() throws ParseException{
        Instant from = dateFormat.parse("2000-01-01").toInstant();
        Instant until = dateFormat.parse("2000-01-02").toInstant();
        assertThat(RedditInterface.countDays(from, until)).isEqualTo(2);
    }
}
