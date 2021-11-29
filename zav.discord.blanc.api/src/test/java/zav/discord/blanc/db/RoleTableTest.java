package zav.discord.blanc.db;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.sql.SQLException;
import java.util.List;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import zav.discord.blanc.databind.RoleValueObject;

/**
 * Test case for the Role database.<br>
 * Verifies that entries are written and read correctly.
 */
public class RoleTableTest extends AbstractTest {
  
  /**
   * Deserializes all Discord entities and initializes the Role database.
   *
   * @throws SQLException If a database error occurred.
   */
  @BeforeEach
  public void setUp() throws SQLException {
    super.setUp();
  
    RoleTable.create();
  }
  
  @Test
  public void testCreateOverExistingTable() throws SQLException {
    // Table has already been created in setUp()
    assertThat(RoleTable.put(guild, role)).isEqualTo(1);
    assertThat(RoleTable.contains(guild.getId(), role.getId())).isTrue();
    // Should not replace the existing DB
    RoleTable.create();
    assertThat(RoleTable.contains(guild.getId(), role.getId())).isTrue();
  }
  
  @Test
  public void testContains() throws SQLException {
    assertThat(RoleTable.contains(guild.getId(), role.getId())).isFalse();
    assertThat(RoleTable.put(guild, role)).isEqualTo(1);
    assertThat(RoleTable.contains(guild.getId(), role.getId())).isTrue();
  }
  
  @Test
  public void testPut() throws SQLException {
    // One row has been modified
    assertThat(RoleTable.put(guild, role)).isEqualTo(1);
  }
  
  @Test
  public void testPutAlreadyExistingRole() throws SQLException {
    RoleTable.put(guild, role);
  
    RoleValueObject response = RoleTable.get(guild.getId(), role.getId());
    assertThat(role.getName()).isEqualTo(response.getName());
  
    role.setName("Updated");
    
    RoleTable.put(guild, role);
    response = RoleTable.get(guild.getId(), role.getId());
    // Old row has been updated
    assertThat(role.getName()).isEqualTo(response.getName());
  }
  
  @Test
  public void testDelete() throws SQLException {
    assertThat(RoleTable.contains(guild.getId(), role.getId())).isFalse();
    assertThat(RoleTable.put(guild, role)).isEqualTo(1);
    assertThat(RoleTable.contains(guild.getId(), role.getId())).isTrue();
    assertThat(RoleTable.delete(guild.getId(), role.getId())).isEqualTo(1);
    assertThat(RoleTable.contains(guild.getId(), role.getId())).isFalse();
  }
  
  @Test
  public void testDeleteAll() throws SQLException {
    assertThat(RoleTable.contains(guild.getId(), role.getId())).isFalse();
    assertThat(RoleTable.put(guild, role)).isEqualTo(1);
    assertThat(RoleTable.contains(guild.getId(), role.getId())).isTrue();
    assertThat(RoleTable.deleteAll(guild.getId())).isEqualTo(1);
    assertThat(RoleTable.contains(guild.getId(), role.getId())).isFalse();
  }
  
  @Test
  public void testDeleteUnknownRole() throws SQLException {
    // Role doesn't exist => Nothing to remove
    assertThat(RoleTable.delete(guild.getId(), role.getId())).isEqualTo(0);
  }
  
  @Test
  public void testGetRole() throws SQLException {
    RoleTable.put(guild, role);
  
    RoleValueObject response = RoleTable.get(guild.getId(), role.getId());
    
    assertThat(response.getId()).isEqualTo(role.getId());
    assertThat(response.getName()).isEqualTo(role.getName());
    assertThat(response.getGroup()).isEqualTo(role.getGroup());
  }
  
  @Test
  public void testGetAllRoles() throws SQLException {
    RoleTable.put(guild, role);
    
    List<RoleValueObject> responses = RoleTable.getAll(guild.getId());
    
    assertThat(responses).hasSize(1);
  
    RoleValueObject response = responses.get(0);
    
    assertThat(response.getId()).isEqualTo(role.getId());
    assertThat(response.getName()).isEqualTo(role.getName());
    assertThat(response.getGroup()).isEqualTo(role.getGroup());
  }
  
  @Test
  public void testGetUnknownRole() {
    assertThrows(NoSuchElementException.class, () -> RoleTable.get(guild.getId(), role.getId()));
  }
}
