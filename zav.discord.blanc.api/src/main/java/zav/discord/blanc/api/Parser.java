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
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.jetbrains.annotations.Contract;

/**
 * Base interface for all command parser.<br>
 * The parsing process itself consists of two steps. First the raw string is transformed into
 * an intermediate representation, where name, arguments and all other relevant parameter are
 * extracted. Then this representation is used to instantiate the actual command.<br>
 * The classes corresponding to the commands are taken from {@link Command}.
 *
 * @see Commands
 */
@NonNullByDefault
public interface Parser {
  @Contract(pure = true)
  Optional<Command> parse(GuildMessageReceivedEvent event);
  
  @Contract(pure = true)
  Optional<Command> parse(PrivateMessageReceivedEvent event);
}
