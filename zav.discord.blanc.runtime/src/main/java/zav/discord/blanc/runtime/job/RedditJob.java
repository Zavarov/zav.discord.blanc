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

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zav.discord.blanc.api.Client;
import zav.discord.blanc.api.Shard;
import zav.discord.blanc.reddit.SubredditObservable;
import zav.discord.blanc.reddit.TextChannelInitializer;
import zav.discord.blanc.reddit.WebhookInitializer;

/**
 * Executable class for updating all registered Subreddit feeds.
 */
@NonNullByDefault
@SuppressWarnings("deprecation")
public class RedditJob implements Runnable {
  private static final Logger LOGGER = LoggerFactory.getLogger(RedditJob.class);
  private final SubredditObservable observable;
  
  /**
   * Creates a new instance of this class.
   *
   * @param client The application client over all shards.
   */
  @SuppressFBWarnings(value = "EI_EXPOSE_REP2")
  public RedditJob(Client client) {
    this.observable = client.get(SubredditObservable.class);
    this.postConstruct(client, new TextChannelInitializer(observable));
    this.postConstruct(client, new WebhookInitializer(observable));
  }
  
  /**
   * Creates listener for all text channel that have been stored in the database.<br>
   * If a text channel/webhook no longer exists, they will be automatically removed from the
   * database.
   *
   * @param client The application client over all shards.
   * @param initializer The initializer function for all text channel listeners.
   */
  public final void postConstruct(Client client, TextChannelInitializer initializer) {
    LOGGER.info("Initializing listeners for all registered text channels.");
    for (Shard shard : client.getShards()) {
      LOGGER.info("Initializing listeners for shard {}.", shard.getJda().getShardInfo());
      for (Guild guild : shard.getJda().getGuilds()) {
        LOGGER.info("Initializing listeners for guild {}.", guild.getName());
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
   */
  public final void postConstruct(Client client, WebhookInitializer initializer) {
    LOGGER.info("Initializing listeners for all registered webhooks.");
    for (Shard shard : client.getShards()) {
      LOGGER.info("Initializing listeners for shard {}.", shard.getJda().getShardInfo());
      for (Guild guild : shard.getJda().getGuilds()) {
        LOGGER.info("Initializing listeners for guild {}.", guild.getName());
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
