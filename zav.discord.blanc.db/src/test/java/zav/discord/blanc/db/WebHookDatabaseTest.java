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
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.sql.SQLException;
import java.util.List;
import java.util.NoSuchElementException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import zav.discord.blanc.databind.WebHookDto;

/**
 * Test case for the WebHook database.<br>
 * Verifies that entries are written and read correctly.
 */
public class WebHookDatabaseTest extends AbstractTest {
  
  /**
   * Deserializes all Discord entities and initializes the WebHook database.
   *
   * @throws SQLException If a database error occurred.
   */
  @BeforeEach
  public void setUp() throws SQLException {
    super.setUp();
  
    WebHookDatabase.create();
  }
  
  @Test
  public void testCreateOverExistingTable() throws SQLException {
    // Table has already been created in setUp()
    assertThat(WebHookDatabase.put(guild, hook)).isEqualTo(1);
    assertThat(WebHookDatabase.contains(guild.getId(), channel.getId(), hook.getId())).isTrue();
    // Should not replace the existing DB
    WebHookDatabase.create();
    assertThat(WebHookDatabase.contains(guild.getId(), channel.getId(), hook.getId())).isTrue();
  }
  
  @Test
  public void testContains() throws SQLException {
    assertThat(WebHookDatabase.contains(guild.getId(), channel.getId(), hook.getId())).isFalse();
    assertThat(WebHookDatabase.put(guild, hook)).isEqualTo(1);
    assertThat(WebHookDatabase.contains(guild.getId(), channel.getId(), hook.getId())).isTrue();
  }
  
  @Test
  public void testPut() throws SQLException {
    // One row has been modified
    assertThat(WebHookDatabase.put(guild, hook)).isEqualTo(1);
  }
  
  @Test
  public void testPutAlreadyExistingHook() throws SQLException {
    WebHookDatabase.put(guild, hook);
  
    WebHookDto response = WebHookDatabase.get(guild.getId(), channel.getId(), hook.getId());
    Assertions.assertThat(hook.getName()).isEqualTo(response.getName());
  
    hook.setName("Updated");
    
    WebHookDatabase.put(guild, hook);
    response = WebHookDatabase.get(guild.getId(), channel.getId(), hook.getId());
    // Old row has been updated
    Assertions.assertThat(hook.getName()).isEqualTo(response.getName());
  }
  
  @Test
  public void testDelete() throws SQLException {
    assertThat(WebHookDatabase.contains(guild.getId(), channel.getId(), hook.getId())).isFalse();
    assertThat(WebHookDatabase.put(guild, hook)).isEqualTo(1);
    assertThat(WebHookDatabase.contains(guild.getId(), channel.getId(), hook.getId())).isTrue();
    assertThat(WebHookDatabase.delete(guild.getId(), channel.getId(), hook.getId())).isEqualTo(1);
    assertThat(WebHookDatabase.contains(guild.getId(), channel.getId(), hook.getId())).isFalse();
  }
  
  @Test
  public void testDeleteAll() throws SQLException {
    assertThat(WebHookDatabase.contains(guild.getId(), channel.getId(), hook.getId())).isFalse();
    assertThat(WebHookDatabase.put(guild, hook)).isEqualTo(1);
    assertThat(WebHookDatabase.contains(guild.getId(), channel.getId(), hook.getId())).isTrue();
    assertThat(WebHookDatabase.deleteAll(guild.getId())).isEqualTo(1);
    assertThat(WebHookDatabase.contains(guild.getId(), channel.getId(), hook.getId())).isFalse();
  }
  
  @Test
  public void testDeleteUnknownHook() throws SQLException {
    // hook doesn't exist => Nothing to remove
    assertThat(WebHookDatabase.delete(guild.getId(), channel.getId(), hook.getId())).isEqualTo(0);
  }
  
  @Test
  public void testGetHook() throws SQLException {
    WebHookDatabase.put(guild, hook);
  
    WebHookDto response = WebHookDatabase.get(guild.getId(), channel.getId(), hook.getId());
    
    Assertions.assertThat(response.getId()).isEqualTo(hook.getId());
    Assertions.assertThat(response.getChannelId()).isEqualTo(hook.getChannelId());
    Assertions.assertThat(response.getName()).isEqualTo(hook.getName());
    Assertions.assertThat(response.getSubreddits()).isEqualTo(hook.getSubreddits());
  }
  
  @Test
  public void testGetAllHooks() throws SQLException {
    WebHookDatabase.put(guild, hook);
  
    List<WebHookDto> responses = WebHookDatabase.getAll(guild.getId());
  
    Assertions.assertThat(responses).hasSize(1);
  
    WebHookDto response = responses.get(0);
  
    Assertions.assertThat(response.getId()).isEqualTo(hook.getId());
    Assertions.assertThat(response.getChannelId()).isEqualTo(hook.getChannelId());
    Assertions.assertThat(response.getName()).isEqualTo(hook.getName());
    Assertions.assertThat(response.getSubreddits()).isEqualTo(hook.getSubreddits());
    Assertions.assertThat(response.isOwner()).isEqualTo(hook.isOwner());
  }
  
  @Test
  public void testGetUnknownHook() {
    long guildId = guild.getId();
    long channelId = channel.getId();
    long hookId = hook.getId();
    assertThatThrownBy(() -> WebHookDatabase.get(guildId, channelId, hookId))
          .isInstanceOf(NoSuchElementException.class);
  }
}
