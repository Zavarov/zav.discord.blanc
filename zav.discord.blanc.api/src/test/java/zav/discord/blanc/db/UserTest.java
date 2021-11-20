package zav.discord.blanc.db;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.sql.SQLException;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import zav.discord.blanc.databind.User;

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
    
    UserTable.create();
  }
  
  @Test
  public void testCreateOverExistingTable() throws SQLException {
    // Table has already been created in setUp()
    assertThat(UserTable.put(user)).isEqualTo(1);
    assertThat(UserTable.contains(user.getId())).isTrue();
    // Should not replace the existing DB
    UserTable.create();
    assertThat(UserTable.contains(user.getId())).isTrue();
  }
  
  @Test
  public void testContains() throws SQLException {
    assertThat(UserTable.contains(user.getId())).isFalse();
    assertThat(UserTable.put(user)).isEqualTo(1);
    assertThat(UserTable.contains(user.getId())).isTrue();
  }
  
  @Test
  public void testPut() throws SQLException {
    // One row has been modified
    assertThat(UserTable.put(user)).isEqualTo(1);
  }
  
  @Test
  public void testPutAlreadyExistingUser() throws SQLException {
    UserTable.put(user);
    
    User response = UserTable.get(user.getId());
    assertThat(user.getName()).isEqualTo(response.getName());
    
    user.setName("Updated");
  
    UserTable.put(user);
    response = UserTable.get(user.getId());
    // Old row has been updated
    assertThat(user.getName()).isEqualTo(response.getName());
  }
  
  @Test
  public void testDelete() throws SQLException {
    assertThat(UserTable.contains(user.getId())).isFalse();
    assertThat(UserTable.put(user)).isEqualTo(1);
    assertThat(UserTable.contains(user.getId())).isTrue();
    assertThat(UserTable.delete(user.getId())).isEqualTo(1);
    assertThat(UserTable.contains(user.getId())).isFalse();
  }
  
  @Test
  public void testDeleteUnknownUser() throws SQLException {
    // User doesn't exist => Nothing to remove
    assertThat(UserTable.delete(user.getId())).isEqualTo(0);
  }
  
  @Test
  public void testGetUser() throws SQLException {
    UserTable.put(user);
    
    User response = UserTable.get(user.getId());
    
    assertThat(response.getId()).isEqualTo(user.getId());
    assertThat(response.getName()).isEqualTo(user.getName());
    assertThat(response.getDiscriminator()).isEqualTo(user.getDiscriminator());
    assertThat(response.getRanks()).isEqualTo(user.getRanks());
  }
  
  @Test
  public void testGetUnknownUser() {
    assertThrows(NoSuchElementException.class, () -> UserTable.get(user.getId()));
  }
}
