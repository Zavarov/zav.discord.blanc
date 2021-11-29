package zav.discord.blanc.db;

import java.sql.SQLException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import zav.discord.blanc.databind.GuildValueObject;
import zav.discord.blanc.databind.RoleValueObject;
import zav.discord.blanc.db.internal.SqlObject;
import zav.discord.blanc.db.internal.SqlQuery;

/**
 * Utility class for communicating with the {@code Role} database.
 */
public abstract class RoleTable {
  private static final SqlQuery SQL = new SqlQuery(SqlQuery.ROLE_DB);
  
  private RoleTable() {}
  
  public static void create() throws SQLException {
    SQL.update("role/CreateRoleTable.sql");
  }
  
  public static boolean contains(long guildId, long roleId) throws SQLException {
    return !SQL.query("role/SelectRole.sql", guildId, roleId).isEmpty();
  }
  
  public static int delete(long guildId, long roleId) throws SQLException {
    return SQL.update("role/DeleteRole.sql", guildId, roleId);
  }
  
  public static int deleteAll(long guildId) throws SQLException {
    return SQL.update("role/DeleteAllRole.sql", guildId);
  }
  
  /**
   * Serializes the role and stores its value in the database.<br>
   * Roles are identified by their id and the id of their associated {@code guild}.<br>
   * If the database doesn't contain an entry for the role, a new one is created. Otherwise the
   * old entry is overwritten.
   *
   * @param guild The {@code guild} instance associated with the role.
   * @param role The {@code role} instance stored in the database.
   * @return The number of lines modified. Should be {@code 1}.
   * @throws SQLException If a database error occurred.
   */
  public static int put(GuildValueObject guild, RoleValueObject role) throws SQLException {
    return SQL.update("role/InsertRole.sql", (stmt) -> {
      stmt.setLong(1, guild.getId());
      stmt.setLong(2, role.getId());
      stmt.setString(3, role.getName());
      stmt.setString(4, role.getGroup());
    });
  }
  
  /**
   * Retrieves the database entry corresponding to the requested {@code role} and deserializes its
   * content.
   * Roles are identified by their id and the id of the guild they are associated with.<br>
   * If no such entry exists, an {@link NoSuchElementException} is thrown.
   *
   * @param guildId The id of the {@code guild} associated with the requested {@code role}.
   * @param roleId The id of the requested {@code role}.
   * @throws SQLException If a database error occurred.
   * @throws NoSuchElementException if the database doesn't contain an entry with the specified ids.
   */
  public static RoleValueObject get(long guildId, long roleId) throws SQLException {
    List<SqlObject> result = SQL.query("role/SelectRole.sql", guildId, roleId);
    
    if (result.isEmpty()) {
      throw new NoSuchElementException();
    }
    
    return SqlQuery.unmarshal(result.get(0), RoleValueObject.class);
  }
  
  /**
   * Retrieves the database entries corresponding to the requested {@code guild}.
   *
   * @param guildId The id of the {@code guild} associated with the requested {@code roles}.
   * @return An unmodifiable list of all roles associated with the provided guild id.
   * @throws SQLException If a database error occurred.
   */
  public static List<RoleValueObject> getAll(long guildId) throws SQLException {
    List<SqlObject> result = SQL.query("role/SelectAllRole.sql", guildId);
  
    return result.stream()
          .map(obj -> SqlQuery.unmarshal(obj, RoleValueObject.class))
          .collect(Collectors.toUnmodifiableList());
  }
}
