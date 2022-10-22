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

import club.minnced.discord.webhook.WebhookClientBuilder;
import club.minnced.discord.webhook.external.JDAWebhookClient;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.Webhook;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zav.jrc.client.Client;
import zav.jrc.client.FailedRequestException;
import zav.jrc.endpoint.subreddit.Subreddit;
import zav.jrc.listener.observable.AbstractSubredditObservable;
import zav.jrc.listener.observer.SubredditObserver;

/**
 * Base class for all Reddit feeds.<br>
 * New listeners can be added and remove via the {@code addListener} and {@code removeListener}
 * methods. A new update can be triggered via {@code notifyAllObservers}, which will check all
 * registered subreddits for updates and notify their corresponding listeners.<br>
 * Each webhook and text channel can only have a single listener for a subreddit.
 */
@NonNullByDefault
public final class SubredditObservable extends AbstractSubredditObservable {
  private static final Logger LOGGER = LoggerFactory.getLogger(SubredditObservable.class);
  private final Map<String, SubredditObserver> observers = new ConcurrentHashMap<>();
  private final ScheduledExecutorService pool;
  private final Client client;
  
  /**
   * Creates a new observable instance.
   *
   * @param client The JRC client.
   * @param pool The executor service over which the webhooks are notified.
   */
  public SubredditObservable(Client client, ScheduledExecutorService pool) {
    super(client);
    this.client = client;
    this.pool = pool;
  }
  
  /**
   * Registers a new listener for the specified text channel.<br>
   * Returns {@code false} if a listener for the given subreddit has already been created for the
   * given textChannel.<br>
   * The subreddit name is {@code case-insensitive}.
   *
   * @param subreddit The subreddit name which is observed.
   * @param textChannel The textChannel which is notified upon new submissions.
   * @return {@code true}, if a new listener was created.
   */
  @Deprecated
  public boolean addListener(String subreddit, TextChannel textChannel) {
    subreddit = subreddit.toLowerCase(Locale.ENGLISH);
    
    return observers.computeIfAbsent(subreddit, this::getObserver)
          .addListener(new TextChannelSubredditListener(textChannel));
  }
  
  /**
   * Registers a new listener for the specified webhook.<br>
   * Returns {@code false} if a listener for the given subreddit has already been created for the
   * given webhook.<br>
   * The subreddit name is {@code case-insensitive}.
   *
   * @param subreddit The subreddit name which is observed.
   * @param webhook The webhook which is notified upon new submissions.
   * @return {@code true}, if a new listener was created.
   */
  public boolean addListener(String subreddit, Webhook webhook) {
    subreddit = subreddit.toLowerCase(Locale.ENGLISH);
    
    return observers.computeIfAbsent(subreddit, this::getObserver)
          .addListener(getListener(subreddit, webhook));
  }
  
  /**
   * Unregisters the listener for the specified webhook.<br>
   * Returns {@code false} no listener has been registered for the given subreddit and webhook.<br>
   * The subreddit name is {@code case-insensitive}.
   *
   * @param subreddit The subreddit name which is observed.
   * @param webhook The webhook which is notified upon new submissions.
   * @return {@code true}, if a listener has been removed.
   */
  public boolean removeListener(String subreddit, Webhook webhook) {
    subreddit = subreddit.toLowerCase(Locale.ENGLISH);
  
    return observers.computeIfAbsent(subreddit, this::getObserver)
          .removeListener(getListener(subreddit, webhook));
  }
  
  /**
   * Unregisters the listener for the specified text channel.<br>
   * Returns {@code false} no listener has been registered for the given subreddit and text
   * channel.<br>
   * The subreddit name is {@code case-insensitive}.
   *
   * @param subreddit The subreddit name which is observed.
   * @param textChannel The textChannel which is notified upon new submissions.
   * @return {@code true}, if a listener has been removed.
   */
  @Deprecated
  public boolean removeListener(String subreddit, TextChannel textChannel) {
    subreddit = subreddit.toLowerCase(Locale.ENGLISH);
    
    return observers.computeIfAbsent(subreddit, this::getObserver)
          .removeListener(new TextChannelSubredditListener(textChannel));
  }
  
  /**
   * Fetches the latest submissions from all registered subreddits and notifies their corresponding
   * listeners.
   */
  public void notifyAllObservers() {
    for (SubredditObserver observer : observers.values()) {
      try {
        observer.notifyAllListeners();
      } catch (FailedRequestException e) {
        LOGGER.error(e.getMessage(), e);
      }
    }
  }
  
  private WebhookSubredditListener getListener(String subredditName, Webhook webhook) {
    Subreddit subreddit = new Subreddit(client, subredditName);
    
    // client.close() should be ignored as the pool is shared across the entire application
    JDAWebhookClient client = WebhookClientBuilder.fromJDA(webhook)
          .setExecutorService(pool)
          .buildJDA();
    
    return new WebhookSubredditListener(subreddit, client);
  }
}
