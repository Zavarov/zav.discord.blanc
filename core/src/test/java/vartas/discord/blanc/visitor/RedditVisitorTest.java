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

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import vartas.discord.blanc.AbstractTest;
import vartas.discord.blanc.io.$json.JSONCredentials;
import vartas.discord.blanc.mock.ClientMock;
import vartas.discord.blanc.mock.SubmissionMock;
import vartas.discord.blanc.mock.SubredditMock;
import vartas.reddit.Submission;
import vartas.reddit.factory.SubmissionFactory;
import vartas.reddit.factory.SubredditFactory;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

public class RedditVisitorTest extends AbstractTest {
    Path targetDirectory = Paths.get("target", "test", "resources");
    Path jsonDirectory;
    ClientMock redditHook;
    RedditVisitor redditVisitor;
    SubredditMock subreddit;
    Submission submission;
    @BeforeEach
    public void setUp() {
        initRedditHook();
        jsonDirectory = JSONCredentials.CREDENTIALS.getJsonDirectory();
        redditVisitor = new RedditVisitor(redditHook);
    }

    @AfterEach
    public void tearDown(){
        credentials.setJsonDirectory(jsonDirectory);
    }

    private void initRedditHook(){
        subreddit = (SubredditMock) SubredditFactory.create(
                SubredditMock::new,
                "subreddit",
                "description",
                //It's over 9000 :O
                9001,
                "id",
                Instant.now()
        );

        //TODO From JSON
        submission = SubmissionFactory.create(
                SubmissionMock::new,
                "author",
                "title",
                0,
                false,
                false,
                "id",
                Instant.now()
        );

        redditHook = new ClientMock();
        textChannel.getSubreddits().forEach(name -> redditHook.putSubreddits(name, subreddit));
        webhook.getSubreddits().forEach(name -> redditHook.putSubreddits(name, subreddit));

        subreddit.submissions.add(submission);
    }

    @Test
    public void testServerException(){
        JSONCredentials.CREDENTIALS.setJsonDirectory(targetDirectory);
        subreddit.action = SubredditMock.ACTION.SERVER_EXCEPTION;

        shard.accept(redditVisitor);

        assertThat(textChannel.retrieveMessages()).isEmpty();
        assertThat(webhook.retrieveMessages()).isEmpty();
    }

    @Test
    public void testClientException(){
        JSONCredentials.CREDENTIALS.setJsonDirectory(targetDirectory);
        subreddit.action = SubredditMock.ACTION.CLIENT_EXCEPTION;

        shard.accept(redditVisitor);

        assertThat(textChannel.retrieveMessages()).isEmpty();
        assertThat(webhook.retrieveMessages()).isEmpty();
    }

    @Test
    public void testForbiddenException(){
        JSONCredentials.CREDENTIALS.setJsonDirectory(targetDirectory);
        subreddit.action = SubredditMock.ACTION.FORBIDDEN_EXCEPTION;

        shard.accept(redditVisitor);

        assertThat(textChannel.retrieveMessages()).isEmpty();
        assertThat(webhook.retrieveMessages()).isEmpty();
    }

    @Test
    public void testRedditUnsuccessfulException(){
        JSONCredentials.CREDENTIALS.setJsonDirectory(targetDirectory);
        subreddit.action = SubredditMock.ACTION.UNSUCCESSFUL_EXCEPTION;

        shard.accept(redditVisitor);

        assertThat(textChannel.retrieveMessages()).isEmpty();
        assertThat(webhook.retrieveMessages()).isEmpty();
    }

    @Test
    public void testUnknownException(){
        JSONCredentials.CREDENTIALS.setJsonDirectory(targetDirectory);
        subreddit.action = SubredditMock.ACTION.UNKNOWN_EXCEPTION;

        shard.accept(redditVisitor);

        assertThat(textChannel.retrieveMessages()).isEmpty();
        assertThat(webhook.retrieveMessages()).isEmpty();
    }

    @Test
    public void testSuccess(){
        JSONCredentials.CREDENTIALS.setJsonDirectory(targetDirectory);
        shard.accept(redditVisitor);

        assertThat(textChannel.retrieveMessages()).isNotEmpty();
        assertThat(webhook.retrieveMessages()).isNotEmpty();
    }
}
