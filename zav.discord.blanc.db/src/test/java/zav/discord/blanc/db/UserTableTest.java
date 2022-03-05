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
import static zav.test.io.JsonUtils.read;

import java.sql.SQLException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import zav.discord.blanc.databind.UserEntity;

/**
 * Test case for the User database.<br>
 * Verifies that entries are written and read correctly.
 */
public class UserTableTest extends AbstractTableTest {
  
  UserTable db;
  UserEntity user;
  
  /**
   * Deserializes Discord user and initializes database.
   *
   * @throws SQLException If a database error occurred.
   */
  @BeforeEach
  public void setUp() throws SQLException {
    super.setUp();
    db = guice.getInstance(UserTable.class);
    user = read("User.json", UserEntity.class);
  }
  
  @Test
  public void testContains() throws SQLException {
    assertThat(db.contains(user.getId())).isFalse();
    assertThat(db.put(user)).isEqualTo(1);
    assertThat(db.contains(user.getId())).isTrue();
  }
  
  @Test
  public void testPut() throws SQLException {
    // One row has been modified
    assertThat(db.put(user)).isEqualTo(1);
  }
  
  @Test
  public void testPutUpdateUser() throws SQLException {
    db.put(user);
  
    UserEntity response = get(db, user.getId());
    assertThat(user.getName()).isEqualTo(response.getName());
    
    user.setName("Updated");
    db.put(user);
    
    response = get(db, user.getId());
    // Old row has been updated
    assertThat(user.getName()).isEqualTo(response.getName());
  }
  
  @Test
  public void testDelete() throws SQLException {
    assertThat(db.contains(user.getId())).isFalse();
    assertThat(db.put(user)).isEqualTo(1);
    assertThat(db.contains(user.getId())).isTrue();
    assertThat(db.delete(user.getId())).isEqualTo(1);
    assertThat(db.contains(user.getId())).isFalse();
  }
  
  @Test
  public void testDeleteUnknownUser() throws SQLException {
    // User doesn't exist => Nothing to remove
    assertThat(db.delete(user.getId())).isEqualTo(0);
  }
  
  @Test
  public void testGetUser() throws SQLException {
    db.put(user);
  
    UserEntity response = get(db, user.getId());
    
    assertThat(response.getId()).isEqualTo(user.getId());
    assertThat(response.getName()).isEqualTo(user.getName());
    assertThat(response.getDiscriminator()).isEqualTo(user.getDiscriminator());
    assertThat(response.getRanks()).isEqualTo(user.getRanks());
  }
  
  @Test
  public void testGetUnknownUser() throws SQLException {
    assertThat(db.get(user.getId())).isEmpty();
  }
}
