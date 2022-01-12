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

public class GuildDto extends GuildTOPDto {
  public @Nullable Pattern getPattern() {
    @Nullable String regex = getBlacklist().stream()
          .reduce((u, v) -> u + "|" + v)
          .orElse(null);
    
    return regex == null ? null : Pattern.compile(regex);
  }
}
