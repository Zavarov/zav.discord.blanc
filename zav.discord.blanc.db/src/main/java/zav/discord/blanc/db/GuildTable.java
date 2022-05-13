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
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.dv8tion.jda.api.entities.Guild;
import zav.discord.blanc.databind.GuildEntity;
import zav.discord.blanc.db.sql.SqlObject;
import zav.discord.blanc.db.sql.SqlQuery;

/**
 * Utility class for communicating with the {@code Guild} database.
 */
@Singleton
public class GuildTable extends AbstractTable<GuildEntity, Guild> {
  
  @Inject
  public GuildTable(SqlQuery sql) {
    super(sql);
  }
  
  @Override
  protected void create() throws SQLException {
    sql.update("db/guild/Create.sql");
  }
  
  @Override
  public int put(GuildEntity entity) throws SQLException {
    return sql.update("db/guild/Insert.sql", (stmt) -> {
      stmt.setLong(1, entity.getId());
      stmt.setString(2, entity.getName());
      // Serialize List<String> to String
      stmt.setString(3, SqlQuery.marshal(entity.getBlacklist()));
    });
  }
  
  @Override
  public int delete(Guild guild) throws SQLException {
    return sql.update("db/guild/Delete.sql", guild.getId());
  }
  
  @Override
  public Optional<GuildEntity> get(Guild guild) throws SQLException {
    List<SqlObject> result = sql.query("db/guild/Select.sql", guild.getId());
  
    return result.stream()
          .map(GuildTable::transform)
          .map(entity -> SqlQuery.unmarshal(entity, GuildEntity.class))
          .findFirst();
  }
  
  private static SqlObject transform(SqlObject entity) {
    // Serialize String to List<String>
    entity.computeIfPresent("blacklist", (k, v) -> SqlQuery.deserialize(v));
    return entity;
  }
}
