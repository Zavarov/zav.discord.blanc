/*
 * Copyright (c) 2021 Zavarov.
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
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.sql.SQLException;
import java.util.List;
import java.util.NoSuchElementException;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import zav.discord.blanc.databind.TextChannelValueObject;

/**
 * Test case for the TextChannel database.<br>
 * Verifies that entries are written and read correctly.
 */
public class TextChannelTableTest extends AbstractTest {
  
  /**
   * Deserializes all Discord entities and initializes the TextChannel database.
   *
   * @throws SQLException If a database error occurred.
   */
  @BeforeEach
  public void setUp() throws SQLException {
    super.setUp();
  
    TextChannelTable.create();
  }
  
  @Test
  public void testCreateOverExistingTable() throws SQLException {
    // Table has already been created in setUp()
    assertThat(TextChannelTable.put(guild, channel)).isEqualTo(1);
    assertThat(TextChannelTable.contains(guild.getId(), channel.getId())).isTrue();
    // Should not replace the existing DB
    TextChannelTable.create();
    assertThat(TextChannelTable.contains(guild.getId(), channel.getId())).isTrue();
  }
  
  @Test
  public void testContains() throws SQLException {
    assertThat(TextChannelTable.contains(guild.getId(), channel.getId())).isFalse();
    assertThat(TextChannelTable.put(guild, channel)).isEqualTo(1);
    assertThat(TextChannelTable.contains(guild.getId(), channel.getId())).isTrue();
  }
  
  @Test
  public void testPut() throws SQLException {
    // One row has been modified
    assertThat(TextChannelTable.put(guild, channel)).isEqualTo(1);
  }
  
  @Test
  public void testPutAlreadyExistingChannel() throws SQLException {
    TextChannelTable.put(guild, channel);
  
    TextChannelValueObject response = TextChannelTable.get(guild.getId(), channel.getId());
    assertThat(channel.getName()).isEqualTo(response.getName());
  
    channel.setName("Updated");
    
    TextChannelTable.put(guild, channel);
    response = TextChannelTable.get(guild.getId(), channel.getId());
    // Old row has been updated
    assertThat(channel.getName()).isEqualTo(response.getName());
  }
  
  @Test
  public void testDelete() throws SQLException {
    assertThat(TextChannelTable.contains(guild.getId(), channel.getId())).isFalse();
    assertThat(TextChannelTable.put(guild, channel)).isEqualTo(1);
    assertThat(TextChannelTable.contains(guild.getId(), channel.getId())).isTrue();
    assertThat(TextChannelTable.delete(guild.getId(), channel.getId())).isEqualTo(1);
    assertThat(TextChannelTable.contains(guild.getId(), channel.getId())).isFalse();
  }
  
  @Test
  public void testDeleteAll() throws SQLException {
    assertThat(TextChannelTable.contains(guild.getId(), channel.getId())).isFalse();
    assertThat(TextChannelTable.put(guild, channel)).isEqualTo(1);
    assertThat(TextChannelTable.contains(guild.getId(), channel.getId())).isTrue();
    assertThat(TextChannelTable.deleteAll(guild.getId())).isEqualTo(1);
    assertThat(TextChannelTable.contains(guild.getId(), channel.getId())).isFalse();
  }
  
  @Test
  public void testDeleteUnknownChannel() throws SQLException {
    // channel doesn't exist => Nothing to remove
    assertThat(TextChannelTable.delete(guild.getId(), channel.getId())).isEqualTo(0);
  }
  
  @Test
  public void testGetChannel() throws SQLException {
    TextChannelTable.put(guild, channel);
  
    TextChannelValueObject response = TextChannelTable.get(guild.getId(), channel.getId());
    
    Assertions.assertThat(response.getId()).isEqualTo(channel.getId());
    Assertions.assertThat(response.getName()).isEqualTo(channel.getName());
    Assertions.assertThat(response.getSubreddits()).isEqualTo(channel.getSubreddits());
  }
  
  @Test
  public void testGetAllChannels() throws SQLException {
    TextChannelTable.put(guild, channel);
  
    List<TextChannelValueObject> responses = TextChannelTable.getAll(guild.getId());
  
    Assertions.assertThat(responses).hasSize(1);
  
    TextChannelValueObject response = responses.get(0);
  
    Assertions.assertThat(response.getId()).isEqualTo(channel.getId());
    Assertions.assertThat(response.getName()).isEqualTo(channel.getName());
    Assertions.assertThat(response.getSubreddits()).isEqualTo(channel.getSubreddits());
  }
  
  @Test
  public void testGetUnknownChannel() {
    long guildId = guild.getId();
    long channelId = channel.getId();
    assertThrows(NoSuchElementException.class, () -> TextChannelTable.get(guildId, channelId));
  }
}
