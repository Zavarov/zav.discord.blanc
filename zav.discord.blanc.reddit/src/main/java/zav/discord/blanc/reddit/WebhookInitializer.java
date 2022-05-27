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

import java.sql.SQLException;
import javax.inject.Inject;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.Webhook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zav.discord.blanc.db.WebhookTable;

/**
 * Utility class for initializing all subreddit feeds that have been mapped to a
 * {@link Webhook}.
 */
public class WebhookInitializer {
  private static final Logger LOGGER = LoggerFactory.getLogger(WebhookInitializer.class);
  
  private final WebhookTable db;
  
  private final SubredditObservable observable;
  
  @Inject
  public WebhookInitializer(WebhookTable db, SubredditObservable observable) {
    this.db = db;
    this.observable = observable;
  }
  
  public void load(TextChannel textChannel) throws SQLException {
    for (Webhook webhook : textChannel.retrieveWebhooks().complete()) {
      load(webhook);
    }
  }
  
  private void load(Webhook webhook) throws SQLException {
    db.get(webhook).ifPresent(entity -> {
      for (String subreddit : entity.getSubreddits()) {
        LOGGER.info("Add subreddit '{}' to webhook '{}'.", subreddit, entity.getName());
        observable.addListener(subreddit, webhook);
      }
    });
  }
}
