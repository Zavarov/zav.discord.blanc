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

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.google.inject.name.Names;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.Webhook;
import org.eclipse.jdt.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zav.jrc.client.Client;
import zav.jrc.client.FailedRequestException;
import zav.jrc.listener.observer.SubredditObserver;

/**
 * Base class for all Reddit feeds.<br>
 * New listeners can be added and remove via the {@code addListener} and {@code removeListener}
 * methods. A new update can be triggered via {@code notifyAllObservers}, which will check all
 * registered subreddits for updates and notify their corresponding listeners.<br>
 * Each webhook and text channel can only have a single listener for a subreddit.
 */
@Singleton
public final class SubredditObservable {
  private static final Logger LOGGER = LoggerFactory.getLogger(SubredditObservable.class);
  private final Map<String, SubredditObserver> observers = new ConcurrentHashMap<>();
  @Inject
  private Injector injector;
  
  /*package*/ SubredditObservable() {
    // Instantiated with Guice
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
          .addListener(new WebhookSubredditListener(webhook));
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
          .removeListener(new WebhookSubredditListener(webhook));
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
  
  private SubredditObserver getObserver(String subredditName) {
    return injector.createChildInjector(new ObserverModule(subredditName)).getInstance(SubredditObserver.class);
  }
  
  private static class ObserverModule extends AbstractModule {
    private final String subredditName;
    
    public ObserverModule(String subredditName) {
      this.subredditName = subredditName;
    }
  
    @Override
    protected void configure() {
      bind(String.class).annotatedWith(Names.named("subreddit")).toInstance(subredditName);
    }
  }
}
