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
import java.util.Objects;
import java.util.Optional;
import javax.inject.Singleton;
import net.dv8tion.jda.api.entities.Guild;
import zav.discord.blanc.databind.GuildEntity;
import zav.discord.blanc.db.sql.SqlObject;
import zav.discord.blanc.db.sql.SqlQuery;

/**
 * Utility class for communicating with the {@code Guild} database.
 */
@Singleton
public class GuildTable extends AbstractTable<GuildEntity> {
  
  @Override
  protected void create() throws SQLException {
    Objects.requireNonNull(sql);
    sql.update("guild/CreateGuildTable.sql");
  }
  
  @Override
  public int put(GuildEntity entity) throws SQLException {
    Objects.requireNonNull(sql);
    
    return sql.update("guild/InsertGuild.sql", (stmt) -> {
      stmt.setLong(1, entity.getId());
      stmt.setString(2, entity.getName());
      // Serialize List<String> to String
      stmt.setString(3, SqlQuery.marshal(entity.getBlacklist()));
    });
  }
  
  /**
   * Removes the {@link Guild} from the database.
   *
   * @param guild The {@link Guild} instance that is removed from the database.
   * @return The number of modified rows.
   * @throws SQLException If a database error occurred.
   */
  public int delete(Guild guild) throws SQLException {
    Objects.requireNonNull(sql);
    
    return sql.update("guild/DeleteGuild.sql", guild.getIdLong());
  }
  
  /**
   * Retrieves the entity associated with the provided {@link Guild}.
   *
   * @param guild The {@link Guild} instance that is retrieved from the database.
   * @return The entity associated with the {@link Guild} or {@link Optional#empty()} if no matching
   *     entity exists in the database.
   * @throws SQLException If a database error occurred.
   */
  public Optional<GuildEntity> get(Guild guild) throws SQLException {
    Objects.requireNonNull(sql);
    List<SqlObject> result = sql.query("guild/SelectGuild.sql", guild.getIdLong());
  
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
