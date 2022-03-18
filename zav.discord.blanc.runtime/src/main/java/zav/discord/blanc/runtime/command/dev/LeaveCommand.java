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

package zav.discord.blanc.runtime.command.dev;

import static zav.discord.blanc.runtime.internal.DatabaseUtils.getOrCreate;

import java.sql.SQLException;
import java.util.Objects;
import javax.inject.Inject;
import net.dv8tion.jda.api.entities.Guild;
import zav.discord.blanc.api.Argument;
import zav.discord.blanc.api.Rank;
import zav.discord.blanc.command.AbstractCommand;
import zav.discord.blanc.databind.GuildEntity;
import zav.discord.blanc.db.GuildTable;
import zav.discord.blanc.db.TextChannelTable;
import zav.discord.blanc.db.WebHookTable;

/**
 * This command instructs the bot to leave the specified guild.
 */
public class LeaveCommand extends AbstractCommand {
  @Argument(index = 0, useDefault = true)
  @SuppressWarnings({"UnusedDeclaration"})
  private Guild guild;
  
  @Inject
  private GuildTable guildTable;
  
  @Inject
  private TextChannelTable channelTable;
  
  @Inject
  private WebHookTable hookTable;
  
  private GuildEntity entity;
    
  public LeaveCommand() {
    super(Rank.DEVELOPER);
  }
  
  @Override
  public void postConstruct() {
    Objects.requireNonNull(guild, i18n.getString("invalid_guild"));
    entity = getOrCreate(guildTable, guild);
  }
    
  @Override
  public void run() throws SQLException {
    guild.leave().complete();
  
    guildTable.delete(entity.getId());
    channelTable.delete(entity.getId());
    hookTable.delete(entity.getId());
  }
}
