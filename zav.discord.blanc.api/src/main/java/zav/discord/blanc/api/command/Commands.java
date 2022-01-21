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

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.jetbrains.annotations.Contract;

/**
 * This class contains all commands that are known during runtime.<br>
 * Each command is identified by a distinct key. New commands can be registers via
 * {@link Commands#bind(String, Class)} and retrieved via {@link Commands#get(String)}.
 */
@NonNullByDefault
public final class Commands {
  private static final Map<String, Class<? extends Command>> commands = new HashMap<>();
  
  private Commands() {}
  
  public static boolean bind(String key, Class<? extends Command> command) {
    return commands.putIfAbsent(key, command) == null;
  }
  
  @Contract(pure = true)
  public static Optional<Class<? extends Command>> get(String key) {
    return Optional.ofNullable(commands.get(key));
  }
  
  public static void clear() {
    commands.clear();
  }
}
