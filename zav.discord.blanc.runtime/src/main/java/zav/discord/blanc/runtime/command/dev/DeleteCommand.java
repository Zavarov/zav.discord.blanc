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

package zav.discord.blanc.runtime.command.dev;

import org.apache.commons.lang3.Validate;
import zav.discord.blanc.Argument;
import zav.discord.blanc.Rank;
import zav.discord.blanc.command.AbstractCommand;
import zav.discord.blanc.view.MessageView;

import java.util.List;

/**
 * This commands allows to delete messages made by the bot.
 */
public class DeleteCommand extends AbstractCommand {
  private MessageView myMessage;
  
  public DeleteCommand() {
    super(Rank.DEVELOPER);
  }
  
  @Override
  public void postConstruct(List<? extends Argument> args) {
    Validate.validIndex(args, 0);
    myMessage = channel.getMessage(args.get(0));
  }
  
  @Override
  public void run() {
    if (myMessage.getAbout().getAuthorId() == shard.getSelfUser().getAbout().getId()) {
      myMessage.delete();
    } else {
      channel.send("I can only delete my own messages.");
    }
  }
}
