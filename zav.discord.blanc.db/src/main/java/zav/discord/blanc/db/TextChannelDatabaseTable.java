package zav.discord.blanc.db;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;
import javax.inject.Singleton;
import zav.discord.blanc.databind.TextChannelEntity;
import zav.discord.blanc.db.sql.SqlObject;
import zav.discord.blanc.db.sql.SqlQuery;

/**
 * Utility class for communicating with the {@code TextChannel} database.
 */
@Singleton
public class TextChannelDatabaseTable extends AbstractDatabaseTable<TextChannelEntity> {
  
  /*package*/ TextChannelDatabaseTable() {
    // Created via Guice
  }
  
  @Override
  protected void create() throws SQLException {
    sql.update("textchannel/CreateTextChannelTable.sql");
  }
  
  @Override
  public int delete(Object... keys) throws SQLException {
    switch (keys.length) {
      case 1:
        return sql.update("textchannel/DeleteAllGuildTextChannel.sql", keys);
      case 2:
        return sql.update("textchannel/DeleteTextChannel.sql", keys);
      default:
        throw new IllegalArgumentException();
    }
  }
  
  @Override
  public int put(TextChannelEntity entity) throws SQLException {
    return sql.update("textchannel/InsertTextChannel.sql", (stmt) -> {
      stmt.setLong(1, entity.getId());
      stmt.setLong(2, entity.getGuildId());
      stmt.setString(3, entity.getName());
      // Serialize List<String> to String
      stmt.setString(4, SqlQuery.marshal(entity.getSubreddits()));
    });
  }
  
  @Override
  public List<TextChannelEntity> get(Object... keys) throws SQLException {
    List<SqlObject> result;
  
    switch (keys.length) {
      case 1:
        result = sql.query("textchannel/SelectAllGuildTextChannel.sql", keys);
        break;
      case 2:
        result = sql.query("textchannel/SelectTextChannel.sql", keys);
        break;
      default:
        throw new IllegalArgumentException();
    }
  
    return result.stream()
          .map(TextChannelDatabaseTable::transform)
          .map(entity -> SqlQuery.unmarshal(entity, TextChannelEntity.class))
          .collect(Collectors.toUnmodifiableList());
  }
  
  private static SqlObject transform(SqlObject obj) {
    // Serialize String to List<String>
    obj.computeIfPresent("subreddits", (k, v) -> SqlQuery.deserialize(v));
    return obj;
  }
}
