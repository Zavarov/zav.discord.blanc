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

import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vartas.discord.blanc.*;
import vartas.discord.blanc.command.visitor.CommandVisitor;
import vartas.discord.blanc.parser.AbstractTypeResolver;
import vartas.discord.blanc.parser.IntermediateCommand;
import vartas.discord.blanc.parser.Parser;
import vartas.discord.blanc.prettyprint.ArgumentPrettyPrinter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

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
     * This class' logger.
     */
    @Nonnull
    private final Logger log = LoggerFactory.getLogger(this.getClass().getSimpleName());
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
     * Provides the type resolver for the command arguments. The type resolver is guild-sensitive, so for each
     * command, an individual resolver has to be created.
     */
    @Nonnull
    private final BiFunction<? super Guild, ? super TextChannel, ? extends AbstractTypeResolver> typeResolverFunction;

    /**
     * Initializes the builder.
     * @param parser the {@link Parser} used for processing the messages.
     * @param globalPrefix the global command prefix.
     */
    @Nonnull
    public CommandBuilder(
            @Nonnull BiFunction<? super Guild, ? super TextChannel, ? extends AbstractTypeResolver> typeResolverFunction,
            @Nonnull Shard shard,
            @Nonnull Parser parser,
            @Nonnull String globalPrefix
    )
    {
        super(typeResolverFunction, shard);
        this.parser = parser;
        this.globalPrefix = globalPrefix;
        this.typeResolverFunction = typeResolverFunction;
        super.shard = shard;
    }

    @Override
    public CommandBuilder getRealThis(){
        return this;
    }

    /**
     * Processes the received {@link Message} from an arbitrary {@link MessageChannel}.<br>
     * If the {@link Message} doesn't start with the global prefix, doesn't describe a {@link Command}
     * or isn't associated with one, {@link Optional#empty()} is returned. Otherwise an {@link Optional} containing
     * the associated command is returned.
     * @param message the received {@link Message}.
     * @param channel the {@link MessageChannel} from which the {@link Message} was received.
     * @return the {@link Command} instance associated with the {@link Message}.
     */
    @Nonnull
    @Override
    public Optional<Command> build(@Nonnull Message message, @Nonnull MessageChannel channel){
        Optional<? extends IntermediateCommand> commandOpt = parser.parse(message);

        //Message is not a command
        if(commandOpt.isEmpty())
            return Optional.empty();

        IntermediateCommand command = commandOpt.get();

        log.info("Received command {} : {} {}", command.getPrefix(), command.getName(), command.getArguments());

        typeResolver = typeResolverFunction.apply(null, null);

        if(comparePrefix(command))
            return build(command.getName(), command.getArguments(), command.getFlags())
                    .map(c -> provideContext(c, message, channel));
        else
            return Optional.empty();
    }


    /**
     * Processes the received {@link Message} from a {@link TextChannel}.<br>
     * If the {@link Message} doesn't start with either the global or the guild prefix,
     * doesn't describe a {@link Command} or isn't associated with one, {@link Optional#empty()} is returned.
     * Otherwise an {@link Optional} containing the associated command is returned.
     * @param message the received {@link Message}.
     * @param guild the {@link Guild} associated with the {@link TextChannel} from which the {@link Message} was received.
     * @param textChannel the {@link TextChannel} from which the {@link Message} was received.
     * @return the {@link Command} instance associated with the {@link Message}.
     */
    @Nonnull
    @Override
    public Optional<Command> build(@Nonnull Message message, @Nonnull Guild guild, @Nonnull TextChannel textChannel){
        Optional<? extends IntermediateCommand> commandOpt = parser.parse(message);

        //Message is not a command
        if(commandOpt.isEmpty())
            return Optional.empty();

        IntermediateCommand command = commandOpt.get();

        log.info("Received command {} : {} {}",
                command.getPrefix(),
                command.getName(),
                command.getArguments().stream().map(ArgumentPrettyPrinter::printPretty).collect(Collectors.toList())
        );

        typeResolver = typeResolverFunction.apply(guild, textChannel);

        if(comparePrefix(command) || comparePrefix(command, guild))
            return build(command.getName(), command.getArguments(), command.getFlags())
                    .map(c -> provideContext(c, message, guild, textChannel));
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

    /**
     * Binds the provided context to a guild command.
     * @param command the received command.
     * @param message the message instance from which the command originates.
     * @param guild the guild the message was recevied in.
     * @param messageChannel the text channel the message was received in.
     * @return the received command.
     */
    @Nonnull
    private Command provideContext
            (
                    @Nonnull Command command,
                    @Nonnull Message message,
                    @Nullable Guild guild,
                    @Nonnull MessageChannel messageChannel
            )
    {
        command.accept(new ContextProvider(message, guild, messageChannel));
        return command;
    }

    /**
     * Binds the provided context to a command.
     * @param command the received command.
     * @param message the message instance from which the command originates.
     * @param messageChannel the message channel the message was received in.
     * @return the received command.
     */
    @Nonnull
    private Command provideContext
            (
                    @Nonnull Command command,
                    @Nonnull Message message,
                    @Nonnull MessageChannel messageChannel
            )
    {
        return provideContext(command, message, null, messageChannel);
    }

    /**
     * This implements traverses the command and attach the context at the respective hook points.
     */
    @Nonnull
    private class ContextProvider implements CommandVisitor{
        /**
         * The guild the command was received in. Null if the message came from a private channel.
         */
        @Nullable
        private final Guild guild;
        /**
         * The message channel in which the command was received in.
         */
        @Nonnull
        private final MessageChannel messageChannel;
        /**
         * The user who issued the command.
         */
        @Nonnull
        private final User author;
        /**
         * The message that triggered the command.
         */
        @Nonnull
        private final Message message;

        /**
         * Creates a fresh visitor instance.
         * @param message the message instance from which the command originates.
         * @param guild the guild the message was recevied in.
         * @param messageChannel the text channel the message was received in.
         */
        @Nonnull
        public ContextProvider(@Nonnull Message message, @Nullable Guild guild, @Nonnull MessageChannel messageChannel){
            this.message = message;
            this.author = message.getAuthor();
            this.guild = guild;
            this.messageChannel = messageChannel;
        }

        /**
         * This method is called in case of a normal command.
         * Binds the context to the provided command.
         * @param command the command associated with the context.
         */
        @Override
        public void visit(MessageCommand command){
            command.set$Author(author);
            command.set$MessageChannel(messageChannel);
            command.set$Shard(shard);
            command.set$Message(message);
        }

        /**
         * This method is called in case of a guild command.
         * Binds the context to the provided command.
         * @param command the command associated with the context.
         */
        @Override
        public void visit(GuildCommand command){
            Preconditions.checkNotNull(guild);

            command.set$Author(guild.getUncheckedMembers(author.getId()));
            command.set$TextChannel(guild.getUncheckedChannels(messageChannel.getId()));
            command.set$Guild(guild);
            command.set$Shard(shard);
            command.set$Message(message);
        }
    }
}