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

package zav.discord.blanc.api;

import java.math.BigDecimal;
import java.util.Optional;
import org.jetbrains.annotations.Contract;

/**
 * A generic argument of a command.<br>
 * An argument may either be a number (e.g. when providing a user id) or a plain string.
 */
public interface Argument {
  @Contract(pure = true)
  Optional<BigDecimal> asNumber();
  
  @Contract(pure = true)
  Optional<String> asString();
}
