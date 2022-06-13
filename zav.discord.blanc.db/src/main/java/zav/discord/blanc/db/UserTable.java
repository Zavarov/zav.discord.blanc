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
import java.util.Optional;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.dv8tion.jda.api.entities.User;
import zav.discord.blanc.databind.UserEntity;
import zav.discord.blanc.db.sql.SqlObject;
import zav.discord.blanc.db.sql.SqlQuery;

/**
 * Utility class for communicating with the {@code User} database.
 */
@Singleton
public class UserTable extends AbstractTable<UserEntity, User> {
  
  @Inject
  public UserTable(SqlQuery sql) {
    super(sql);
  }
  
  @Override
  protected void create() throws SQLException {
    sql.update("db/user/Create.sql");
  }
  
  @Override
  public int put(UserEntity user) throws SQLException {
    return sql.update("db/user/Insert.sql", (stmt) -> {
      stmt.setLong(1, user.getId());
      stmt.setString(2, user.getName());
      stmt.setString(3, user.getDiscriminator());
      // Serialize List<String> to String
      stmt.setString(4, SqlQuery.marshal(user.getRanks()));
    });
  }
  
  @Override
  public int delete(User user) throws SQLException {
    return sql.update("db/user/Delete.sql", user.getId());
  }
  
  @Override
  public Optional<UserEntity> get(User user) throws SQLException {
    List<SqlObject> result = sql.query("db/user/Select.sql", user.getId());
  
    return result.stream()
          .map(UserTable::transform)
          .map(entity -> SqlQuery.unmarshal(entity, UserEntity.class))
          .findFirst();
  }
  
  private static SqlObject transform(SqlObject entity) {
    // Serialize String to List<String>
    entity.computeIfPresent("ranks", (k, v) -> SqlQuery.deserialize(v));
    return entity;
  }
}
