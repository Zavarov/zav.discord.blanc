package zav.discord.blanc.reddit;

import java.util.Objects;

import com.google.inject.Inject;
import org.eclipse.jdt.annotation.Nullable;
import zav.discord.blanc.api.TextChannel;
import zav.jrc.databind.LinkValueObject;
import zav.jrc.listener.SubredditListener;

/**
 * This listener notifies a text channel, whenever a new submission has been received from a
 * subreddit.
 *
 * @deprecated Deprecated in favor of the {@link WebhookSubredditListener}.
 */
@Deprecated
public final class TextChannelSubredditListener implements SubredditListener {
  private final TextChannel channel;
  
  public TextChannelSubredditListener(TextChannel channel) {
    this.channel = channel;
  }
  
  @Inject
  public void handle(LinkValueObject link) {
    channel.send(link);
  }
  
  @Override
  public int hashCode() {
    return Objects.hashCode(channel.getAbout().getId());
  }
  
  @Override
  public boolean equals(@Nullable Object obj) {
    if (obj == null) {
      return false;
    }
    
    if (!(obj instanceof TextChannelSubredditListener)) {
      return false;
    }
  
    TextChannelSubredditListener other = (TextChannelSubredditListener) obj;
    
    return Objects.equals(this.channel.getAbout().getId(), other.channel.getAbout().getId());
  }
}
