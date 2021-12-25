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
import zav.discord.blanc.api.Argument;
import zav.discord.blanc.api.Permission;
import zav.discord.blanc.command.AbstractGuildCommand;
import zav.discord.blanc.databind.GuildValueObject;
import zav.discord.blanc.db.GuildTable;

import java.sql.SQLException;
import java.util.List;
import java.util.regex.Pattern;

/**
 * This command allows to blacklist certain words. Any message that contains the
 * word will be deleted by the bot.
 */
public class BlacklistCommand extends AbstractGuildCommand {
  private String myRegEx;
  private GuildValueObject myGuildData;
  
  public BlacklistCommand() {
    super(Permission.MANAGE_MESSAGES);
  }
  
  @Override
  public void postConstruct(List<? extends Argument> args) {
    Validate.validIndex(args, 0);
    myRegEx = args.get(0).asString().orElseThrow();
    Validate.notBlank(myRegEx);
    myGuildData = guild.getAbout();
  }
  
  @Override
  public void run() throws SQLException {
    if (myGuildData.getBlacklist().remove(myRegEx)) {
      channel.send("Removed '%s' from the blacklist.", myRegEx);
    } else {
      //Check if the regex is valid
      Pattern.compile(myRegEx);
  
      myGuildData.getBlacklist().add(myRegEx);
      channel.send("Added '%s' to the blacklist.", myRegEx);
    }
  
    // Update database
    GuildTable.put(myGuildData);
    
    // Update pattern
    String regEx = myGuildData.getBlacklist().stream().reduce((u, v) -> u + "|" + v).orElse("");

    Pattern result = Pattern.compile(regEx);
    
    guild.updateBlacklist(result);
  }
}
