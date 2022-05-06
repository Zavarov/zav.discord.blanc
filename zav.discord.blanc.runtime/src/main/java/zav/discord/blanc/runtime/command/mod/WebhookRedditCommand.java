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

package zav.discord.blanc.runtime.command.mod;

import static zav.discord.blanc.runtime.internal.DatabaseUtils.getOrCreate;

import java.sql.SQLException;
import java.util.List;
import javax.inject.Inject;
import net.dv8tion.jda.api.entities.Webhook;
import zav.discord.blanc.databind.WebHookEntity;
import zav.discord.blanc.db.WebHookTable;

public class WebhookRedditCommand extends AbstractRedditCommand {
  private static final String WEBHOOK = "Reddit";
  
  @Inject
  private WebHookTable webhookTable;
  
  private WebHookEntity webhookEntity;
  private Webhook webhook;
  
  @Override
  public void postConstruct() {
    super.postConstruct();
    
    List<Webhook> webhooks = target.retrieveWebhooks().complete();
    
    webhook = webhooks.stream()
          .filter(e -> e.getName().equals(WEBHOOK))
          .findFirst()
          .orElseGet(() -> target.createWebhook(WEBHOOK).complete());
    
    webhookEntity = getOrCreate(webhookTable, webhook);
  }
  
  @Override
  public void run() throws SQLException {
    // Update view
    if (webhookEntity.getSubreddits().contains(subreddit)) {
      // Remove subreddit from database
      webhookEntity.getSubreddits().remove(subreddit);
      
      // Remove subreddit from the Reddit job
      observable.removeListener(subreddit, webhook);
      
      // Delete webhook if it's no longer needed
      if (webhookEntity.getSubreddits().isEmpty() && webhookEntity.isOwner()) {
        webhook.delete().complete();
      }
      
      event.replyFormat(i18n.getString("remove_subreddit"), subreddit, target.getAsMention()).complete();
    } else {
      // Add subreddit to database
      webhookEntity.getSubreddits().add(subreddit);
      observable.addListener(subreddit, webhook);
      
      event.replyFormat(i18n.getString("add_subreddit"), subreddit, target.getAsMention()).complete();
    }
    
    //Update the persistence file
    webhookTable.put(webhookEntity);
  }
}
