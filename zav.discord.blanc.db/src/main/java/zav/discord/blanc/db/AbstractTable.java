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

import static zav.discord.blanc.db.sql.SqlQuery.ENTITY_DB_PATH;

import java.nio.file.Files;
import java.sql.SQLException;
import javax.inject.Inject;
import zav.discord.blanc.db.sql.SqlQuery;

/**
 * Abstract base class for all database tables.<br>
 * This class is responsible for initializing the database and the entity table.
 *
 * @param <T> The type of entity that is stored in this database table.
 * @param <U> The Java object corresponding to the entity.
 */
public abstract class AbstractTable<T, U> implements Table<T, U> {
  protected final SqlQuery sql;
  
  @Inject
  public AbstractTable(SqlQuery sql) {
    this.sql = sql;
  }
  
  /**
   * Called after object construction.
   *
   * @throws Exception If the database file couldn't be created.
   */
  @Inject
  public void postConstruct() throws Exception {
    if (!Files.exists(ENTITY_DB_PATH)) {
      Files.createDirectories(ENTITY_DB_PATH.getParent());
      Files.createFile(ENTITY_DB_PATH);
    }
    
    create();
  }
  
  protected abstract void create() throws SQLException;
}
