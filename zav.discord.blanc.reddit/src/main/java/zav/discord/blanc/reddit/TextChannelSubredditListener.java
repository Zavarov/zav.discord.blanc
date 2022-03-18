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
