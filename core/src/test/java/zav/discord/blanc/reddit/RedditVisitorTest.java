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

package zav.discord.blanc.reddit;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import zav.discord.blanc.io._json.JSONCredentials;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;

public class RedditVisitorTest extends AbstractRedditTest {
    RedditObservable observable;
    RedditVisitor visitor;

    Path targetDirectory = Paths.get("target", "test", "resources");
    Path jsonDirectory;

    @BeforeEach
    public void setUp() throws IOException {
        jsonDirectory = JSONCredentials.CREDENTIALS.getJsonDirectory();
        JSONCredentials.CREDENTIALS.setJsonDirectory(targetDirectory);

        observable = new RedditObservable();
        visitor = new RedditVisitor(observable, client);
    }

    @AfterEach
    public void tearDown(){
        JSONCredentials.CREDENTIALS.setJsonDirectory(jsonDirectory);
    }

    @Test
    public void testHandleGuild(){
        guild.accept(visitor);

        assertThat(observable.size()).isEqualTo(2);
        assertThat(observable.get(redditdev).size()).isEqualTo(1);
        assertThat(observable.get(modnews).size()).isEqualTo(1);
    }

    @Test
    public void testHandleShard(){
        shard.accept(visitor);

        assertThat(observable.size()).isEqualTo(2);
        assertThat(observable.get(redditdev).size()).isEqualTo(1);
        assertThat(observable.get(modnews).size()).isEqualTo(1);
    }
    /*
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
                LinkMock::new,
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
        subreddit.action = SubredditMock.STATE.SERVER_EXCEPTION;

        shard.accept(redditVisitor);

        assertThat(textChannel.retrieveMessages()).isEmpty();
        assertThat(webhook.retrieveMessages()).isEmpty();
    }

    @Test
    public void testClientException(){
        JSONCredentials.CREDENTIALS.setJsonDirectory(targetDirectory);
        subreddit.action = SubredditMock.STATE.CLIENT_EXCEPTION;

        shard.accept(redditVisitor);

        assertThat(textChannel.retrieveMessages()).isEmpty();
        assertThat(webhook.retrieveMessages()).isEmpty();
    }

    @Test
    public void testForbiddenException(){
        JSONCredentials.CREDENTIALS.setJsonDirectory(targetDirectory);
        subreddit.action = SubredditMock.STATE.FORBIDDEN_EXCEPTION;

        shard.accept(redditVisitor);

        assertThat(textChannel.retrieveMessages()).isEmpty();
        assertThat(webhook.retrieveMessages()).isEmpty();
    }

    @Test
    public void testRedditUnsuccessfulException(){
        JSONCredentials.CREDENTIALS.setJsonDirectory(targetDirectory);
        subreddit.action = SubredditMock.STATE.UNSUCCESSFUL_EXCEPTION;

        shard.accept(redditVisitor);

        assertThat(textChannel.retrieveMessages()).isEmpty();
        assertThat(webhook.retrieveMessages()).isEmpty();
    }

    @Test
    public void testUnknownException(){
        JSONCredentials.CREDENTIALS.setJsonDirectory(targetDirectory);
        subreddit.action = SubredditMock.STATE.UNKNOWN_EXCEPTION;

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

    @Test
    public void testInvalidSubreddit(){
        credentials.setJsonDirectory(targetDirectory);
        redditHook.invalidateAllSubreddits();

        shard.accept(redditVisitor);

        assertThat(textChannel.retrieveMessages()).isEmpty();
        assertThat(webhook.retrieveMessages()).isEmpty();
        assertThat(textChannel.getSubreddits()).isEmpty();
        assertThat(webhook.getSubreddits()).isEmpty();
    }
     */
}
