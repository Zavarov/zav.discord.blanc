/*
 * Copyright (c) 2022 Zavarov.
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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
public class GuildTable extends AbstractTable<GuildEntity> {
  
  /*package*/ GuildTable() {
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
          .map(GuildTable::transform)
          .map(entity -> SqlQuery.unmarshal(entity, GuildEntity.class))
          .collect(Collectors.toUnmodifiableList());
  }
  
  private static SqlObject transform(SqlObject entity) {
    // Serialize String to List<String>
    entity.computeIfPresent("blacklist", (k, v) -> SqlQuery.deserialize(v));
    return entity;
  }
}
