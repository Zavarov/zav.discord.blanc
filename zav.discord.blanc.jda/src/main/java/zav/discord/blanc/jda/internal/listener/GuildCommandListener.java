/*
 * Copyright (c) 2021 Zavarov.
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

package zav.discord.blanc.jda.internal.listener;

import static zav.discord.blanc.jda.internal.GuiceUtils.injectGuildMessage;

import com.google.inject.Injector;
import javax.inject.Inject;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.eclipse.jdt.annotation.Nullable;
import zav.discord.blanc.api.GuildMessage;
import zav.discord.blanc.api.Shard;
import zav.discord.blanc.command.Command;
import zav.discord.blanc.command.Commands;
import zav.discord.blanc.command.guice.GuildCommandModule;
import zav.discord.blanc.command.parser.IntermediateCommand;
import zav.discord.blanc.command.parser.Parser;
import zav.discord.blanc.databind.MessageDto;
import zav.discord.blanc.jda.internal.guice.JdaGuildMessageModule;


/**
 * Listener for guild commands.<br>
 * Whenever a new message is received, it is checked whether it corresponds to a command. If so, the
 * corresponding command instance is created and submitted for execution.
 */
public class GuildCommandListener extends AbstractCommandListener {
  @Inject
  private Parser parser;
  
  @Inject
  private Injector injector;

  public GuildCommandListener(Shard shard) {
    super(shard);
  }

  @Override
  public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
    //Ignore bots
    if (event.getAuthor().isBot()) {
      return;
    }
    
    Message jdaMessage = event.getMessage();
    MessageDto message = new MessageDto()
          .withId(jdaMessage.getIdLong())
          .withAuthor(jdaMessage.getAuthor().getName())
          .withContent(jdaMessage.getContentRaw())
          .withAuthorId(jdaMessage.getAuthor().getIdLong());
 
    @Nullable
    IntermediateCommand command = parser.parse(message);
    
    // Message is not a command -> abort
    if (command == null) {
      return;
    }

    GuildMessage messageView = injectGuildMessage(jdaMessage);
    
    // Create a new injector for each command to avoid collisions between injected members
    Injector commandInjector = injector.createChildInjector(
          new JdaGuildMessageModule(jdaMessage),
          new GuildCommandModule(messageView)
    );
    
    @Nullable
    Class<? extends Command> commandClass = Commands.get(command.getName()).orElse(null);
    
    // Command doesn't exist -> abort
    if (commandClass == null) {
      return;
    }
    
    Command commandInstance = commandInjector.getInstance(commandClass);
    
    super.submit(event.getChannel(), commandInstance, command.getArguments());
  }
}
