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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import vartas.discord.blanc.AbstractTest;
import vartas.discord.blanc.Guild;
import vartas.discord.blanc.Shard;
import vartas.discord.blanc.factory.GuildFactory;
import vartas.discord.blanc.factory.ShardFactory;
import vartas.discord.blanc.factory.TextChannelFactory;
import vartas.discord.blanc.factory.WebhookFactory;
import vartas.discord.blanc.mock.*;
import vartas.reddit.Submission;
import vartas.reddit.factory.SubmissionFactory;
import vartas.reddit.factory.SubredditFactory;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

public class RedditVisitorTest extends AbstractTest {
    Shard shard;
    Guild guild;
    TextChannelMock textChannel;

    ClientMock redditHook;
    RedditVisitor redditVisitor;
    SubredditMock subreddit;
    Submission submission;
    WebhookMock webhook;
    @BeforeEach
    public void setUp() {
        initRedditHook();
        initDiscordHook();
        redditVisitor = new RedditVisitor(redditHook);
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
        redditHook.putSubreddits(subreddit.getName(), subreddit);

        subreddit.submissions.add(submission);
    }

    private void initDiscordHook() {
        webhook = (WebhookMock) WebhookFactory.create(WebhookMock::new, 12345L,"subreddit");

        textChannel = (TextChannelMock) TextChannelFactory.create(TextChannelMock::new, 2, "TextChannel");
        textChannel.addSubreddits(subreddit.getName());
        textChannel.putWebhooks("subreddit", webhook);

        guild = GuildFactory.create(GuildMock::new, new SelfMemberMock(), 1, "Guild");
        guild.putChannels(textChannel.getId(), textChannel);

        shard = ShardFactory.create(0, new SelfUserMock());
        shard.putGuilds(guild.getId(), guild);

    }

    @Test
    public void testServerException(){
        subreddit.action = SubredditMock.ACTION.SERVER_EXCEPTION;

        shard.accept(redditVisitor);

        assertThat(textChannel.getSubreddits()).containsExactly(subreddit.getName());
        assertThat(textChannel.valuesWebhooks()).containsExactly(webhook);
        assertThat(textChannel.sent).isEmpty();
        assertThat(webhook.sent).isEmpty();
    }

    @Test
    public void testClientException(){
        subreddit.action = SubredditMock.ACTION.FORBIDDEN_EXCEPTION;

        shard.accept(redditVisitor);

        assertThat(textChannel.getSubreddits()).containsExactly(subreddit.getName());
        assertThat(textChannel.valuesWebhooks()).containsExactly(webhook);
        assertThat(textChannel.sent).isEmpty();
        assertThat(webhook.sent).isEmpty();
    }

    @Test
    public void testForbiddenException(){
        subreddit.action = SubredditMock.ACTION.CLIENT_EXCEPTION;

        shard.accept(redditVisitor);

        assertThat(textChannel.getSubreddits()).containsExactly(subreddit.getName());
        assertThat(textChannel.valuesWebhooks()).containsExactly(webhook);
        assertThat(textChannel.sent).isEmpty();
        assertThat(webhook.sent).isEmpty();
    }

    @Test
    public void testRedditUnsuccessfulException(){
        subreddit.action = SubredditMock.ACTION.UNSUCCESSFUL_EXCEPTION;

        shard.accept(redditVisitor);

        assertThat(textChannel.getSubreddits()).containsExactly(subreddit.getName());
        assertThat(textChannel.valuesWebhooks()).containsExactly(webhook);
        assertThat(textChannel.sent).isEmpty();
        assertThat(webhook.sent).isEmpty();
    }

    @Test
    public void testUnknownException(){
        subreddit.action = SubredditMock.ACTION.UNKNOWN_EXCEPTION;

        shard.accept(redditVisitor);

        assertThat(textChannel.getSubreddits()).containsExactly(subreddit.getName());
        assertThat(textChannel.valuesWebhooks()).containsExactly(webhook);
        assertThat(textChannel.sent).isEmpty();
        assertThat(webhook.sent).isEmpty();
    }

    @Test
    public void testSuccess(){
        shard.accept(redditVisitor);

        assertThat(textChannel.sent).isNotEmpty();
        assertThat(webhook.sent).isNotEmpty();
    }
}
