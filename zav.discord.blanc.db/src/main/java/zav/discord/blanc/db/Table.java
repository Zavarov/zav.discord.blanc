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
import java.util.Optional;

/**
 * Base class for all SQL database tables.
 *
 * @param <T> The type of entity that is stored in this database table.
 * @param <U> The Java object corresponding to the entity.
 */
public interface Table<T, U> {
  /**
   * Stores the specified entity in the database table.
   *
   * @param entity The entity that is stored.
   * @return The number of modified rows.
   * @throws SQLException If a database error occurred.
   */
  int put(T entity) throws SQLException;
  
  /**
   * Removes the entity associated with the provided object from the database.
   *
   * @param object The realization of the entity that is removed from the database.
   * @return The number of modified rows.
   * @throws SQLException If a database error occurred.
   */
  int delete(U object) throws SQLException;
  
  /**
   * Retrieves the entity associated with the provided object.
   *
   * @param object The realization of the entity that is retrieved from the database.
   * @return The entity associated with the object or {@link Optional#empty()} if no matching
   *     entity exists in the database.
   * @throws SQLException If a database error occurred.
   */
  Optional<T> get(U object) throws SQLException;
  
  boolean contains(U object) throws SQLException;
}
