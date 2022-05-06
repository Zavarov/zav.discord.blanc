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

package zav.discord.blanc.db.test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static zav.test.io.JsonUtils.read;

import java.sql.SQLException;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.Webhook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import zav.discord.blanc.databind.WebhookEntity;
import zav.discord.blanc.db.WebhookTable;

/**
 * Test case for the Webhook database.<br>
 * Verifies that entries are written and read correctly.
 */
public class WebhookTableTest extends AbstractTableTest {
  
  WebhookTable db;
  WebhookEntity entity;
  
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
  
    db = new WebhookTable();
    db.setSqlQuery(query);
    db.postConstruct();
    
    entity = read("Webhook.json", WebhookEntity.class);
  }
  
  @Test
  public void testPut() throws SQLException {
    when(guild.getIdLong()).thenReturn(entity.getGuildId());
    when(textChannel.getIdLong()).thenReturn(entity.getChannelId());
    when(webhook.getGuild()).thenReturn(guild);
    when(webhook.getChannel()).thenReturn(textChannel);
    when(webhook.getIdLong()).thenReturn(entity.getId());
  
    assertEquals(db.put(entity), 1);
    assertThat(db.get(webhook)).map(WebhookEntity::getName).contains(entity.getName());
  
    entity.setName("NotWebhook");
  
    assertEquals(db.put(entity), 1);
    assertThat(db.get(guild)).map(WebhookEntity::getName).contains(entity.getName());
  }
  
  @Test
  public void testDeleteGuild() throws SQLException {
    when(guild.getIdLong()).thenReturn(entity.getGuildId());
  
    assertEquals(db.put(entity), 1);
    assertEquals(db.delete(guild), 1);
    assertEquals(db.delete(guild), 0);
  }
  
  @Test
  public void testDeleteTextChannel() throws SQLException {
    when(guild.getIdLong()).thenReturn(entity.getGuildId());
    when(textChannel.getIdLong()).thenReturn(entity.getChannelId());
    when(textChannel.getGuild()).thenReturn(guild);
  
    assertEquals(db.put(entity), 1);
    assertEquals(db.delete(textChannel), 1);
    assertEquals(db.delete(textChannel), 0);
  }
  
  @Test
  public void testDeleteWebhook() throws SQLException {
    when(guild.getIdLong()).thenReturn(entity.getGuildId());
    when(textChannel.getIdLong()).thenReturn(entity.getChannelId());
    when(webhook.getGuild()).thenReturn(guild);
    when(webhook.getChannel()).thenReturn(textChannel);
    when(webhook.getIdLong()).thenReturn(entity.getId());
  
    assertEquals(db.put(entity), 1);
    assertEquals(db.delete(webhook), 1);
    assertEquals(db.delete(webhook), 0);
  }
  
  @Test
  public void testGetGuild() throws SQLException {
    db.put(entity);
    
    assertThat(db.get(guild)).isEmpty();
    when(guild.getIdLong()).thenReturn(entity.getGuildId());
    assertThat(db.get(guild)).contains(entity);
  }
  
  @Test
  public void testGetTextChannel() throws SQLException {
    when(textChannel.getGuild()).thenReturn(guild);
    
    db.put(entity);
    
    assertThat(db.get(textChannel)).isEmpty();
    when(guild.getIdLong()).thenReturn(entity.getGuildId());
    when(textChannel.getIdLong()).thenReturn(entity.getChannelId());
    assertThat(db.get(textChannel)).contains(entity);
  }
  
  @Test
  public void testGetWebhook() throws SQLException {
    when(webhook.getGuild()).thenReturn(guild);
    when(webhook.getChannel()).thenReturn(textChannel);
  
    db.put(entity);
  
    assertThat(db.get(webhook)).isEmpty();
    when(guild.getIdLong()).thenReturn(entity.getGuildId());
    when(textChannel.getIdLong()).thenReturn(entity.getChannelId());
    when(webhook.getIdLong()).thenReturn(entity.getId());
    assertThat(db.get(webhook)).contains(entity);
  }
}
