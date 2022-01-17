package zav.discord.blanc.reddit;

import static zav.discord.blanc.reddit.internal.MessageUtils.forLink;

import java.util.Objects;
import javax.inject.Inject;
import net.dv8tion.jda.api.entities.Webhook;
import org.eclipse.jdt.annotation.Nullable;
import zav.jrc.databind.LinkValueObject;
import zav.jrc.listener.SubredditListener;

/**
 * This listener notifies a webhook, whenever a new submission has been received from a subreddit.
 */
public final class WebhookSubredditListener implements SubredditListener {
  private final Webhook hook;

  public WebhookSubredditListener(Webhook hook) {
    this.hook = hook;
  }
  
  @Inject
  public void handle(LinkValueObject link) {
    hook.getChannel().sendMessage(forLink(link)).complete();
  }
  
  @Override
  public int hashCode() {
    return Objects.hashCode(hook.getIdLong());
  }
  
  @Override
  public boolean equals(@Nullable Object obj) {
    if (!(obj instanceof WebhookSubredditListener)) {
      return false;
    }
  
    WebhookSubredditListener other = (WebhookSubredditListener) obj;
    
    return this.hook.getIdLong() == other.hook.getIdLong();
  }
}
