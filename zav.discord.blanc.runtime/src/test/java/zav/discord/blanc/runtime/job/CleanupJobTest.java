package zav.discord.blanc.runtime.job;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import zav.discord.blanc.runtime.command.AbstractTest;

/**
 * This test case checks whether invalid text channels and webhooks are deleted from the database.
 */
@ExtendWith(MockitoExtension.class)
public class CleanupJobTest extends AbstractTest {
  CleanupJob job;

  @BeforeEach
  public void setUp() {
    job = new CleanupJob(client);
  }

  @Test
  public void testRemoveTextChannel() {
    job.run();
    
    assertEquals(guildEntity.getTextChannels(), Collections.emptyList());
  }
  
  @Test
  public void testRemoveWebhookl() {
    job.run();

    assertEquals(guildEntity.getWebhooks(), Collections.emptyList());
  }
}
