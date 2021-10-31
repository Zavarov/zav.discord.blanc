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

import org.apache.commons.lang3.Validate;
import zav.discord.blanc.command.AbstractGuildCommand;
import zav.discord.blanc.Argument;
import zav.discord.blanc.databind.Role;

import java.io.IOException;
import java.util.List;

/**
 * This command shows information about a specific role.
 */
public class RoleInfoCommand extends AbstractGuildCommand {
  private Role myRole;
  
  @Override
  public void postConstruct(List<? extends Argument> args) {
    Validate.validIndex(args, 0);
    myRole = guild.getRole(args.get(0)).getAbout();
  }
  
  @Override
  public void run() throws IOException {
    channel.send(myRole);
  }
}