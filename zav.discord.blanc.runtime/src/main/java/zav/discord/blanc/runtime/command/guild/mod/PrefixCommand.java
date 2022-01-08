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

import org.eclipse.jdt.annotation.Nullable;
import zav.discord.blanc.api.Permission;
import zav.discord.blanc.command.AbstractGuildCommand;
import zav.discord.blanc.api.Argument;
import zav.discord.blanc.databind.GuildDto;
import zav.discord.blanc.db.GuildDatabase;

import java.sql.SQLException;
import java.util.List;

/**
 * This command allows to set a custom prefix for a server.
 */
public class PrefixCommand extends AbstractGuildCommand {
  @Nullable
  private String myPrefix;
  
  private GuildDto myGuildData;
    
  public PrefixCommand() {
    super(Permission.MANAGE_MESSAGES);
  }
  
  @Override
  public void postConstruct(List<? extends Argument> args) {
    myPrefix = args.isEmpty() ? null : args.get(0).asString().orElseThrow();
    myGuildData = guild.getAbout();
  }
  
  @Override
  public void run() throws SQLException {
    
    if (myPrefix == null) {
      channel.send("Removed the custom prefix.");
    } else {
      channel.send("Set the custom prefix to '%s'.", myPrefix);
    }
  
    myGuildData.setPrefix(myPrefix);
    
    GuildDatabase.put(myGuildData);
  }
}
