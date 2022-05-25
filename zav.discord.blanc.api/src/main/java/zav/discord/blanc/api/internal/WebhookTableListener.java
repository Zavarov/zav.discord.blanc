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

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.sql.SQLException;
import javax.inject.Inject;
import net.dv8tion.jda.api.events.channel.text.TextChannelDeleteEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zav.discord.blanc.db.WebhookTable;

/**
 * This listener removes the corresponding entries from the webhook table, whenever a text channel
 * is deleted or the bot is kicked from a guild.
 */
@SuppressFBWarnings(value = "EI_EXPOSE_REP2", justification = "That's the point...")
public class WebhookTableListener extends ListenerAdapter {
  private static final Logger LOGGER = LoggerFactory.getLogger(WebhookTableListener.class);
  private final WebhookTable db;
  
  @Inject
  public WebhookTableListener(WebhookTable db) {
    this.db = db;
  }
  
  @Override
  public void onTextChannelDelete(TextChannelDeleteEvent event) {
    try {
      db.delete(event.getChannel());
      LOGGER.info("Delete all database entries associated with {}.", event.getChannel());
    } catch (SQLException e) {
      LOGGER.error(e.getMessage(), e);
    }
  }
  
  @Override
  public void onGuildLeave(GuildLeaveEvent event) {
    try {
      db.delete(event.getGuild());
      LOGGER.info("Delete all database entries associated with {}.", event.getGuild());
    } catch (SQLException e) {
      LOGGER.error(e.getMessage(), e);
    }
  }
}
