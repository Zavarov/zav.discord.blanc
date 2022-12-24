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

package zav.discord.blanc.api.util;

import java.util.NoSuchElementException;

/**
 * Central interface for managing all application-wide objects. Each class can be mapped to a
 * singleton instance and be accessed from anywhere in the program.
 */
public interface ApplicationContext {
  /**
   * Creates the object bound to the given class.<br>
   * If no instance is found, a {@link NoSuchElementException} is thrown.
   *
   * @param <T> The type of the requested object.
   * @param clazz The class of the requested object.
   * @return A singleton instance of the requested class.
   */
  <T> T get(Class<T> clazz);
  
  /**
   * Maps the provided object to the given class. If the class is already bound to another object,
   * the old value is overwritten.
   *
   * @param <T> The type of the object to bound.
   * @param clazz The class of the object to bound.
   * @param object The object to bound.
   */
  <T> void bind(Class<T> clazz, T object);
}
