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

package zav.discord.blanc.runtime.command.mod;

import static zav.discord.blanc.runtime.internal.DatabaseUtils.getOrCreate;

import java.sql.SQLException;
import javax.inject.Inject;
import net.dv8tion.jda.api.Permission;
import org.eclipse.jdt.annotation.Nullable;
import zav.discord.blanc.api.Argument;
import zav.discord.blanc.command.AbstractGuildCommand;
import zav.discord.blanc.databind.GuildEntity;
import zav.discord.blanc.db.GuildTable;

/**
 * This command allows to set a custom prefix for a server.
 */
public class PrefixCommand extends AbstractGuildCommand {
  @Nullable
  @Argument(index = 0)
  @SuppressWarnings({"UnusedDeclaration"})
  private String myPrefix;
  
  @Inject
  private GuildTable db;
  
  private GuildEntity myGuildData;
    
  public PrefixCommand() {
    super(Permission.MESSAGE_MANAGE);
  }
  
  @Override
  public void postConstruct() {
    myGuildData = getOrCreate(db, guild);
  }
  
  @Override
  public void run() throws SQLException {
    
    if (myPrefix == null) {
      channel.sendMessage(i18n.getString("remove_prefix")).complete();
    } else {
      channel.sendMessageFormat(i18n.getString("add_prefix"), myPrefix).complete();
    }
  
    myGuildData.setPrefix(myPrefix);
    
    db.put(myGuildData);
  }
}
