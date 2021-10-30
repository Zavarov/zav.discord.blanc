package zav.discord.blanc.db;

import java.sql.SQLException;
import java.util.List;
import java.util.NoSuchElementException;
import zav.discord.blanc.databind.User;
import zav.discord.blanc.db.internal.SqlObject;
import zav.discord.blanc.db.internal.SqlQuery;

/**
 * Utility class for communicating with the {@code User} database.
 */
public abstract class UserTable {
  private static final SqlQuery SQL = new SqlQuery(SqlQuery.USER_DB);
  private UserTable() {}
  
  public static void create() throws SQLException {
    SQL.update("user/CreateUserTable.sql");
  }
  
  public static boolean contains(long userId) throws SQLException {
    return !SQL.query("user/SelectUser.sql", userId).isEmpty();
  }
  
  public static int delete(long userId) throws SQLException {
    return SQL.update("user/DeleteUser.sql", userId);
  }
  
  public static int put(User user) throws SQLException {
    return SQL.insert("user/InsertUser.sql", (stmt) -> {
      stmt.setLong(1, user.getId());
      stmt.setString(2, user.getName());
      stmt.setLong(3, user.getDiscriminator());
      // Serialize List<String> to String
      stmt.setString(4, SqlQuery.serialize(user.getRanks()));
    });
  }
  
  public static User get(long userId) throws SQLException {
    List<SqlObject> result = SQL.query("user/SelectUser.sql", userId);
    
    if (result.isEmpty()) {
      throw new NoSuchElementException();
    }
  
    SqlObject guild = result.get(0);
  
    // Serialize String to List<String>
    guild.computeIfPresent("ranks", (k, v) -> SqlQuery.deserialize(v));
    
    return SqlQuery.unmarshal(result.get(0), User.class);
  }
}
