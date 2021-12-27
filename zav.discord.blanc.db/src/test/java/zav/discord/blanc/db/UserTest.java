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
import zav.discord.blanc.databind.UserValueObject;

/**
 * Test case for the User database.<br>
 * Verifies that entries are written and read correctly.
 */
public class UserTest extends AbstractTest {
  
  /**
   * Deserializes all Discord entities and initializes the User database.
   *
   * @throws SQLException If a database error occurred.
   */
  @BeforeEach
  public void setUp() throws SQLException {
    super.setUp();
    
    UserDatabase.create();
  }
  
  @Test
  public void testCreateOverExistingTable() throws SQLException {
    // Table has already been created in setUp()
    assertThat(UserDatabase.put(user)).isEqualTo(1);
    assertThat(UserDatabase.contains(user.getId())).isTrue();
    // Should not replace the existing DB
    UserDatabase.create();
    assertThat(UserDatabase.contains(user.getId())).isTrue();
  }
  
  @Test
  public void testContains() throws SQLException {
    assertThat(UserDatabase.contains(user.getId())).isFalse();
    assertThat(UserDatabase.put(user)).isEqualTo(1);
    assertThat(UserDatabase.contains(user.getId())).isTrue();
  }
  
  @Test
  public void testPut() throws SQLException {
    // One row has been modified
    assertThat(UserDatabase.put(user)).isEqualTo(1);
  }
  
  @Test
  public void testPutAlreadyExistingUser() throws SQLException {
    UserDatabase.put(user);
  
    UserValueObject response = UserDatabase.get(user.getId());
    assertThat(user.getName()).isEqualTo(response.getName());
    
    user.setName("Updated");
  
    UserDatabase.put(user);
    response = UserDatabase.get(user.getId());
    // Old row has been updated
    assertThat(user.getName()).isEqualTo(response.getName());
  }
  
  @Test
  public void testDelete() throws SQLException {
    assertThat(UserDatabase.contains(user.getId())).isFalse();
    assertThat(UserDatabase.put(user)).isEqualTo(1);
    assertThat(UserDatabase.contains(user.getId())).isTrue();
    assertThat(UserDatabase.delete(user.getId())).isEqualTo(1);
    assertThat(UserDatabase.contains(user.getId())).isFalse();
  }
  
  @Test
  public void testDeleteUnknownUser() throws SQLException {
    // User doesn't exist => Nothing to remove
    assertThat(UserDatabase.delete(user.getId())).isEqualTo(0);
  }
  
  @Test
  public void testGetUser() throws SQLException {
    UserDatabase.put(user);
  
    UserValueObject response = UserDatabase.get(user.getId());
    
    Assertions.assertThat(response.getId()).isEqualTo(user.getId());
    Assertions.assertThat(response.getName()).isEqualTo(user.getName());
    Assertions.assertThat(response.getDiscriminator()).isEqualTo(user.getDiscriminator());
    Assertions.assertThat(response.getRanks()).isEqualTo(user.getRanks());
  }
  
  @Test
  public void testGetUnknownUser() {
    assertThrows(NoSuchElementException.class, () -> UserDatabase.get(user.getId()));
  }
}
