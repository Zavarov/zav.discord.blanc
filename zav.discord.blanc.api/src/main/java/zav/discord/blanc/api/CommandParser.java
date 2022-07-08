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

import java.util.Optional;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import org.jetbrains.annotations.Contract;

/**
 * Classes implementing this interface provide the ability to convert the received Discord command
 * into an executable task.
 */
public interface CommandParser {
  /**
   * Attempts to transform the event into an executable command. If a command can't be created, 
   * {@link Optional#empty()} is returned. Otherwise the optional contains a new instance of the
   * created command.
   *
   * @param event A command event.
   * @return As described.
   */
  @Contract(pure = true)
  Optional<Command> parse(SlashCommandEvent event);
}
