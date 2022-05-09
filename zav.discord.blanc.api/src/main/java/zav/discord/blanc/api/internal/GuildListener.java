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

package zav.discord.blanc.api.internal;

import java.sql.SQLException;
import java.util.Objects;
import javax.inject.Inject;
import net.dv8tion.jda.api.events.channel.text.TextChannelDeleteEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.eclipse.jdt.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zav.discord.blanc.db.GuildTable;
import zav.discord.blanc.db.TextChannelTable;
import zav.discord.blanc.db.WebhookTable;

/**
 * This class is responsible for automatically updating the database whenever the program leaves
 * a guild or a text channel is deleted.
 */
public class GuildListener extends ListenerAdapter {
  private static final Logger LOGGER = LoggerFactory.getLogger(GuildListener.class);

  private @Nullable TextChannelTable textChannelTable;
  private @Nullable WebhookTable webhookTable;
  private @Nullable GuildTable guildTable;
  
  @Inject
  public void setDatabase(TextChannelTable textChannelTable) {
    this.textChannelTable = textChannelTable;
  }
  
  @Inject
  public void setDatabase(WebhookTable webhookTable) {
    this.webhookTable = webhookTable;
  }
  
  @Inject
  public void setDatabase(GuildTable guildTable) {
    this.guildTable = guildTable;
  }
  
  @Override
  public void onTextChannelDelete(TextChannelDeleteEvent event) {
    try {
      Objects.requireNonNull(textChannelTable);
      Objects.requireNonNull(webhookTable);
  
      webhookTable.delete(event.getChannel());
      textChannelTable.delete(event.getChannel());
      
      LOGGER.info("Delete all database entries associated with {}.", event.getChannel().getName());
    } catch (SQLException e) {
      LOGGER.error(e.getMessage(), e);
    }
  }
  
  @Override
  public void onGuildLeave(GuildLeaveEvent event) {
    try {
      Objects.requireNonNull(textChannelTable);
      Objects.requireNonNull(webhookTable);
      Objects.requireNonNull(guildTable);
  
      guildTable.delete(event.getGuild());
      webhookTable.delete(event.getGuild());
      textChannelTable.delete(event.getGuild());
      
      LOGGER.info("Delete all database entries associated with {}.", event.getGuild().getName());
    } catch (SQLException e) {
      LOGGER.error(e.getMessage(), e);
    }
  }
}
