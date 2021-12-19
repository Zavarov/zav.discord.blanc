package zav.discord.blanc.reddit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import zav.discord.blanc.api.Job;

/**
 * Executable class for updating all registered Subreddit feeds.
 */
public class RedditJob implements Job {
  private static final Logger LOGGER = LogManager.getLogger(RedditJob.class);

  @Override
  public void run() {
    LOGGER.info("Update Reddit feed.");
    SubredditObservable.notifyAllObservers();
  }
}
