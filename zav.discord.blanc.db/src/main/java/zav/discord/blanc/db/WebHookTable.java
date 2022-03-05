package zav.discord.blanc.db;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;
import javax.inject.Singleton;
import zav.discord.blanc.databind.WebHookEntity;
import zav.discord.blanc.db.sql.SqlObject;
import zav.discord.blanc.db.sql.SqlQuery;

/**
 * Utility class for communicating with the {@code WebHook} database.
 */
@Singleton
public class WebHookTable extends AbstractTable<WebHookEntity> {
  
  /*package*/ WebHookTable() {
    // Created via Guice
  }

  @Override
  protected void create() throws SQLException {
    sql.update("webhook/CreateWebHookTable.sql");
  }
  
  @Override
  public int delete(Object... keys) throws SQLException {
    switch (keys.length) {
      case 1:
        return sql.update("webhook/DeleteAllGuildWebHook.sql", keys);
      case 2:
        return sql.update("webhook/DeleteAllChannelWebHook.sql", keys);
      case 3:
        return sql.update("webhook/DeleteWebHook.sql", keys);
      default:
        throw new IllegalArgumentException();
    }
  }
  
  @Override
  public int put(WebHookEntity entity) throws SQLException {
    return sql.update("webhook/InsertWebHook.sql", (stmt) -> {
      stmt.setLong(1, entity.getId());
      stmt.setLong(2, entity.getGuildId());
      stmt.setLong(3, entity.getChannelId());
      stmt.setString(4, entity.getName());
      // Serialize List<String> to String
      stmt.setString(5, SqlQuery.marshal(entity.getSubreddits()));
      stmt.setBoolean(6, entity.isOwner());
    });
  }
  
  @Override
  public List<WebHookEntity> get(Object... keys) throws SQLException {
    List<SqlObject> result;
  
    switch (keys.length) {
      case 1:
        result = sql.query("webhook/SelectAllGuildWebHook.sql", keys);
        break;
      case 2:
        result = sql.query("webhook/SelectAllChannelWebHook.sql", keys);
        break;
      case 3:
        result = sql.query("webhook/SelectWebHook.sql", keys);
        break;
      default:
        throw new IllegalArgumentException();
    }
  
    return result.stream()
          .map(WebHookTable::transform)
          .map(entity -> SqlQuery.unmarshal(entity, WebHookEntity.class))
          .collect(Collectors.toUnmodifiableList());
  }
  
  private static SqlObject transform(SqlObject obj) {
    // Serialize String to List<String>
    obj.computeIfPresent("subreddits", (k, v) -> SqlQuery.deserialize(v));
    return obj;
  }
}
