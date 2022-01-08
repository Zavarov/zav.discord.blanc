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

package zav.discord.blanc.runtime.command.core;

import zav.discord.blanc.api.Argument;
import zav.discord.blanc.command.AbstractCommand;
import zav.discord.blanc.databind.UserDto;

import java.util.List;

/**
 * This command posts the invitation link for the bot.
 */
public class InviteCommand extends AbstractCommand {
  private UserDto mySelfUserData;
  
  @Override
  public void postConstruct(List<? extends Argument> args) {
    mySelfUserData = shard.getSelfUser().getAbout();
  }
  
  /**
   * Retrieves the link from the config file and sends it.
   */
  @Override
  public void run() {
    StringBuilder stringBuilder = new StringBuilder()
          .append("Use this link if you want to add this bot to your server:\n")
          .append(String.format("https://discordapp.com/oauth2/authorize?client_id=%s&scope=bot", mySelfUserData.getId()));

    channel.send(stringBuilder);
  }
}
