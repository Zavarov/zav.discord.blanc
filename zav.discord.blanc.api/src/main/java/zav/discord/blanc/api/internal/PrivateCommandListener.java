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

package zav.discord.blanc.api.internal;

import javax.inject.Inject;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import zav.discord.blanc.api.Command;
import zav.discord.blanc.api.Parser;

/**
 * Listener for private commands.<br>
 * Whenever a new message is received, it is checked whether it corresponds to a command. If so, the
 * corresponding command instance is created and submitted for execution.
 */
@NonNullByDefault
public class PrivateCommandListener extends AbstractCommandListener {
  @Inject
  private Parser parser;
  
  /*package*/ PrivateCommandListener() {
    // Create instance with Guice
  }

  @Override
  public void onPrivateMessageReceived(PrivateMessageReceivedEvent event) {
    //Ignore bots
    if (event.getAuthor().isBot()) {
      return;
    }
  
    @Nullable
    Command command = parser.parse(event).orElse(null);
  
    // Message doesn't correspond to a command -> abort
    if (command == null) {
      return;
    }
  
    super.submit(event.getChannel(), command);
  }
}