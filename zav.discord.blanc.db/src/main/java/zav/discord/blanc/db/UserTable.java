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
import zav.discord.blanc.databind.UserEntity;
import zav.discord.blanc.db.sql.SqlObject;
import zav.discord.blanc.db.sql.SqlQuery;

/**
 * Utility class for communicating with the {@code User} database.
 */
@Singleton
public class UserTable extends AbstractTable<UserEntity> {
  
  /*package*/ UserTable() {
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
          .map(UserTable::transform)
          .map(entity -> SqlQuery.unmarshal(entity, UserEntity.class))
          .collect(Collectors.toUnmodifiableList());
  }
  
  private static SqlObject transform(SqlObject entity) {
    // Serialize String to List<String>
    entity.computeIfPresent("ranks", (k, v) -> SqlQuery.deserialize(v));
    return entity;
  }
}
