package zav.discord.blanc.db;

import zav.discord.blanc.databind.Guild;
import zav.discord.blanc.databind.Role;
import zav.discord.blanc.db.internal.SqlObject;
import zav.discord.blanc.db.internal.SqlQuery;

import java.sql.SQLException;
import java.util.List;
import java.util.NoSuchElementException;

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
  
  public static int put(Guild guild, Role role) throws SQLException {
    return SQL.insert("role/InsertRole.sql", (stmt) -> {
      stmt.setLong(1, guild.getId());
      stmt.setLong(2, role.getId());
      stmt.setString(3, role.getName());
      stmt.setString(4, role.getGroup());
    });
  }
  
  public static Role get(long guildId, long roleId) throws SQLException {
    List<SqlObject> result = SQL.query("role/SelectRole.sql", guildId, roleId);
    
    if (result.isEmpty()) {
      throw new NoSuchElementException();
    }
    
    return SqlQuery.unmarshal(result.get(0), Role.class);
  }
}
