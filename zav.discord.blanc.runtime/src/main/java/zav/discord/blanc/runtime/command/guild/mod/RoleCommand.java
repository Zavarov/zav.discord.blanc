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

package zav.discord.blanc.runtime.command.guild.mod;

import org.apache.commons.lang3.Validate;
import zav.discord.blanc.api.Permission;
import zav.discord.blanc.command.AbstractGuildCommand;
import zav.discord.blanc.api.Argument;
import zav.discord.blanc.databind.GuildValueObject;
import zav.discord.blanc.databind.RoleValueObject;
import zav.discord.blanc.db.RoleDatabase;

import java.sql.SQLException;
import java.util.List;

/**
 * This command groups roles together so that only one of them can be self-assigned at a time.
 */
public class RoleCommand extends AbstractGuildCommand {
  private GuildValueObject myGuildData;
  private RoleValueObject myRoleData;
  private String myGroup;
    
  public RoleCommand() {
    super(Permission.MANAGE_ROLES);
  }
  
  @Override
  public void postConstruct(List<? extends Argument> args) {
    Validate.validIndex(args, 0);
    myRoleData = guild.getRole(args.get(0)).getAbout();
    Validate.validIndex(args, 1);
    myGroup = args.get(1).asString().orElseThrow();
    myGuildData = guild.getAbout();
  }
  
  @Override
  public void run() throws SQLException {
    // The person executing this command can't interact with the role
    if (!guild.canInteract(author, myRoleData)) {
      channel.send("You need to be able to interact with the role \"%s\".", myRoleData.getName());
    // This bot can't interact with the role
    } else if (!guild.canInteract(guild.getSelfMember(), myRoleData)) {
      channel.send("I need to be able to interact with the role \"%s\".", myRoleData.getName());
    // The role is in this group -> Ungroup
    } else if (myGroup.equals(myRoleData.getGroup())) {
      myRoleData.setGroup(null);
      channel.send("Ungrouped role \"%s\".", myRoleData.getName());
      RoleDatabase.put(myGuildData, myRoleData);
    // The role is in a different group
    } else if (myRoleData.getGroup() != null) {
      channel.send("The role \"%s\" is already grouped under \"%s\".", myRoleData.getName(), myRoleData.getGroup());
    // Everything OK, make role self-assignable
    } else {
      myRoleData.setGroup(myGroup);
      channel.send("The role \"%s\" has been grouped under \"%s\".", myRoleData.getName(), myGroup);
      RoleDatabase.put(myGuildData, myRoleData);
    }
  }
}
