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

package zav.discord.blanc.databind;

import zav.discord.blanc.databind.internal.PersistenceUtil;

/**
 * Base interface for all entities. It provides basic methods for interacting with the database.
 */
public interface PersistedEntity {

  /**
   * Returns the unique id of this entity.
   *
   * @return As described.
   */
  long getId();

  /**
   * Writes this entity to the database.
   */
  default void merge() {
    PersistenceUtil.merge(this);
  }

  /**
   * Checks whether the entity is persisted. 
   *
   * @return {@code true} if the entity is persisted, otherwise {@code false}.
   */
  default boolean isPersisted() {
    return PersistenceUtil.find(getClass(), getId()) != null;
  }
}
