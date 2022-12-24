/*
 * Copyright (c) 2022 Zavarov.
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
public class GuildEntityTest {
  // Arbitrary but fixed. Because of GenerationType.IDENTITY, the first element gets the id 1
  private static final long AUTORESPONSE_ID = 0L;

  GuildEntity guildEntity;
  TextChannelEntity channelEntity;
  WebhookEntity webhookEntity;
  AutoResponseEntity responseEntity;

  @Mock Guild guild;
  @Mock TextChannel channel;
  @Mock Webhook webhook;

  @BeforeEach
  public void setUp() {
    guildEntity = GuildEntity.find(guild);
    channelEntity = TextChannelEntity.find(channel);
    webhookEntity = WebhookEntity.find(webhook);
    responseEntity = AutoResponseEntity.create("foo", "bar");

    guildEntity.add(webhookEntity);
    guildEntity.add(responseEntity);
    guildEntity.add(channelEntity);
    guildEntity.merge();
  }
  
  @AfterEach
  public void tearDown() {
    GuildEntity.remove(guild);
  }
  
  @Test
  public void testFindGuild() {
    assertEquals(GuildEntity.find(guild).getId(), guildEntity.getId());
  }
  
  @Test
  public void testFindUnknownGuild() {
    when(guild.getIdLong()).thenReturn(Long.MAX_VALUE);
    
    assertNotEquals(GuildEntity.find(guild).getId(), guildEntity.getId());
  }
  
  @Test
  public void removeUnknownGuild() {
    when(guild.getIdLong()).thenReturn(Long.MAX_VALUE);
    assertNull(PersistenceUtil.find(GuildEntity.class, guild.getIdLong()));
    
    GuildEntity.remove(guild);
    
    assertNull(PersistenceUtil.find(GuildEntity.class, guild.getIdLong()));
  }
  
  /**
   * Use Case: Removing a guild should also remove its text channels.
   */
  @Test
  public void testRemoveGuildRemovesTextChannel() {
    assertTrue(guildEntity.isPersisted());
    assertTrue(channelEntity.isPersisted());
    
    GuildEntity.remove(guild);
    
    assertFalse(guildEntity.isPersisted());
    assertFalse(channelEntity.isPersisted());
  }
  
  /**
   * Use Case: Removing a guild should also remove its webhooks.
   */
  @Test
  public void testRemoveGuildRemovesWebhook() {
    assertTrue(guildEntity.isPersisted());
    assertTrue(webhookEntity.isPersisted());
    
    GuildEntity.remove(guild);
    
    assertFalse(guildEntity.isPersisted());
    assertFalse(webhookEntity.isPersisted());
  }
  
  /**
   * Use Case: Removing a guild should also remove its auto responses.
   */
  @Test
  public void testRemoveGuildRemovesAutoResponse() {
    assertTrue(guildEntity.isPersisted());
    assertTrue(responseEntity.isPersisted());
    
    GuildEntity.remove(guild);
    
    assertFalse(guildEntity.isPersisted());
    assertFalse(responseEntity.isPersisted());
  }
  
  /**
   * Use Case: Updating a guild should also update referenced text channels.
   */
  @Test
  public void testMergeGuildUpdatesTextChannel() {
    channelEntity.setName("foo");

    guildEntity.merge();

    assertEquals(PersistenceUtil.find(TextChannelEntity.class, channel.getIdLong()).getName(), "foo");
  }
  
  /**
   * Use Case: Updating a guild should also update referenced webhooks.
   */
  @Test
  public void testMergeGuildUpdatesWebhook() {
    webhookEntity.setName("foo");

    guildEntity.merge();

    assertEquals(PersistenceUtil.find(WebhookEntity.class, webhook.getIdLong()).getName(), "foo");
  }
  
  /**
   * Use Case: Updating a guild should also update referenced responses.
   */
  @Test
  public void testMergeGuildUpdatesAutoResponse() {
    responseEntity.setPattern("test");

    guildEntity.merge();

    assertEquals(PersistenceUtil.find(AutoResponseEntity.class, AUTORESPONSE_ID).getPattern(), "test");
  }
}
