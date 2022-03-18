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
import net.dv8tion.jda.api.entities.Webhook;
import org.eclipse.jdt.annotation.Nullable;
import zav.jrc.databind.LinkDto;
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
  public void handle(LinkDto link) {
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
