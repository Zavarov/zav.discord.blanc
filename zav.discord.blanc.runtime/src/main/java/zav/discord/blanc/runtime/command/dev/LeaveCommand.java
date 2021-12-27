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
import zav.discord.blanc.api.Argument;
import zav.discord.blanc.command.Rank;
import zav.discord.blanc.command.AbstractCommand;
import zav.discord.blanc.databind.GuildValueObject;
import zav.discord.blanc.db.GuildDatabase;
import zav.discord.blanc.db.RoleDatabase;
import zav.discord.blanc.db.TextChannelDatabase;
import zav.discord.blanc.db.WebHookDatabase;
import zav.discord.blanc.api.Guild;

import java.sql.SQLException;
import java.util.List;

/**
 * This command instructs the bot to leave the specified guild.
 */
public class LeaveCommand extends AbstractCommand {
  private Guild myGuild;
  private GuildValueObject myGuildData;
    
  public LeaveCommand() {
    super(Rank.DEVELOPER);
  }
  
  @Override
  public void postConstruct(List<? extends Argument> args) {
    Validate.validIndex(args, 0);
    myGuild = shard.getGuild(args.get(0));
    myGuildData = myGuild.getAbout();
  }
    
  @Override
  public void run() throws SQLException {
    myGuild.leave();
  
    GuildDatabase.delete(myGuildData.getId());
    TextChannelDatabase.deleteAll(myGuildData.getId());
    RoleDatabase.deleteAll(myGuildData.getId());
    WebHookDatabase.deleteAll(myGuildData.getId());
  }
}
