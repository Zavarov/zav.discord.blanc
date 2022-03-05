package zav.discord.blanc.reddit;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.Webhook;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jdt.annotation.Nullable;
import zav.jrc.api.Reddit;
import zav.jrc.api.Subreddit;
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
  private static final Logger LOGGER = LogManager.getLogger(SubredditObservable.class);
  private final Map<String, SubredditObserver> observers = new ConcurrentHashMap<>();
  @Nullable
  private Injector injector;
  
  /*package*/ SubredditObservable() {
    // Instantiated with Guice
  }
  
  @Inject
  /*package*/ void setInjector(Injector injector) {
    this.injector = injector;
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
    assert injector != null;
    
    Subreddit subreddit = injector.getInstance(Reddit.class).getSubreddit(subredditName);
    SubredditObserver observer = new SubredditObserver(subreddit);
    injector.injectMembers(observer);
    return observer;
  }
}
