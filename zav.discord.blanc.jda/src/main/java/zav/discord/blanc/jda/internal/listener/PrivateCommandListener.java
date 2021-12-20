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

import static zav.discord.blanc.jda.internal.GuiceUtils.injectPrivateMessage;

import com.google.inject.Injector;
import javax.inject.Inject;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import org.eclipse.jdt.annotation.Nullable;
import zav.discord.blanc.api.PrivateMessage;
import zav.discord.blanc.api.Shard;
import zav.discord.blanc.command.Command;
import zav.discord.blanc.command.Commands;
import zav.discord.blanc.command.guice.PrivateCommandModule;
import zav.discord.blanc.command.parser.IntermediateCommand;
import zav.discord.blanc.command.parser.Parser;
import zav.discord.blanc.databind.MessageValueObject;
import zav.discord.blanc.jda.internal.guice.JdaPrivateMessageModule;

/**
 * Listener for private commands.<br>
 * Whenever a new message is received, it is checked whether it corresponds to a command. If so, the
 * corresponding command instance is created and submitted for execution.
 */
public class PrivateCommandListener extends AbstractCommandListener {
  @Inject
  private Parser parser;
  
  @Inject
  private Injector injector;
  
  public PrivateCommandListener(Shard shard) {
    super(shard);
  }

  @Override
  public void onPrivateMessageReceived(PrivateMessageReceivedEvent event) {
    //Ignore bots
    if (event.getAuthor().isBot()) {
      return;
    }

    Message jdaMessage = event.getMessage();
    MessageValueObject message = new MessageValueObject()
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

    PrivateMessage messageView = injectPrivateMessage(jdaMessage);

    // Create a new injector for each command to avoid
    Injector commandInjector = injector.createChildInjector(
          new JdaPrivateMessageModule(jdaMessage),
          new PrivateCommandModule(messageView)
    );

    @Nullable
    Class<? extends Command> commandClass = Commands.get(command.getName()).orElse(null);

    // Command doesn't exist -> abort
    if (commandClass == null) {
      return;
    }

    Command commandInstance = commandInjector.getInstance(commandClass);

    super.submit(event.getChannel(), commandInstance);
  }
}
