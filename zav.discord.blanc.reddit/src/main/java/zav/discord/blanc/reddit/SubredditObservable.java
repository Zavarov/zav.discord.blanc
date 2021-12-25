package zav.discord.blanc.reddit;

import com.google.inject.Injector;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jdt.annotation.Nullable;
import zav.discord.blanc.api.TextChannel;
import zav.discord.blanc.api.WebHook;
import zav.jrc.client.FailedRequestException;
import zav.jrc.listener.observer.SubredditObserver;
import zav.jrc.api.Subreddit;
import zav.jrc.api.guice.SubredditFactory;

/**
 * Base class for all Reddit feeds.<br>
 * New listeners can be added and remove via the {@code addListener} and {@code removeListener}
 * methods. A new update can be triggered via {@code notifyAllObservers}, which will check all
 * registered subreddits for updates and notify their corresponding listeners.<br>
 * Each webhook and text channel can only have a single listener for a subreddit.
 */
public final class SubredditObservable {
  private static final Logger LOGGER = LogManager.getLogger(SubredditObservable.class);
  private static final Map<String, SubredditObserver> observers = new ConcurrentHashMap<>();
  private static @Nullable Injector injector;
  
  private SubredditObservable() { }

  public static void init(@Nullable Injector injector) {
    SubredditObservable.injector = injector;
  }
  
  /**
   * Registers a new listener for the specified text channel view.<br>
   * Returns {@code false} if a listener for the given subreddit has already been created for the
   * given view.
   *
   * @param subreddit The subreddit name which is observed.
   * @param view The view which is notified upon new submissions.
   * @return {@code true}, if a new listener was created.
   */
  public static boolean addListener(String subreddit, TextChannel view) {
    return observers.computeIfAbsent(subreddit, SubredditObservable::getObserver)
          .addListener(new TextChannelSubredditListener(view));
  }
  
  /**
   * Registers a new listener for the specified webhook view.<br>
   * Returns {@code false} if a listener for the given subreddit has already been created for the
   * given view.
   *
   * @param subreddit The subreddit name which is observed.
   * @param view The view which is notified upon new submissions.
   * @return {@code true}, if a new listener was created.
   */
  public static boolean addListener(String subreddit, WebHook view) {
    return observers.computeIfAbsent(subreddit, SubredditObservable::getObserver)
          .addListener(new WebhookSubredditListener(view));
  }
  
  /**
   * Unregisters the listener for the specified webhook view.<br>
   * Returns {@code false} no listener has been registered for the given subreddit and view.
   *
   * @param subreddit The subreddit name which is observed.
   * @param view The view which is notified upon new submissions.
   * @return {@code true}, if a listener has been removed.
   */
  public static boolean removeListener(String subreddit, WebHook view) {
    return observers.computeIfAbsent(subreddit, SubredditObservable::getObserver)
          .removeListener(new WebhookSubredditListener(view));
  }
  
  /**
   * Unregisters the listener for the specified textchannel view.<br>
   * Returns {@code false} no listener has been registered for the given subreddit and view.
   *
   * @param subreddit The subreddit name which is observed.
   * @param view The view which is notified upon new submissions.
   * @return {@code true}, if a listener has been removed.
   */
  @Deprecated
  public static boolean removeListener(String subreddit, TextChannel view) {
    return observers.computeIfAbsent(subreddit, SubredditObservable::getObserver)
          .removeListener(new TextChannelSubredditListener(view));
  }
  
  /**
   * Fetches the latest submissions from all registered subreddits and notifies their corresponding
   * listeners.
   */
  public static void notifyAllObservers() {
    for (SubredditObserver observer : observers.values()) {
      try {
        observer.notifyAllListeners();
      } catch (FailedRequestException e) {
        LOGGER.error(e.getMessage(), e);
      }
    }
  }
  
  private static SubredditObserver getObserver(String subreddit) {
    assert injector != null;
  
    Subreddit view = injector.getInstance(SubredditFactory.class).create(subreddit);
    return new SubredditObserver(view);
  }
}
