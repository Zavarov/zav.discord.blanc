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

package zav.discord.blanc.db;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static zav.discord.blanc.db.sql.SqlQuery.ENTITY_DB_PATH;
import static zav.test.io.JsonUtils.read;

import java.sql.SQLException;
import java.util.List;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.Webhook;
import net.dv8tion.jda.api.requests.RestAction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import zav.discord.blanc.databind.WebhookEntity;

/**
 * Test case for the Webhook database.<br>
 * Verifies that entries are written and read correctly.
 */
public class WebhookTableTest extends AbstractTableTest {
  
  WebhookTable db;
  WebhookEntity entity;
  
  @Mock RestAction<List<Webhook>> action;
  @Mock Webhook webhook;
  @Mock TextChannel textChannel;
  @Mock Guild guild;
  
  /**
   * Deserializes Discord Webhook and initializes database.
   *
   * @throws Exception If the database couldn't be initialized.
   */
  @BeforeEach
  public void setUp() throws Exception {
    super.setUp();
  
    db = new WebhookTable(query);
    db.postConstruct();
    
    entity = read("Webhook.json", WebhookEntity.class);
  }
  
  @Test
  public void testPut() throws SQLException {
    when(guild.getId()).thenReturn(Long.toString(entity.getGuildId()));
    when(textChannel.getId()).thenReturn(Long.toString(entity.getChannelId()));
    when(webhook.getGuild()).thenReturn(guild);
    when(webhook.getChannel()).thenReturn(textChannel);
    when(webhook.getId()).thenReturn(Long.toString(entity.getId()));
  
    assertEquals(db.put(entity), 1);
    assertThat(db.get(webhook)).map(WebhookEntity::getName).contains(entity.getName());
  
    entity.setName("NotWebhook");
  
    assertEquals(db.put(entity), 1);
    assertThat(db.get(guild)).map(WebhookEntity::getName).contains(entity.getName());
  }
  
  @Test
  public void testDeleteGuild() throws SQLException {
    when(guild.getId()).thenReturn(Long.toString(entity.getGuildId()));
    
    assertEquals(db.put(entity), 1);
    assertEquals(db.delete(guild), 1);
    assertEquals(db.delete(guild), 0);
  }
  
  @Test
  public void testDeleteTextChannel() throws SQLException {
    when(guild.getId()).thenReturn(Long.toString(entity.getGuildId()));
    when(textChannel.getId()).thenReturn(Long.toString(entity.getChannelId()));
    when(textChannel.getGuild()).thenReturn(guild);
  
    assertEquals(db.put(entity), 1);
    assertEquals(db.delete(textChannel), 1);
    assertEquals(db.delete(textChannel), 0);
  }
  
  @Test
  public void testDeleteWebhook() throws SQLException {
    when(guild.getId()).thenReturn(Long.toString(entity.getGuildId()));
    when(textChannel.getId()).thenReturn(Long.toString(entity.getChannelId()));
    when(webhook.getGuild()).thenReturn(guild);
    when(webhook.getChannel()).thenReturn(textChannel);
    when(webhook.getId()).thenReturn(Long.toString(entity.getId()));
  
    assertEquals(db.put(entity), 1);
    assertEquals(db.delete(webhook), 1);
    assertEquals(db.delete(webhook), 0);
  }
  
  @Test
  public void testGetGuild() throws SQLException {
    when(guild.getId()).thenReturn(Long.toString(entity.getGuildId()));
    
    assertThat(db.get(guild)).isEmpty();
    db.put(entity);
    assertThat(db.get(guild)).contains(entity);
  }
  
  @Test
  public void testGetTextChannel() throws SQLException {
    when(guild.getId()).thenReturn(Long.toString(entity.getGuildId()));
    when(textChannel.getGuild()).thenReturn(guild);
    
    db.put(entity);
    
    assertThat(db.get(textChannel)).isEmpty();
    when(textChannel.getId()).thenReturn(Long.toString(entity.getChannelId()));
    assertThat(db.get(textChannel)).contains(entity);
  }
  
  @Test
  public void testGetWebhook() throws SQLException {
    when(guild.getId()).thenReturn(Long.toString(entity.getGuildId()));
    when(webhook.getGuild()).thenReturn(guild);
    when(webhook.getChannel()).thenReturn(textChannel);
  
    db.put(entity);
  
    assertThat(db.get(webhook)).isEmpty();
    when(textChannel.getId()).thenReturn(Long.toString(entity.getChannelId()));
    when(webhook.getId()).thenReturn(Long.toString(entity.getId()));
    assertThat(db.get(webhook)).contains(entity);
  }
  
  @Test
  public void testContains() throws SQLException {
    when(guild.getId()).thenReturn(Long.toString(entity.getGuildId()));
    when(webhook.getGuild()).thenReturn(guild);
    when(webhook.getChannel()).thenReturn(textChannel);
    when(webhook.getId()).thenReturn(Long.toString(entity.getId()));
    when(textChannel.getId()).thenReturn(Long.toString(entity.getChannelId()));
    when(webhook.getId()).thenReturn(Long.toString(entity.getId()));
    
    assertFalse(db.contains(webhook));
    
    db.put(entity);
    
    assertTrue(db.contains(webhook));
  }
  
  @Test
  public void testRetain() throws SQLException {
    when(webhook.getGuild()).thenReturn(guild);
    when(webhook.getChannel()).thenReturn(textChannel);
    when(webhook.getId()).thenReturn(Long.toString(entity.getId()));
    when(guild.getTextChannels()).thenReturn(List.of(textChannel));
    when(guild.getId()).thenReturn(Long.toString(entity.getGuildId()));
    when(textChannel.retrieveWebhooks()).thenReturn(action);
    when(textChannel.getId()).thenReturn(Long.toString(entity.getChannelId()));
    when(action.complete()).thenReturn(List.of(webhook));
  
    when(textChannel.canTalk()).thenReturn(false);
    db.put(entity);
  
    db.retain(guild);
    assertFalse(db.contains(webhook));
  
    when(textChannel.canTalk()).thenReturn(true);
    db.put(entity);
    
    db.retain(guild);
    assertTrue(db.contains(webhook));
  
    when(action.complete()).thenReturn(List.of(webhook, mock(Webhook.class)));
    db.retain(guild);
    assertTrue(db.contains(webhook));
  
    when(guild.getTextChannels()).thenReturn(List.of());
    db.retain(guild);
    assertFalse(db.contains(webhook));
  }
  
  @Test
  public void testPostConstruct() throws Exception {
    long lastModified = ENTITY_DB_PATH.toFile().lastModified();
    
    db.postConstruct();
    
    // Database should not be overwritten
    assertEquals(ENTITY_DB_PATH.toFile().lastModified(), lastModified);
  }
}
