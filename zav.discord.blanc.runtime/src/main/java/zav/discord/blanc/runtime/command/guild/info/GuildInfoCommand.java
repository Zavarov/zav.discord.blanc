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

package zav.discord.blanc.runtime.command.guild.info;

import zav.discord.blanc.api.Argument;
import zav.discord.blanc.command.AbstractGuildCommand;
import zav.discord.blanc.databind.GuildDto;

import java.util.List;

/**
 * This command show the information about the guild the command was executed in.
 */
public class GuildInfoCommand extends AbstractGuildCommand {
  private GuildDto myGuildData;
  
  @Override
  public void postConstruct(List<? extends Argument> args) {
    myGuildData = guild.getAbout();
  }
  
  @Override
  public void run() {
    channel.send(myGuildData);
  }
}