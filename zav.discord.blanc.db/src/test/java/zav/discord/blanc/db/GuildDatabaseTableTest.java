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

import java.sql.SQLException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import zav.discord.blanc.databind.GuildEntity;

/**
 * Test case for the Guild database.<br>
 * Verifies that entries are written and read correctly.
 */
public class GuildDatabaseTableTest extends AbstractDatabaseTableTest {
  
  GuildDatabaseTable db;
  GuildEntity guild;
  
  /**
   * Deserializes Discord guild and initializes database.
   *
   * @throws SQLException If a database error occurred.
   */
  @BeforeEach
  public void setUp() throws SQLException {
    super.setUp();
    db = guice.getInstance(GuildDatabaseTable.class);
    guild = read("Guild.json", GuildEntity.class);
  }
  
  @Test
  public void testContains() throws SQLException {
    assertThat(db.contains(guild.getId())).isFalse();
    assertThat(db.put(guild)).isEqualTo(1);
    assertThat(db.contains(guild.getId())).isTrue();
  }
  
  @Test
  public void testPut() throws SQLException {
    // One row has been modified
    assertThat(db.put(guild)).isEqualTo(1);
  }
  
  @Test
  public void testPutUpdateGuild() throws SQLException {
    db.put(guild);
    
    GuildEntity response = get(db, guild.getId());
    assertThat(guild.getName()).isEqualTo(response.getName());
    
    guild.setName("Updated");
    db.put(guild);
    
    response = get(db, guild.getId());
    // Old row has been updated
    assertThat(guild.getName()).isEqualTo(response.getName());
  }
  
  @Test
  public void testDelete() throws SQLException {
    assertThat(db.contains(guild.getId())).isFalse();
    assertThat(db.put(guild)).isEqualTo(1);
    assertThat(db.contains(guild.getId())).isTrue();
    assertThat(db.delete(guild.getId())).isEqualTo(1);
    assertThat(db.contains(guild.getId())).isFalse();
  }
  
  @Test
  public void testDeleteUnknownGuild() throws SQLException {
    // Guild doesn't exist => Nothing to remove
    assertThat(db.delete(guild.getId())).isEqualTo(0);
  }
  
  @Test
  public void testGetGuild() throws SQLException {
    db.put(guild);
  
    GuildEntity entity = get(db, guild.getId());
    
    assertThat(entity.getId()).isEqualTo(guild.getId());
    assertThat(entity.getName()).isEqualTo(guild.getName());
    assertThat(entity.getPrefix()).isEqualTo(guild.getPrefix());
    assertThat(entity.getBlacklist()).isEqualTo(guild.getBlacklist());
  }
  
  @Test
  public void testGetUnknownGuild() throws SQLException {
    assertThat(db.get(guild.getId())).isEmpty();
  }
}
