package zav.discord.blanc.reddit;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import zav.discord.blanc.exceptions.InsufficientPermissionException;
import zav.discord.blanc.io._json.JSONCredentials;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class TextChannelSubredditListenerTest extends AbstractRedditTest {
    Path targetDirectory = Paths.get("target", "test", "resources");
    Path jsonDirectory;
    TextChannelSubredditListener listener;

    @BeforeEach
    public void setUp() {
        jsonDirectory = JSONCredentials.CREDENTIALS.getJsonDirectory();
        JSONCredentials.CREDENTIALS.setJsonDirectory(targetDirectory);

        listener = new TextChannelSubredditListener(guild, textChannel);
    }

    @AfterEach
    public void tearDown(){
        JSONCredentials.CREDENTIALS.setJsonDirectory(jsonDirectory);
    }

    @Test
    public void testNewLink(){
        listener.newLink(redditdev, link);

        assertThat(textChannel.getSubreddits()).contains("redditdev");
    }

    @Test
    public void testNewLinkWebhookException(){
        textChannel.sendLinkException = new InsufficientPermissionException();

        assertThatThrownBy(() -> listener.newLink(redditdev, link)).isInstanceOf(InvalidListenerException.class);
    }

    @Test
    public void testNewLinkIOException(){
        textChannel.sendLinkException = new IOException("IOException");
        listener.newLink(redditdev, link);

        assertThat(textChannel.getSubreddits()).contains("redditdev");
    }

    @Test
    public void testDestroy(){
        listener.destroy("redditdev");

        assertThat(textChannel.getSubreddits()).isEmpty();
    }
}
