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

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zav.discord.blanc.api.Client;
import zav.discord.blanc.databind.GuildEntity;
import zav.discord.blanc.runtime.internal.validator.TextChannelValidator;
import zav.discord.blanc.runtime.internal.validator.WebhookValidator;

/**
 * This job is used to periodically remove all Reddit feeds from the database which can no longer be
 * handled. For example when the text channel no longer exists or can't be accessed by the bot
 * anymore.
 */
@SuppressWarnings("deprecation")
public class CleanupJob implements Runnable {
  private static final Logger LOGGER = LoggerFactory.getLogger(CleanupJob.class);
  private final EntityManagerFactory factory;
  private final Client client;
  
  /**
   * Creates a new instance of this class.
   *
   * @param client The global application instance.
   */
  public CleanupJob(Client client) {
    this.client = client;
    this.factory = client.getEntityManagerFactory();
  }
  
  @Override
  public void run() {
    try (EntityManager entityManager = factory.createEntityManager()) {
      for (JDA shard : client.getShards()) {
        for (Guild guild : shard.getGuilds()) {
          GuildEntity entity = GuildEntity.getOrCreate(entityManager, guild);

          LOGGER.info("Remove invalid text channels from the database.");
          entity.getTextChannels().removeIf(new TextChannelValidator(guild));
          LOGGER.info("Remove invalid webhooks from the database.");
          entity.getWebhooks().removeIf(new WebhookValidator(guild));
          
          entityManager.getTransaction().begin();
          entityManager.merge(entity);
          entityManager.getTransaction().commit();
        }
      }
    }
  }
}
