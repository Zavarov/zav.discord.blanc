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

import static zav.discord.blanc.reddit.internal.MessageUtils.forLink;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Objects;
import net.dv8tion.jda.api.entities.TextChannel;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zav.jrc.listener.SubredditListener;
import zav.jrc.listener.event.LinkEvent;

/**
 * This listener notifies a text channel, whenever a new submission has been received from a
 * subreddit.
 *
 * @deprecated Deprecated in favor of the {@link WebhookSubredditListener}.
 */
@Deprecated
@NonNullByDefault
public final class TextChannelSubredditListener implements SubredditListener {
  private static final Logger LOGGER = LoggerFactory.getLogger(TextChannelSubredditListener.class);
  private final TextChannel channel;
  
  /**
   * Creates a new instance of this class.
   *
   * @param channel The text-channel managed by this listener.
   */
  @SuppressFBWarnings(value = "EI_EXPOSE_REP2")
  public TextChannelSubredditListener(TextChannel channel) {
    this.channel = channel;
  }
  
  @Override
  public void notify(LinkEvent linkEvent) {
    try {
      channel.sendMessage(forLink(linkEvent.getSource())).complete();
    } catch (Exception e) {
      // Failing to notify this channel shouldn't prevent notifications in other channels.
      LOGGER.error(e.getMessage(), e);
    }
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
