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

import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.Contract;

/**
 * An enumeration of all possible ranks a user may have.<br>
 * Ranks are used to limit, what types of commands a user can use. For example, a normal user
 * shouldn't be able to use developer commands.
 */
public enum Rank {
  /**
   * The default rank of every user.
   */
  USER,
  /**
   * Developers of this bot. Required for executed special, bot-specific commands.
   */
  DEVELOPER,
  /**
   * Superuser. Can execute all commands, regardless of required permissions and ranks.
   */
  ROOT;
  
  private static final Map<Rank, EnumSet<Rank>> effectiveRanks = new HashMap<>();
  
  static {
    effectiveRanks.put(Rank.USER, EnumSet.of(Rank.USER));
    effectiveRanks.put(Rank.DEVELOPER, EnumSet.of(Rank.DEVELOPER, Rank.USER));
    effectiveRanks.put(Rank.ROOT, EnumSet.allOf(Rank.class));
  }
  
  /**
   * Returns all ranks that are owned by the given user. If the user is not in the database or if
   * an SQL error occurred, {@link Rank#USER} is returned.
   *
   * @param user A Discord user.
   * @return A list of effective ranks.
   */
  @Contract(pure = true)
  public static Set<Rank> getEffectiveRanks(User user) {
    UserEntity entity = UserEntity.find(user);
    
    return Optional.ofNullable(entity)
          .map(UserEntity::getRanks)
          .orElse(List.of(Rank.USER))
          .stream()
          .map(effectiveRanks::get)
          .flatMap(Collection::stream)
          .collect(Collectors.toUnmodifiableSet());
  }
}
