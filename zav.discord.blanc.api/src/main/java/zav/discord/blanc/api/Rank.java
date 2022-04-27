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

package zav.discord.blanc.api;

import java.sql.SQLException;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import net.dv8tion.jda.api.entities.User;
import org.apache.commons.lang3.Validate;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.jetbrains.annotations.Contract;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zav.discord.blanc.databind.UserEntity;
import zav.discord.blanc.db.UserTable;

/**
 * An enumeration of all possible ranks a user may have.<br>
 * Ranks are used to limit, what types of commands a user can use. For example, a normal user
 * shouldn't be able to use developer commands.
 */
@NonNullByDefault
public enum Rank {
  REDDIT,
  USER,
  DEVELOPER,
  ROOT;
  
  private static final Map<Rank, EnumSet<Rank>> effectiveRanks = new HashMap<>();
  private static final Logger LOGGER = LoggerFactory.getLogger(Rank.class);
  
  static {
    effectiveRanks.put(Rank.REDDIT, EnumSet.of(Rank.REDDIT, Rank.USER));
    effectiveRanks.put(Rank.DEVELOPER, EnumSet.of(Rank.DEVELOPER, Rank.USER));
    effectiveRanks.put(Rank.ROOT, EnumSet.allOf(Rank.class));
  }
  
  /**
   * Calculates all ranks that are represented by the argument. In addition to ranks directly
   * matching provided names, all transitive ranks are included as well. For example,
   * {@link Rank#ROOT} will always include all other ranks.<br>
   * The mapping is {@code case-insensitive}.
   *
   * @param ranks A collection of rank names.
   * @return A list of effective ranks.
   */
  @Contract(pure = true)
  public static Set<Rank> getEffectiveRanks(Collection<String> ranks) {
    Set<Rank> source = ranks.stream()
          .map(String::toUpperCase)
          .map(Rank::valueOf)
          .collect(Collectors.toSet());

    Set<Rank> target = new HashSet<>();
    
    for (Rank userRank : source) {
      target.addAll(effectiveRanks.getOrDefault(userRank, EnumSet.of(Rank.USER)));
    }
    
    return Set.copyOf(target);
  }
  
  /**
   * Returns all ranks that are owned by the given user. If the user is not in the database or if
   * an SQL error occurred, {@link Rank#USER} is returned.
   *
   * @param userTable The user database.
   * @param user A Discord user.
   * @return A list of effective ranks.
   */
  @Contract(pure = true)
  public static Set<Rank> getEffectiveRanks(UserTable userTable, User user) {
    try {
      List<UserEntity> responses = userTable.get(user.getIdLong());
    
      Validate.validState(responses.size() <= 1);
    
      return responses.isEmpty() ? Set.of(USER) : getEffectiveRanks(responses.get(0).getRanks());
    } catch (SQLException e) {
      LOGGER.warn(e.getMessage(), e);
      return Set.of(Rank.USER);
    }
  }
}
