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

package zav.discord.blanc.command;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.jetbrains.annotations.Contract;
import zav.discord.blanc.api.Command;

/**
 * This class contains all commands that are known during runtime.<br>
 * Each command is identified by a distinct key. New commands can be registers via
 * {@link Commands#bind(String, Class)} and retrieved via {@link Commands#get(String)}.
 */
@NonNullByDefault
public final class Commands {
  private static final Map<String, Class<? extends Command>> commands = new HashMap<>();
  
  private Commands() {}
  
  /**
   * Binds the given class to a name. Every slash command with that name creates a new instance of
   * the given class. Does nothing if the key is already assigned to another command.
   *
   * @param key The command name.
   * @param command The corresponding class.
   * @return {@code true}, if the name isn't bound to another command.
   */
  public static boolean bind(String key, Class<? extends Command> command) {
    return commands.putIfAbsent(key, command) == null;
  }
  
  /**
   * Returns the class bound to the given name. Returns {@link Optional#empty()}, if the name
   * doesn't match any command. Otherwise the optional contains the corresponding class.
   *
   * @param key The command name.
   * @return As described.
   */
  @Contract(pure = true)
  public static Optional<Class<? extends Command>> get(String key) {
    return Optional.ofNullable(commands.get(key));
  }

  /**
   * Unbinds all commands.
   */
  @Contract(mutates = "this")
  public static void clear() {
    commands.clear();
  }
}
