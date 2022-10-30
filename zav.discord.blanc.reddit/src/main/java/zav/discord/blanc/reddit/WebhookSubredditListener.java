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

import club.minnced.discord.webhook.external.JDAWebhookClient;
import club.minnced.discord.webhook.send.WebhookEmbed;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import club.minnced.discord.webhook.send.WebhookMessage;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.apache.commons.text.StringEscapeUtils;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zav.jrc.client.FailedRequestException;
import zav.jrc.databind.LinkEntity;
import zav.jrc.databind.SubredditEntity;
import zav.jrc.endpoint.subreddit.Subreddit;
import zav.jrc.listener.SubredditListener;
import zav.jrc.listener.event.LinkEvent;

/**
 * This listener notifies a webhook, whenever a new submission has been received from a subreddit.
 */
@NonNullByDefault
public final class WebhookSubredditListener implements SubredditListener {
  private static final Logger LOGGER = LoggerFactory.getLogger(WebhookSubredditListener.class);
  private final JDAWebhookClient client;
  private final Subreddit subreddit;

  /**
   * Creates a new instance of this class.
   *
   * @param subreddit The subreddit managed by this listener.
   * @param client The JDA client.
   */
  @SuppressFBWarnings(value = "EI_EXPOSE_REP2")
  public WebhookSubredditListener(Subreddit subreddit, JDAWebhookClient client) {
    this.subreddit = subreddit;
    this.client = client;
  }
  
  @Override
  public void notify(LinkEvent linkEvent) {
    LinkEntity link = linkEvent.getSource();
    Message source = forLink(link);
    
    String avatarUrl;
    
    try {
      SubredditEntity entity = subreddit.getAbout();
      avatarUrl = getAvatarUrl(entity);
    } catch (FailedRequestException e) {
      // Might be thrown when e.g. the subreddit is private or if Reddit is unavailable.
      LOGGER.error(e.getMessage(), e);
      avatarUrl = null;
    }
    
    WebhookMessage message = new WebhookMessageBuilder()
          .addEmbeds(transform(source.getEmbeds()))
          .setAvatarUrl(avatarUrl)
          .setUsername(null)
          .build();

    try {
      client.send(message);
    } catch (Exception e) {
      // Failing to notify this channel shouldn't prevent notifications in other channels.
      LOGGER.error(e.getMessage(), e);
    }
  }
  
  private @Nullable String getAvatarUrl(SubredditEntity entity) {
    String avatarUrl = StringEscapeUtils.unescapeHtml4(entity.getIconImage());
  
    // Check icon image first
    if (avatarUrl != null && EmbedBuilder.URL_PATTERN.matcher(avatarUrl).matches()) {
      return avatarUrl;
    }
    
    avatarUrl = StringEscapeUtils.unescapeHtml4(entity.getCommunityIcon());
  
    // Fall back to the community icon
    if (avatarUrl != null && EmbedBuilder.URL_PATTERN.matcher(avatarUrl).matches()) {
      return avatarUrl;
    }
    
    return null;
  }
  
  private List<WebhookEmbed> transform(List<MessageEmbed> source) {
    return source.stream()
          .map(WebhookEmbedBuilder::fromJDA)
          .map(WebhookEmbedBuilder::build)
          .collect(Collectors.toUnmodifiableList());
  }
  
  @Override
  public int hashCode() {
    return Objects.hashCode(client.getId());
  }
  
  @Override
  public boolean equals(@Nullable Object obj) {
    if (!(obj instanceof WebhookSubredditListener)) {
      return false;
    }
  
    WebhookSubredditListener other = (WebhookSubredditListener) obj;
    
    return this.client.getId() == other.client.getId();
  }
}
