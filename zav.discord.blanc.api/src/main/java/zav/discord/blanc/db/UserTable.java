package zav.discord.blanc.db;

import java.sql.SQLException;
import java.util.List;
import java.util.NoSuchElementException;
import zav.discord.blanc.databind.UserValueObject;
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
  
  /**
   * Serializes the user and stores its value in the database.<br>
   * Users are identified by their id and the id.<br>
   * If the database doesn't contain an entry for the user, a new one is created. Otherwise the
   * old entry is overwritten.
   *
   * @param user The {@code user} instance to be serialized.
   * @return The number of lines modified. Should be {@code 1}.
   * @throws SQLException If a database error occurred.
   */
  public static int put(UserValueObject user) throws SQLException {
    return SQL.update("user/InsertUser.sql", (stmt) -> {
      stmt.setLong(1, user.getId());
      stmt.setString(2, user.getName());
      stmt.setLong(3, user.getDiscriminator());
      // Serialize List<String> to String
      stmt.setString(4, SqlQuery.marshal(user.getRanks()));
    });
  }
  
  /**
   * Retrieves the database entry corresponding to the requested {@code user} and deserializes its
   * content.
   * User are identified by their id.<br>
   * If no such entry exists, an {@link NoSuchElementException} is thrown.
   *
   * @param userId The id of the requested {@code user}.
   * @return The deserialized {@code user} instance retrieved from the database.
   * @throws SQLException If a database error occurred.
   * @throws NoSuchElementException if the database doesn't contain an entry with the specified id.
   */
  public static UserValueObject get(long userId) throws SQLException {
    List<SqlObject> result = SQL.query("user/SelectUser.sql", userId);
    
    if (result.isEmpty()) {
      throw new NoSuchElementException();
    }
  
    SqlObject guild = result.get(0);
  
    // Serialize String to List<String>
    guild.computeIfPresent("ranks", (k, v) -> SqlQuery.deserialize(v));
    
    return SqlQuery.unmarshal(result.get(0), UserValueObject.class);
  }
}
