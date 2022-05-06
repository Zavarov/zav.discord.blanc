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

package zav.discord.blanc.reddit;

import java.sql.SQLException;
import java.util.List;
import javax.inject.Inject;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.Webhook;
import org.eclipse.jdt.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zav.discord.blanc.api.Client;
import zav.discord.blanc.databind.TextChannelEntity;
import zav.discord.blanc.databind.WebHookEntity;
import zav.discord.blanc.db.TextChannelTable;
import zav.discord.blanc.db.WebHookTable;

/**
 * Executable class for updating all registered Subreddit feeds.
 */
public class RedditJob implements Runnable {
  private static final Logger LOGGER = LoggerFactory.getLogger(RedditJob.class);
  
  @Inject
  private SubredditObservable observable;
  
  @Inject
  private TextChannelTable textDb;
  
  @Inject
  private WebHookTable hookDb;
  
  /*package*/ RedditJob() {
    // Instantiated with Guice
  }
  
  /**
   * Creates listener for all text channel and webhooks that have been stored in the database.<br>
   * If a text channel/webhook no longer exists, they will be automatically removed from the
   * database.
   *
   * @param client The application client over all shards.
   * @throws SQLException If an SQL request to the database failed.
   */
  @Inject
  private void load(Client client) throws SQLException {
    for (JDA shard : client.getShards()) {
      for (Guild guild : shard.getGuilds()) {
        for (WebHookEntity webHook : hookDb.get(guild.getIdLong())) {
          loadWebHooks(guild, webHook);
        }
        for (TextChannelEntity textChannel : textDb.get(guild.getIdLong())) {
          loadTextChannels(guild, textChannel);
        }
      }
    }
  }
  
  private void loadWebHooks(Guild guild, WebHookEntity entity) throws SQLException {
    // Text channel may no longer exist...
    @Nullable TextChannel textChannel = guild.getTextChannelById(entity.getChannelId());
  
    if (textChannel == null) {
      hookDb.delete(entity.getGuildId(), entity.getChannelId(), entity.getId());
      LOGGER.error("TextChannel with id {} no longer exists -> delete...", entity.getChannelId());
      return;
    }
    
    List<Webhook> webhooks = textChannel.retrieveWebhooks().complete();
  
    // Web hook may no longer exist...
    @Nullable Webhook webhook = webhooks.stream()
          .filter(hook -> hook.getName().equals("Reddit"))
          .findFirst()
          .orElse(null);
    
    if (webhook == null) {
      hookDb.delete(entity.getGuildId(), entity.getChannelId(), entity.getId());
      LOGGER.error("Webhook with id {} no longer exists -> delete...", entity.getId());
      return;
    }
    
    for (String subreddit : entity.getSubreddits()) {
      LOGGER.info("Add subreddit '{}' to webhook '{}'.", subreddit, entity.getName());
      observable.addListener(subreddit, webhook);
    }
  }
  
  private void loadTextChannels(Guild guild, TextChannelEntity entity) throws SQLException {
    // Text channel may no longer exist...
    @Nullable TextChannel textChannel = guild.getTextChannelById(entity.getId());
  
    if (textChannel == null) {
      textDb.delete(entity.getGuildId(), entity.getId());
      LOGGER.error("TextChannel with id {} no longer exists -> delete...", entity.getId());
      return;
    }
    
    for (String subreddit : entity.getSubreddits()) {
      observable.addListener(subreddit, textChannel);
      LOGGER.info("Add subreddit '{}' to textChannel '{}'.", subreddit, textChannel.getName());
    }
  }

  @Override
  public void run() {
    try {
      LOGGER.info("Update Reddit feed.");
      observable.notifyAllObservers();
    } catch (Exception e) {
      LOGGER.error(e.getMessage(), e);
    }
  }
}
