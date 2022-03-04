package zav.discord.blanc.db;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;
import javax.inject.Singleton;
import org.apache.commons.lang3.Validate;
import zav.discord.blanc.databind.UserEntity;
import zav.discord.blanc.db.sql.SqlObject;
import zav.discord.blanc.db.sql.SqlQuery;

/**
 * Utility class for communicating with the {@code User} database.
 */
@Singleton
public class UserDatabaseTable extends AbstractDatabaseTable<UserEntity> {
  
  /*package*/ UserDatabaseTable() {
    // Created via Guice
  }

  @Override
  protected void create() throws SQLException {
    sql.update("user/CreateUserTable.sql");
  }
  
  @Override
  public int delete(Object... keys) throws SQLException {
    Validate.validState(keys.length == 1);
    
    return sql.update("user/DeleteUser.sql", keys);
  }
  
  @Override
  public int put(UserEntity user) throws SQLException {
    return sql.update("user/InsertUser.sql", (stmt) -> {
      stmt.setLong(1, user.getId());
      stmt.setString(2, user.getName());
      stmt.setString(3, user.getDiscriminator());
      // Serialize List<String> to String
      stmt.setString(4, SqlQuery.marshal(user.getRanks()));
    });
  }
  
  @Override
  public List<UserEntity> get(Object... keys) throws SQLException {
    Validate.validState(keys.length == 1);
    
    List<SqlObject> result = sql.query("user/SelectUser.sql", keys);
  
    return result.stream()
          .map(UserDatabaseTable::transform)
          .map(entity -> SqlQuery.unmarshal(entity, UserEntity.class))
          .collect(Collectors.toUnmodifiableList());
  }
  
  private static SqlObject transform(SqlObject entity) {
    // Serialize String to List<String>
    entity.computeIfPresent("ranks", (k, v) -> SqlQuery.deserialize(v));
    return entity;
  }
}
