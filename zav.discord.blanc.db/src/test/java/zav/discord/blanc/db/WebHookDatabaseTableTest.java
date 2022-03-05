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
import static zav.test.io.JsonUtils.read;

import java.sql.SQLException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import zav.discord.blanc.databind.WebHookEntity;

/**
 * Test case for the WebHook database.<br>
 * Verifies that entries are written and read correctly.
 */
public class WebHookDatabaseTableTest extends AbstractDatabaseTableTest {
  
  WebHookDatabaseTable db;
  WebHookEntity hook;
  
  /**
   * Deserializes Discord Webhook and initializes database.
   *
   * @throws SQLException If a database error occurred.
   */
  @BeforeEach
  public void setUp() throws SQLException {
    super.setUp();
    db = guice.getInstance(WebHookDatabaseTable.class);
    hook = read("WebHook.json", WebHookEntity.class);
  }
  
  @Test
  public void testContains() throws SQLException {
    assertThat(db.contains(hook.getGuildId(), hook.getChannelId(), hook.getId())).isFalse();
    assertThat(db.put(hook)).isEqualTo(1);
    assertThat(db.contains(hook.getGuildId(), hook.getChannelId(), hook.getId())).isTrue();
  }
  
  @Test
  public void testPut() throws SQLException {
    // One row has been modified
    assertThat(db.put(hook)).isEqualTo(1);
  }
  
  @Test
  public void testPutUpdateHook() throws SQLException {
    db.put(hook);
  
    WebHookEntity response = get(db, hook.getGuildId(), hook.getChannelId(), hook.getId());
    assertThat(hook.getName()).isEqualTo(response.getName());
  
    hook.setName("Updated");
    db.put(hook);
    
    response = get(db, hook.getGuildId(), hook.getChannelId(), hook.getId());
    // Old row has been updated
    assertThat(hook.getName()).isEqualTo(response.getName());
  }
  
  @Test
  public void testDelete() throws SQLException {
    assertThat(db.contains(hook.getGuildId(), hook.getChannelId(), hook.getId())).isFalse();
    assertThat(db.put(hook)).isEqualTo(1);
    assertThat(db.contains(hook.getGuildId(), hook.getChannelId(), hook.getId())).isTrue();
    assertThat(db.delete(hook.getGuildId(), hook.getChannelId(), hook.getId())).isEqualTo(1);
    assertThat(db.contains(hook.getGuildId(), hook.getChannelId(), hook.getId())).isFalse();
  }
  
  @Test
  public void testDeleteInvalidArgument() {
    Object[] args = new Object[] {Long.MAX_VALUE, Long.MAX_VALUE, Long.MAX_VALUE, Long.MAX_VALUE};
    assertThatThrownBy(() -> db.delete()).isInstanceOf(IllegalArgumentException.class);
    assertThatThrownBy(() -> db.delete(args)).isInstanceOf(IllegalArgumentException.class);
  }
  
  @Test
  public void testDeleteAllFromChannel() throws SQLException {
    assertThat(db.contains(hook.getGuildId(), hook.getChannelId(), hook.getId())).isFalse();
    assertThat(db.put(hook)).isEqualTo(1);
    assertThat(db.contains(hook.getGuildId(), hook.getChannelId(), hook.getId())).isTrue();
    assertThat(db.delete(hook.getGuildId(), hook.getChannelId())).isEqualTo(1);
    assertThat(db.contains(hook.getGuildId(), hook.getChannelId(), hook.getId())).isFalse();
  }
  
  @Test
  public void testDeleteAllFromGuild() throws SQLException {
    assertThat(db.contains(hook.getGuildId(), hook.getChannelId(), hook.getId())).isFalse();
    assertThat(db.put(hook)).isEqualTo(1);
    assertThat(db.contains(hook.getGuildId(), hook.getChannelId(), hook.getId())).isTrue();
    assertThat(db.delete(hook.getGuildId())).isEqualTo(1);
    assertThat(db.contains(hook.getGuildId(), hook.getChannelId(), hook.getId())).isFalse();
  }
  
  @Test
  public void testDeleteUnknownHook() throws SQLException {
    // hook doesn't exist => Nothing to remove
    assertThat(db.delete(hook.getGuildId(), hook.getChannelId(), hook.getId())).isEqualTo(0);
  }
  
  @Test
  public void testGet() throws SQLException {
    db.put(hook);
  
    WebHookEntity response = get(db, hook.getGuildId(), hook.getChannelId());
    
    assertThat(response.getId()).isEqualTo(hook.getId());
    assertThat(response.getChannelId()).isEqualTo(hook.getChannelId());
    assertThat(response.getName()).isEqualTo(hook.getName());
    assertThat(response.getSubreddits()).isEqualTo(hook.getSubreddits());
  }
  
  @Test
  public void testGetInvalidArgument() {
    Object[] args = new Object[] {Long.MAX_VALUE, Long.MAX_VALUE, Long.MAX_VALUE, Long.MAX_VALUE};
    assertThatThrownBy(() -> db.get()).isInstanceOf(IllegalArgumentException.class);
    assertThatThrownBy(() -> db.get(args)).isInstanceOf(IllegalArgumentException.class);
  }
  
  @Test
  public void testGetAllFromGuild() throws SQLException {
    db.put(hook);
  
    WebHookEntity response = get(db, hook.getGuildId());
  
    assertThat(response.getId()).isEqualTo(hook.getId());
    assertThat(response.getChannelId()).isEqualTo(hook.getChannelId());
    assertThat(response.getName()).isEqualTo(hook.getName());
    assertThat(response.getSubreddits()).isEqualTo(hook.getSubreddits());
    assertThat(response.isOwner()).isEqualTo(hook.isOwner());
  }
  
  @Test
  public void testGetAllFromChannel() throws SQLException {
    db.put(hook);
    
    WebHookEntity response = get(db, hook.getGuildId(), hook.getChannelId());
    
    assertThat(response.getId()).isEqualTo(hook.getId());
    assertThat(response.getChannelId()).isEqualTo(hook.getChannelId());
    assertThat(response.getName()).isEqualTo(hook.getName());
    assertThat(response.getSubreddits()).isEqualTo(hook.getSubreddits());
    assertThat(response.isOwner()).isEqualTo(hook.isOwner());
  }
  
  @Test
  public void testGetUnknownHook() throws SQLException {
    assertThat(db.get(Long.MAX_VALUE, Long.MAX_VALUE)).isEmpty();
  }
}
