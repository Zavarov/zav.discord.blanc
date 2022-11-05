package zav.discord.blanc.databind;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.Webhook;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class TextChannelEntityTest {
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
  
  /**
   * Use Case: The webhook is still referenced by the guild, so we have too keep it.
   */
  @Test
  public void testRemoveChannelKeepsWebhook() {
    assertTrue(webhookEntity.isPersisted());
    assertTrue(channelEntity.isPersisted());

    TextChannelEntity.remove(channel);

    assertFalse(webhookEntity.isPersisted());
    assertFalse(channelEntity.isPersisted());
  }
  
  /**
   * Use Case: The text channel is contained by the guild. Removing it doesn't remove the guild.
   */
  @Test
  public void testRemoveChannelKeepsGuild() {
    assertTrue(guildEntity.isPersisted());
    assertTrue(channelEntity.isPersisted());

    TextChannelEntity.remove(channel);

    assertFalse(guildEntity.isPersisted());
    assertFalse(channelEntity.isPersisted());
  }
}
