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

package zav.discord.blanc.runtime.job;

import java.sql.SQLException;
import javax.inject.Inject;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zav.discord.blanc.api.Client;
import zav.discord.blanc.db.TextChannelTable;
import zav.discord.blanc.db.WebhookTable;

/**
 * This job is used to periodically remove all Reddit feeds from the database which can no longer be
 * handled. For example when the text channel no longer exists or can't be accessed by the bot
 * anymore.
 */
public class CleanupDatabaseJob implements Runnable {
  private static final Logger LOGGER = LoggerFactory.getLogger(CleanupDatabaseJob.class);
  
  @Inject
  private TextChannelTable textChannelTable;
  
  @Inject
  private WebhookTable webhookTable;
  
  @Inject
  private Client client;
  
  @Override
  public void run() {
    try {
      for (JDA shard : client.getShards()) {
        for (Guild guild : shard.getGuilds()) {
          int count = textChannelTable.retain(guild);
          LOGGER.info("{} text channel(s) have been deleted from the text channel database.", count);
    
          count = webhookTable.retain(guild);
          LOGGER.info("{} text channel(s) have been deleted from the webhook database.", count);
        }
      }
    } catch (SQLException e) {
      LOGGER.error(e.getMessage(), e);
    }
  }
}
