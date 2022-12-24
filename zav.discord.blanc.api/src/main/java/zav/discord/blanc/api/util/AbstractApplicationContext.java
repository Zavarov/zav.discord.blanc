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

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import org.jetbrains.annotations.Contract;

/**
 * Abstract implementation of an application context. Classes can be bound to singleton instances.
 */
public abstract class AbstractApplicationContext implements ApplicationContext {
  private final Map<Class<?>, Object> context = new HashMap<>();

  @Override
  @Contract(pure = true)
  public <T> T get(Class<T> clazz) {
    Object result = context.get(clazz);

    if (result == null) {
      throw new NoSuchElementException("No element bound for " + clazz.toString());
    }

    return clazz.cast(result);
  }

  @Override
  @Contract(mutates = "this")
  public <T> void bind(Class<T> clazz, T object) {
    context.put(clazz, object);
  }
}
