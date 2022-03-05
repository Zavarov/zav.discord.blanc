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

/**
 * Base class for all SQL database tables.
 *
 * @param <T> The type of entity that is stored in this database table.
 */
public interface Table<T> {
  
  /**
   * Checks whether the database table contains at least one entity with the specified keys.
   *
   * @param keys The entity keys.
   * @return {@code true} when the table contains the specified entity.
   * @throws SQLException If a database error occurred.
   */
  boolean contains(Object... keys) throws SQLException;
  
  /**
   * Deletes all entities with matching keys from the database table.
   *
   * @param keys The entity keys.
   * @return The number of modified rows.
   * @throws SQLException If a database error occurred.
   */
  int delete(Object... keys) throws SQLException;
  
  /**
   * Stores the specified entity in the database table.
   *
   * @param entity The entity that stored.
   * @return The number of modified rows.
   * @throws SQLException If a database error occurred.
   */
  int put(T entity) throws SQLException;
  
  /**
   * Retrieves all entities with matching keys from the database table.
   *
   * @param keys The entity keys.
   * @return An unmodifiable list of retrieved entities.
   * @throws SQLException If a database error occurred.
   */
  List<T> get(Object... keys) throws SQLException;
}
