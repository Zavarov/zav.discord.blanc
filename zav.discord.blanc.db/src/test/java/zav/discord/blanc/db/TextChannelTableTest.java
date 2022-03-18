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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static zav.test.io.JsonUtils.read;

import java.sql.SQLException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import zav.discord.blanc.databind.TextChannelEntity;

/**
 * Test case for the TextChannel database.<br>
 * Verifies that entries are written and read correctly.
 */
public class TextChannelTableTest extends AbstractTableTest {
  
  TextChannelTable db;
  TextChannelEntity channel;
  
  /**
   * Deserializes Discord text channel and initializes database.
   *
   * @throws SQLException If a database error occurred.
   */
  @BeforeEach
  public void setUp() throws SQLException {
    super.setUp();
    db = guice.getInstance(TextChannelTable.class);
    channel = read("TextChannel.json", TextChannelEntity.class);
  }
  
  @Test
  public void testContains() throws SQLException {
    assertThat(db.contains(channel.getGuildId(), channel.getId())).isFalse();
    assertThat(db.put(channel)).isEqualTo(1);
    assertThat(db.contains(channel.getGuildId(), channel.getId())).isTrue();
  }
  
  @Test
  public void testPut() throws SQLException {
    // One row has been modified
    assertThat(db.put(channel)).isEqualTo(1);
  }
  
  @Test
  public void testPutUpdateTextChannel() throws SQLException {
    db.put(channel);
  
    TextChannelEntity response = get(db, channel.getGuildId(), channel.getId());
    assertThat(channel.getName()).isEqualTo(response.getName());
  
    channel.setName("Updated");
    db.put(channel);

    response = get(db, channel.getGuildId(), channel.getId());
    // Old row has been updated
    assertThat(channel.getName()).isEqualTo(response.getName());
  }
  
  @Test
  public void testDelete() throws SQLException {
    assertThat(db.contains(channel.getGuildId(), channel.getId())).isFalse();
    assertThat(db.put(channel)).isEqualTo(1);
    assertThat(db.contains(channel.getGuildId(), channel.getId())).isTrue();
    assertThat(db.delete(channel.getGuildId(), channel.getId())).isEqualTo(1);
    assertThat(db.contains(channel.getGuildId(), channel.getId())).isFalse();
  }
  
  @Test
  public void testDeleteInvalidArgument() {
    Object[] args = new Object[] {Long.MAX_VALUE, Long.MAX_VALUE, Long.MAX_VALUE};
    assertThatThrownBy(() -> db.delete()).isInstanceOf(IllegalArgumentException.class);
    assertThatThrownBy(() -> db.delete(args)).isInstanceOf(IllegalArgumentException.class);
  }
  
  @Test
  public void testDeleteAllFromGuild() throws SQLException {
    assertThat(db.contains(channel.getGuildId(), channel.getId())).isFalse();
    assertThat(db.put(channel)).isEqualTo(1);
    assertThat(db.contains(channel.getGuildId(), channel.getId())).isTrue();
    assertThat(db.delete(channel.getGuildId())).isEqualTo(1);
    assertThat(db.contains(channel.getGuildId(), channel.getId())).isFalse();
  }
  
  @Test
  public void testDeleteUnknownChannel() throws SQLException {
    // channel doesn't exist => Nothing to remove
    assertThat(db.delete(channel.getGuildId(), channel.getId())).isEqualTo(0);
  }
  
  @Test
  public void testGetChannel() throws SQLException {
    db.put(channel);
  
    TextChannelEntity response = get(db, channel.getGuildId(), channel.getId());
    
    assertThat(response.getId()).isEqualTo(channel.getId());
    assertThat(response.getName()).isEqualTo(channel.getName());
    assertThat(response.getSubreddits()).isEqualTo(channel.getSubreddits());
  }
  
  @Test
  public void testGetInvalidArgument() {
    Object[] args = new Object[] {Long.MAX_VALUE, Long.MAX_VALUE, Long.MAX_VALUE};
    assertThatThrownBy(() -> db.get()).isInstanceOf(IllegalArgumentException.class);
    assertThatThrownBy(() -> db.get(args)).isInstanceOf(IllegalArgumentException.class);
  }
  
  @Test
  public void testGetAllChannels() throws SQLException {
    db.put(channel);
  
    TextChannelEntity response = get(db, channel.getGuildId());
  
    assertThat(response.getId()).isEqualTo(channel.getId());
    assertThat(response.getName()).isEqualTo(channel.getName());
    assertThat(response.getSubreddits()).isEqualTo(channel.getSubreddits());
  }
  
  @Test
  public void testGetUnknownChannel() throws SQLException {
    assertThat(db.get(channel.getGuildId(), channel.getId())).isEmpty();
  }
}
