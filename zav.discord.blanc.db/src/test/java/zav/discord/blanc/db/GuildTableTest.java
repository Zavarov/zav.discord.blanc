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
import java.util.NoSuchElementException;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import zav.discord.blanc.databind.GuildValueObject;

/**
 * Test case for the Guild database.<br>
 * Verifies that entries are written and read correctly.
 */
public class GuildTableTest extends AbstractTest {
  
  /**
   * Deserializes all Discord entities and initializes the Guild database.
   *
   * @throws SQLException If a database error occurred.
   */
  @BeforeEach
  public void setUp() throws SQLException {
    super.setUp();
    
    GuildTable.create();
  }
  
  @Test
  public void testCreateOverExistingTable() throws SQLException {
    // Table has already been created in setUp()
    assertThat(GuildTable.put(guild)).isEqualTo(1);
    assertThat(GuildTable.contains(guild.getId())).isTrue();
    // Should not replace the existing DB
    GuildTable.create();
    assertThat(GuildTable.contains(guild.getId())).isTrue();
  }
  
  @Test
  public void testContains() throws SQLException {
    assertThat(GuildTable.contains(guild.getId())).isFalse();
    assertThat(GuildTable.put(guild)).isEqualTo(1);
    assertThat(GuildTable.contains(guild.getId())).isTrue();
  }
  
  @Test
  public void testPut() throws SQLException {
    // One row has been modified
    assertThat(GuildTable.put(guild)).isEqualTo(1);
  }
  
  @Test
  public void testPutAlreadyExistingGuild() throws SQLException {
    GuildTable.put(guild);
    
    GuildValueObject response = GuildTable.get(guild.getId());
    assertThat(guild.getName()).isEqualTo(response.getName());
    
    guild.setName("Updated");
  
    GuildTable.put(guild);
    response = GuildTable.get(guild.getId());
    // Old row has been updated
    assertThat(guild.getName()).isEqualTo(response.getName());
  }
  
  @Test
  public void testDelete() throws SQLException {
    assertThat(GuildTable.contains(guild.getId())).isFalse();
    assertThat(GuildTable.put(guild)).isEqualTo(1);
    assertThat(GuildTable.contains(guild.getId())).isTrue();
    assertThat(GuildTable.delete(guild.getId())).isEqualTo(1);
    assertThat(GuildTable.contains(guild.getId())).isFalse();
  }
  
  @Test
  public void testDeleteUnknownGuild() throws SQLException {
    // Guild doesn't exist => Nothing to remove
    assertThat(GuildTable.delete(guild.getId())).isEqualTo(0);
  }
  
  @Test
  public void testGetGuild() throws SQLException {
    GuildTable.put(guild);
    
    GuildValueObject response = GuildTable.get(guild.getId());
    
    Assertions.assertThat(response.getId()).isEqualTo(guild.getId());
    Assertions.assertThat(response.getName()).isEqualTo(guild.getName());
    Assertions.assertThat(response.getPrefix()).isEqualTo(guild.getPrefix());
    Assertions.assertThat(response.getBlacklist()).isEqualTo(guild.getBlacklist());
  }
  
  @Test
  public void testGetUnknownGuild() {
    assertThrows(NoSuchElementException.class, () -> GuildTable.get(guild.getId()));
  }
}
