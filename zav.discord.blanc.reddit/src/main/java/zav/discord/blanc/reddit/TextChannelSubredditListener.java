package zav.discord.blanc.reddit;

import static zav.discord.blanc.reddit.internal.MessageUtils.forLink;

import java.util.Objects;
import javax.inject.Inject;
import net.dv8tion.jda.api.entities.TextChannel;
import org.eclipse.jdt.annotation.Nullable;
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
    channel.sendMessage(forLink(link)).complete();
  }
  
  @Override
  public int hashCode() {
    return Objects.hashCode(channel.getIdLong());
  }
  
  @Override
  public boolean equals(@Nullable Object obj) {
    if (!(obj instanceof TextChannelSubredditListener)) {
      return false;
    }
  
    TextChannelSubredditListener other = (TextChannelSubredditListener) obj;
    
    return this.channel.getIdLong() == other.channel.getIdLong();
  }
}
