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

package zav.discord.blanc.api.command;

import org.jetbrains.annotations.Contract;
import zav.discord.blanc.api.Job;

/**
 * Base interface implemented by all commands.
 */
public interface Command extends Job {
  
  @Contract(mutates = "this")
  default void postConstruct() {}
  
  @Contract(pure = true)
  void validate() throws Exception;
}