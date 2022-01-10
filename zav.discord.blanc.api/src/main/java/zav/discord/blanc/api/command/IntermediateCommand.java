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

import java.util.List;
import java.util.Optional;
import zav.discord.blanc.api.Argument;

/**
 * Base interface for the intermediate representation of a command.<br>
 * All commands consist of a (guild-specific) prefix, a distinct name and optionally, flags and
 * arguments. As an example:
 * <pre>
 *   b:foo -f bar
 *
 *   (prefix) b
 *   (name) foo
 *   (flags) [f]
 *   (arguments) [bar]
 * </pre>
 */
public interface IntermediateCommand {
  Optional<String> getPrefix();
  
  String getName();
  
  List<String> getFlags();
  
  List<? extends Argument> getArguments();
}
