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

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.sql.SQLException;
import javax.inject.Inject;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zav.discord.blanc.api.Client;

/**
 * Executable class for updating all registered Subreddit feeds.
 */
@SuppressFBWarnings(value = "EI_EXPOSE_REP2", justification = "That's the point...")
public class RedditJob implements Runnable {
  private static final Logger LOGGER = LoggerFactory.getLogger(RedditJob.class);
  private final SubredditObservable observable;
  
  @Inject
  public RedditJob(SubredditObservable observable) {
    this.observable = observable;
  }
  
  /**
   * Creates listener for all text channel that have been stored in the database.<br>
   * If a text channel/webhook no longer exists, they will be automatically removed from the
   * database.
   *
   * @param client The application client over all shards.
   * @param initializer The initializer function for all text channel listeners.
   * @throws SQLException If a database error occurred.
   */
  @Inject
  public void postConstruct(Client client, TextChannelInitializer initializer) throws SQLException {
    for (JDA shard : client.getShards()) {
      for (Guild guild : shard.getGuilds()) {
        initializer.load(guild);
      }
    }
  }
  
  /**
   * Creates listener for all webhooks that have been stored in the database.<br>
   * If a text channel/webhook no longer exists, they will be automatically removed from the
   * database.
   *
   * @param client The application client over all shards.
   * @param initializer The initializer function for all webhook listeners.
   * @throws SQLException If a database error occurred.
   */
  @Inject
  public void postConstruct(Client client, WebhookInitializer initializer) throws SQLException {
    for (JDA shard : client.getShards()) {
      for (Guild guild : shard.getGuilds()) {
        for (TextChannel textChannel : guild.getTextChannels()) {
          if (textChannel.canTalk()) {
            initializer.load(textChannel);
          }
        }
      }
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
