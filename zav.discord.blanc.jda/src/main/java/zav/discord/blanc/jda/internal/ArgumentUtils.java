/*
 * Copyright (c) 2021 Zavarov.
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

package zav.discord.blanc.jda.internal;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import zav.discord.blanc.api.Argument;

/**
 * Utility class for resolving entities via arguments.
 */
public final class ArgumentUtils {
  private ArgumentUtils() {}
  
  /**
   * Attempts to resolve the entity using the number representation of the argument.
   *
   * @param argument A generic argument.
   * @param byId Mapper function for resolving the entity.
   * @param <T> The target entity type.
   * @return An {@link Optional} containing the resolved entity. Empty if no match was found.
   */
  public static <T> Optional<T> resolveById(Argument argument, Function<Long, T> byId) {
    return argument.asNumber()
          .map(BigDecimal::longValue)
          .map(byId);
  }
  
  /**
   * Attempts to resolve the entity using the string representation of the argument.
   *
   * @param argument A generic argument.
   * @param byName Mapper function for resolving the entity.
   * @param <T> The target entity type.
   * @return An {@link Optional} containing the resolved entity. Empty if no unique match was found.
   */
  public static <T> Optional<T> resolveByName(Argument argument, Function<String, List<T>> byName) {
    List<T> results = argument.asString()
          .map(byName)
          .orElse(Collections.emptyList());
  
    // A unique guild matching this name has been found
    if (results.size() == 1) {
      return Optional.of(results.get(0));
    } else {
      return Optional.empty();
    }
  }
}
