package zav.discord.blanc.runtime.job;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import zav.discord.blanc.databind.GuildEntity;
import zav.discord.blanc.databind.TextChannelEntity;
import zav.discord.blanc.databind.WebhookEntity;
import zav.discord.blanc.runtime.command.AbstractTest;

/**
 * This test case checks whether invalid text channels and webhooks are deleted from the database.
 */
@ExtendWith(MockitoExtension.class)
public class CleanupJobTest extends AbstractTest {
  TextChannelEntity channelEntity;
  WebhookEntity webhookEntity;
  GuildEntity guildEntity;
  CleanupJob job;
  
  /**
   * Initializes the cleanup job with a mocked database. The database contains both a webhook and a
   * text-channel.
   */
  @BeforeEach
  public void setUp() {
    webhookEntity = new WebhookEntity();
    channelEntity = new TextChannelEntity();
    channelEntity.add(webhookEntity);
    guildEntity = new GuildEntity();
    guildEntity.add(channelEntity);
    guildEntity.add(webhookEntity);
    
    when(entityManager.find(eq(GuildEntity.class), anyLong())).thenReturn(guildEntity);

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
