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
import net.dv8tion.jda.api.entities.Webhook;
import org.eclipse.jdt.annotation.Nullable;
import zav.jrc.listener.SubredditListener;
import zav.jrc.listener.event.LinkEvent;

/**
 * This listener notifies a webhook, whenever a new submission has been received from a subreddit.
 */
@SuppressFBWarnings(value = "EI_EXPOSE_REP2", justification = "That's the point...")
public final class WebhookSubredditListener implements SubredditListener {
  private final Webhook webhook;

  public WebhookSubredditListener(Webhook webhook) {
    this.webhook = webhook;
  }
  
  @Override
  public void notify(LinkEvent linkEvent) {
    webhook.getChannel().sendMessage(forLink(linkEvent.getSource())).complete();
  }
  
  @Override
  public int hashCode() {
    return Objects.hashCode(webhook.getIdLong());
  }
  
  @Override
  public boolean equals(@Nullable Object obj) {
    if (!(obj instanceof WebhookSubredditListener)) {
      return false;
    }
  
    WebhookSubredditListener other = (WebhookSubredditListener) obj;
    
    return this.webhook.getIdLong() == other.webhook.getIdLong();
  }
}
