package zav.discord.blanc.db;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;
import javax.inject.Singleton;
import org.apache.commons.lang3.Validate;
import zav.discord.blanc.databind.GuildEntity;
import zav.discord.blanc.db.sql.SqlObject;
import zav.discord.blanc.db.sql.SqlQuery;

/**
 * Utility class for communicating with the {@code Guild} database.
 */
@Singleton
public class GuildDatabaseTable extends AbstractDatabaseTable<GuildEntity> {
  
  /*package*/ GuildDatabaseTable() {
    // Created via Guice
  }
  
  @Override
  protected void create() throws SQLException {
    sql.update("guild/CreateGuildTable.sql");
  }
  
  @Override
  public int delete(Object... keys) throws SQLException {
    Validate.validState(keys.length == 1);

    return sql.update("guild/DeleteGuild.sql", keys);
  }
  
  @Override
  public int put(GuildEntity entity) throws SQLException {
    return sql.update("guild/InsertGuild.sql", (stmt) -> {
      stmt.setLong(1, entity.getId());
      stmt.setString(2, entity.getName());
      stmt.setString(3, entity.getPrefix().orElse(null));
      // Serialize List<String> to String
      stmt.setString(4, SqlQuery.marshal(entity.getBlacklist()));
    });
  }
  
  @Override
  public List<GuildEntity> get(Object... keys) throws SQLException {
    Validate.validState(keys.length == 1);

    List<SqlObject> result = sql.query("guild/SelectGuild.sql", keys);
  
    return result.stream()
          .map(GuildDatabaseTable::transform)
          .map(entity -> SqlQuery.unmarshal(entity, GuildEntity.class))
          .collect(Collectors.toUnmodifiableList());
  }
  
  private static SqlObject transform(SqlObject entity) {
    // Serialize String to List<String>
    entity.computeIfPresent("blacklist", (k, v) -> SqlQuery.deserialize(v));
    return entity;
  }
}
