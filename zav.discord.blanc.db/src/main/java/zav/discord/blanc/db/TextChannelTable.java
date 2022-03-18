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
import zav.discord.blanc.databind.TextChannelEntity;
import zav.discord.blanc.db.sql.SqlObject;
import zav.discord.blanc.db.sql.SqlQuery;

/**
 * Utility class for communicating with the {@code TextChannel} database.
 *
 * @deprecated Deprecated in favor of {@link WebHookTable}.
 */
@Singleton
@Deprecated
public class TextChannelTable extends AbstractTable<TextChannelEntity> {
  
  /*package*/ TextChannelTable() {
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
          .map(TextChannelTable::transform)
          .map(entity -> SqlQuery.unmarshal(entity, TextChannelEntity.class))
          .collect(Collectors.toUnmodifiableList());
  }
  
  private static SqlObject transform(SqlObject obj) {
    // Serialize String to List<String>
    obj.computeIfPresent("subreddits", (k, v) -> SqlQuery.deserialize(v));
    return obj;
  }
}
