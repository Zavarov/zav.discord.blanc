package zav.discord.blanc.reddit;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import zav.discord.blanc.exceptions.WebhookException;
import zav.discord.blanc.io._json.JSONCredentials;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class WebhookSubredditListenerTest extends AbstractRedditTest {
    Path targetDirectory = Paths.get("target", "test", "resources");
    Path jsonDirectory;
    WebhookSubredditListener listener;

    @BeforeEach
    public void setUp() {
        jsonDirectory = JSONCredentials.CREDENTIALS.getJsonDirectory();
        JSONCredentials.CREDENTIALS.setJsonDirectory(targetDirectory);

        listener = new WebhookSubredditListener(guild, webhook);
    }

    @AfterEach
    public void tearDown(){
        JSONCredentials.CREDENTIALS.setJsonDirectory(jsonDirectory);
    }

    @Test
    public void testNewLink(){
        listener.newLink(redditdev, link);

        assertThat(webhook.getSubreddits()).contains("modnews");
    }

    @Test
    public void testNewLinkWebhookException(){
        webhook.sendLinkException = new WebhookException();

        assertThatThrownBy(() -> listener.newLink(redditdev, link)).isInstanceOf(InvalidListenerException.class);
    }

    @Test
    public void testNewLinkIOException(){
        textChannel.sendLinkException = new IOException("IOException");
        listener.newLink(redditdev, link);

        assertThat(webhook.getSubreddits()).contains("modnews");
    }

    @Test
    public void testDestroy(){
        listener.destroy("modnews");

        assertThat(webhook.getSubreddits()).isEmpty();
    }
}
