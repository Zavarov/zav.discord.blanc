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

import java.util.regex.Pattern;
import org.eclipse.jdt.annotation.Nullable;
import org.jetbrains.annotations.Contract;

/**
 * {@inheritDoc}
 */
public class GuildDto extends GuildTOPDto {
  /**
   * Each expression is concatenated using an {@code or}, meaning the pattern will match any String
   * that matches at least one banned expression.<br>
   * This method acts as a utility function to simplify the transformation of multiple Strings into
   * a single pattern.
   *
   * @return The pattern corresponding to all blacklisted expressions.
   */
  @Contract(pure = true)
  public @Nullable Pattern getPattern() {
    @Nullable String regex = getBlacklist().stream()
          .reduce((u, v) -> u + "|" + v)
          .orElse(null);
    
    return regex == null ? null : Pattern.compile(regex);
  }
}
