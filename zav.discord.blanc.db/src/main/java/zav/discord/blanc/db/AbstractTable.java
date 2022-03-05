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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import zav.discord.blanc.db.sql.SqlQuery;

/**
 * Abstract base class for all database tables.<br>
 * This class is responsible for initializing the database and the entity table.
 *
 * @param <T> The type of entity that is stored in this database table.
 */
public abstract class AbstractTable<T> implements DatabaseTable<T> {
  
  private static final Logger LOGGER = LogManager.getLogger();
  
  @Inject
  protected SqlQuery sql;
  
  @Inject
  /*package*/ void postConstruct() throws Exception {
    try {
      if (!Files.exists(ENTITY_DB_PATH)) {
        Files.createDirectories(ENTITY_DB_PATH.getParent());
        Files.createFile(ENTITY_DB_PATH);
      }
      
      create();
    } catch (Exception e) {
      LOGGER.error(e.getMessage(), e);
      throw e;
    }
  }
  
  @Override
  public boolean contains(Object... keys) throws SQLException {
    return !get(keys).isEmpty();
  }
  
  protected abstract void create() throws SQLException;
}
