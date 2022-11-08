package zav.discord.blanc.databind;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.Webhook;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import zav.discord.blanc.databind.internal.PersistenceUtil;

@ExtendWith(MockitoExtension.class)
public class WebhookEntityTest {
  GuildEntity guildEntity;
  TextChannelEntity channelEntity;
  WebhookEntity webhookEntity;
  
  @Mock Guild guild;
  @Mock TextChannel channel;
  @Mock Webhook webhook;
  
  @BeforeEach
  public void setUp() {
    guildEntity = GuildEntity.find(guild);
    channelEntity = TextChannelEntity.find(channel);
    webhookEntity = WebhookEntity.find(webhook);
      
    guildEntity.add(webhookEntity);
    guildEntity.add(channelEntity);
    guildEntity.merge();
  }
  
  @AfterEach
  public void tearDown() {
    GuildEntity.remove(guild);
  }
  
  @Test
  public void testFindWebhook() {
    assertEquals(WebhookEntity.find(webhook).getId(), webhookEntity.getId());
  }
  
  @Test
  public void testFindUnknownWebhook() {
    when(webhook.getIdLong()).thenReturn(Long.MAX_VALUE);
    
    assertNotEquals(WebhookEntity.find(webhook).getId(), webhookEntity.getId());
  }
  
  @Test
  public void removeUnknownWebhook() {
    when(webhook.getIdLong()).thenReturn(Long.MAX_VALUE);
    assertNull(PersistenceUtil.find(WebhookEntity.class, webhook.getIdLong()));
    
    WebhookEntity.remove(webhook);

    assertNull(PersistenceUtil.find(WebhookEntity.class, webhook.getIdLong()));
  }
  
  /**
   * Use Case: The webhook is contained by the text channel. Removing it doesn't remove the channel.
   */
  @Test
  public void testRemoveWebhookKeepsChannel() {
    assertTrue(channelEntity.isPersisted());
    assertTrue(webhookEntity.isPersisted());

    WebhookEntity.remove(webhook);

    assertFalse(channelEntity.isPersisted());
    assertFalse(webhookEntity.isPersisted());
  }
  
  /**
   * Use Case: The webhook is contained by the guild. Removing it doesn't remove the guild.
   */
  @Test
  public void testRemoveWebhookKeepsGuild() {
    assertTrue(guildEntity.isPersisted());
    assertTrue(webhookEntity.isPersisted());

    WebhookEntity.remove(webhook);

    assertFalse(guildEntity.isPersisted());
    assertFalse(webhookEntity.isPersisted());
  }
}
