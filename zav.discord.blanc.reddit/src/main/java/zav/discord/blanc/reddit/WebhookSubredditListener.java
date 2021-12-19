package zav.discord.blanc.reddit;

import java.util.Objects;
import org.eclipse.jdt.annotation.Nullable;
import zav.discord.blanc.api.WebHook;
import zav.jrc.databind.Link;
import zav.jrc.listener.SubredditListener;

/**
 * This listener notifies a webhook, whenever a new submission has been received from a subreddit.
 */
public final class WebhookSubredditListener implements SubredditListener {
  private final WebHook hook;

  public WebhookSubredditListener(WebHook hook) {
    this.hook = hook;
  }
  
  @Override
  public void handle(Link link) {
    hook.send(link);
  }
  
  @Override
  public int hashCode() {
    return Objects.hashCode(hook.getAbout().getId());
  }
  
  @Override
  public boolean equals(@Nullable Object obj) {
    if (obj == null) {
      return false;
    }
    
    if (!(obj instanceof WebhookSubredditListener)) {
      return false;
    }
  
    WebhookSubredditListener other = (WebhookSubredditListener) obj;
    
    return Objects.equals(this.hook.getAbout().getId(), other.hook.getAbout().getId());
  }
}
