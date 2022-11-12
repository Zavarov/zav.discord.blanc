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

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.Webhook;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zav.discord.blanc.databind.WebhookEntity;

/**
 * Utility class for initializing all subreddit feeds that have been mapped to a
 * {@link Webhook}.
 */
@NonNullByDefault
public class WebhookInitializer {
  private static final Logger LOGGER = LoggerFactory.getLogger(WebhookInitializer.class);
  
  private final SubredditObservable observable;
  
  /**
   * Creates a new instance of this class.
   *
   * @param observable The global subreddit observable.
   */
  @SuppressFBWarnings(value = "EI_EXPOSE_REP2")
  public WebhookInitializer(SubredditObservable observable) {
    this.observable = observable;
  }
  
  /**
   * Initialize the listeners for all registered subreddits per webhook.
   *
   * @param textChannel One of the text channels visible to the bot.
   */
  public void load(TextChannel textChannel) {
    Member selfMember = textChannel.getGuild().getSelfMember();
    if (selfMember.hasPermission(textChannel, Permission.MANAGE_WEBHOOKS)) {
      LOGGER.info("Load subreddits for text channel {}", textChannel.getName());
      for (Webhook webhook : textChannel.retrieveWebhooks().complete()) {
        if (webhook.getToken() == null) {
          LOGGER.info("Foreign webhook {} found. Skip...", webhook.getName());
          continue;
        }
        
        load(webhook);
      }
    }
  }
  
  private void load(Webhook webhook) {
    WebhookEntity entity = WebhookEntity.find(webhook);

    for (String subreddit : entity.getSubreddits()) {
      LOGGER.info("Add subreddit '{}' to webhook '{}'.", subreddit, entity.getName());
      observable.addListener(subreddit, webhook);
    }
  }
}
