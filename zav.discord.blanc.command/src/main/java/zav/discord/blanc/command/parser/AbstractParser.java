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

package zav.discord.blanc.command.parser;

import com.google.inject.Injector;
import com.google.inject.Module;
import java.util.Optional;
import javax.inject.Inject;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.jetbrains.annotations.Contract;
import zav.discord.blanc.api.Command;
import zav.discord.blanc.api.Commands;
import zav.discord.blanc.api.Help;
import zav.discord.blanc.api.Parser;
import zav.discord.blanc.command.IntermediateCommand;
import zav.discord.blanc.command.internal.GuildCommandModule;
import zav.discord.blanc.command.internal.IntermediateCommandModule;
import zav.discord.blanc.command.internal.ParameterModule;
import zav.discord.blanc.command.internal.PrivateCommandModule;

/**
 * Abstract base class for all command parser that implement the conversion from the intermediate
 * command representation to a Java object.
 */
@NonNullByDefault
public abstract class AbstractParser implements Parser {
  private static final String HELP = "h";
  
  @Inject
  private Injector injector;
  
  protected AbstractParser() {
    // Instantiated by Guice
  }
  
  @Contract(pure = true)
  protected abstract @Nullable IntermediateCommand parse(Message source);
  
  @Contract(pure = true)
  private Optional<Command> parse(Module module, Message message) {
    @Nullable
    IntermediateCommand cmd = parse(message);
    
    // Input is not a valid command
    if (cmd == null) {
      return Optional.empty();
    }
    
    Optional<Class<? extends Command>> cmdClazz = Commands.get(cmd.getName());
    
    // No command with the specified name exists -> abort
    if (cmdClazz.isEmpty()) {
      return Optional.empty();
    }
  
    // The help flag overrules the normal command
    if (cmd.getFlags().contains(HELP)) {
      message.getChannel().sendMessageEmbeds(Help.getHelp(cmdClazz.get())).complete();
      return Optional.empty();
    }
    
    Module cmdModule = new IntermediateCommandModule(cmd);
    Module paramModule = new ParameterModule(message, cmd.getParameters());
  
    // Injector w/ JDA & arguments
    Injector cmdInjector = injector.createChildInjector(module, cmdModule, paramModule);
  
    return cmdClazz.map(cmdInjector::getInstance);
  }
  
  @Override
  public Optional<Command> parse(GuildMessageReceivedEvent event) {
    return parse(new GuildCommandModule(event.getMessage()), event.getMessage());
  }
  
  @Override
  public Optional<Command> parse(PrivateMessageReceivedEvent event) {
    return parse(new PrivateCommandModule(event.getMessage()), event.getMessage());
  }
}
