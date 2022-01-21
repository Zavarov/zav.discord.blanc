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

package zav.discord.blanc.api.command.parser;

import com.google.inject.Module;
import java.util.Optional;
import net.dv8tion.jda.api.entities.Message;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.jetbrains.annotations.Contract;
import zav.discord.blanc.api.command.Command;
import zav.discord.blanc.api.command.Commands;
import zav.discord.blanc.api.command.IntermediateCommand;

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
  @Nullable IntermediateCommand parse(Message source);
  
  @Contract(pure = true)
  Optional<Command> parse(Module module, Message source);
}
