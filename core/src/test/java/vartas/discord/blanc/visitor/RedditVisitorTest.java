package vartas.discord.blanc.visitor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import vartas.discord.blanc.*;
import vartas.discord.blanc.factory.GuildFactory;
import vartas.discord.blanc.factory.ShardFactory;
import vartas.discord.blanc.factory.TextChannelFactory;
import vartas.discord.blanc.json.JSONMessage;
import vartas.discord.blanc.mock.DiscordServerHookPointMock;
import vartas.discord.blanc.mock.RedditClientMock;
import vartas.discord.blanc.mock.SubredditMock;
import vartas.reddit.Submission;
import vartas.reddit.factory.SubmissionFactory;
import vartas.reddit.factory.SubredditFactory;

import java.io.IOException;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

public class RedditVisitorTest extends AbstractTest {
    DiscordServerHookPointMock discordHook;
    Shard shard;
    Guild guild;
    TextChannel textChannel;
    Message message;

    RedditClientMock redditHook;
    RedditVisitor redditVisitor;
    SubredditMock subreddit;
    Submission submission;
    @BeforeEach
    public void setUp() throws IOException {
        initRedditHook();
        initDiscordHook();
        redditVisitor = new RedditVisitor(discordHook, redditHook);
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
                "author",
                "title",
                0,
                false,
                false,
                "id",
                Instant.now()
        );

        redditHook = new RedditClientMock();
        redditHook.putSubreddits(subreddit.getName(), subreddit);
        subreddit.submissions.add(submission);
    }

    private void initDiscordHook() throws IOException {
        message = JSONMessage.of(RESOURCES.resolve("message.json"));

        textChannel = TextChannelFactory.create(2, "TextChannel");
        textChannel.addSubreddits(subreddit.getName());

        guild = GuildFactory.create(1, "Guild");
        guild.putChannels(textChannel.getId(), textChannel);

        shard = ShardFactory.create(0);
        shard.putGuilds(guild.getId(), guild);

        discordHook = new DiscordServerHookPointMock();

    }

    @Test
    public void testRedditTimeoutException(){
        subreddit.action = SubredditMock.ACTION.TIMEOUT_EXCEPTION;

        shard.accept(redditVisitor);

        assertThat(textChannel.getSubreddits()).containsExactly(subreddit.getName());
        assertThat(discordHook.sent).isEmpty();
    }

    @Test
    public void testRedditHttpException(){
        subreddit.action = SubredditMock.ACTION.HTTP_EXCEPTION;

        shard.accept(redditVisitor);

        assertThat(textChannel.isEmptySubreddits()).isTrue();
        assertThat(discordHook.sent).isEmpty();
    }

    @Test
    public void testRedditUnsuccessfulException(){
        subreddit.action = SubredditMock.ACTION.UNSUCCESSFUL_EXCEPTION;

        shard.accept(redditVisitor);

        assertThat(textChannel.getSubreddits()).containsExactly(subreddit.getName());
        assertThat(discordHook.sent).isEmpty();
    }

    @Test
    public void testSuccess(){
        shard.accept(redditVisitor);

        assertThat(discordHook.sent).isNotEmpty();
    }
}
