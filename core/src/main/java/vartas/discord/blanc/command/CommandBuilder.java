/*
 * Copyright (c) 2020 Zavarov
 *
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

package vartas.discord.blanc.command;

import vartas.discord.blanc.Guild;
import vartas.discord.blanc.Message;
import vartas.discord.blanc.MessageChannel;
import vartas.discord.blanc.TextChannel;
import vartas.discord.blanc.parser.IntermediateCommand;
import vartas.discord.blanc.parser.Parser;

import javax.annotation.Nonnull;
import java.util.Optional;

/**
 * This class is responsible for transforming received text {@link Message Messages}
 * and execute the {@link Command Commands} associated with it.<br>
 * This process is split into two phases. First, the {@link Message} will be parsed
 * and transformed into an {@link IntermediateCommand}.<br>
 * In this state, we can check for a matching prefix. This can either be the global prefix,
 * valid in all circumstances, or guild-specific prefixes,
 * which are only accepted in their respective {@link Guild Guilds}.
 * If the prefix is valid, an instance of the command, indicated by its name is created and executed,
 * provided with the arguments of the call.
 * @see IntermediateCommand
 */
@Nonnull
public abstract class CommandBuilder extends CommandBuilderTOP {
    /**
     * Responsible for creating the {@link IntermediateCommand}
     */
    @Nonnull
    private final Parser parser;
    /**
     * The command prefix accepted in all situations.
     */
    @Nonnull
    private final String globalPrefix;

    /**
     * Initializes the builder.
     * @param parser the {@link Parser} used for processing the messages.
     * @param globalPrefix the global command prefix.
     */
    public CommandBuilder(@Nonnull Parser parser, @Nonnull String globalPrefix){
        this.parser = parser;
        this.globalPrefix = globalPrefix;
    }

    /**
     * Processes the received {@link Message} from an arbitrary {@link MessageChannel}.<br>
     * If the {@link Message} doesn't start with the global prefix, doesn't describe a {@link Command}
     * or isn't associated with one, {@link Optional#empty()} is returned. Otherwise an {@link Optional} containing
     * the associated command is returned.
     * @param message the received {@link Message}.
     * @return the {@link Command} instance associated with the {@link Message}.
     */
    @Nonnull
    @Override
    public Optional<Command> build(@Nonnull Message message){
        Optional<? extends IntermediateCommand> commandOpt = parser.parse(message);

        //Message is not a command
        if(commandOpt.isEmpty())
            return Optional.empty();

        IntermediateCommand command = commandOpt.get();

        if(comparePrefix(command))
            return build(command.getName(), command.getArguments());
        else
            return Optional.empty();
    }


    /**
     * Processes the received {@link Message} from a {@link TextChannel}.<br>
     * If the {@link Message} doesn't start with either the global or the guild prefix,
     * doesn't describe a {@link Command} or isn't associated with one, {@link Optional#empty()} is returned.
     * Otherwise an {@link Optional} containing the associated command is returned.
     * @param message the received {@link Message}.
     * @param guild the {@link Guild} associated with the {@link TextChannel} from which the message was received.
     * @return the {@link Command} instance associated with the {@link Message}.
     */
    @Nonnull
    @Override
    public Optional<Command> build(@Nonnull Message message, @Nonnull Guild guild){
        Optional<? extends IntermediateCommand> commandOpt = parser.parse(message);

        //Message is not a command
        if(commandOpt.isEmpty())
            return Optional.empty();

        IntermediateCommand command = commandOpt.get();

        if(comparePrefix(command) || comparePrefix(command, guild))
            return build(command.getName(), command.getArguments());
        else
            return Optional.empty();
    }

    /**
     * Checks if the {@link Command} starts with either the guild or the global prefix.
     * @param command the {@link Command} associated with the received {@link Message}.
     * @param guild the {@link Guild} associated with the received {@link Message}.
     * @return true if the command prefix is valid.
     */
    private boolean comparePrefix(@Nonnull IntermediateCommand command, @Nonnull Guild guild){
        return guild.getPrefix().equals(command.getPrefix()) && command.getPrefix().isPresent();
    }

    /**
     * Checks if the {@link Command} starts with the global prefix.
     * @param command the {@link Command} associated with the received {@link Message}.
     * @return true if the command prefix is valid.
     */
    private boolean comparePrefix(@Nonnull IntermediateCommand command){
        return command.getPrefix().map(globalPrefix::equals).orElse(false);
    }
}
