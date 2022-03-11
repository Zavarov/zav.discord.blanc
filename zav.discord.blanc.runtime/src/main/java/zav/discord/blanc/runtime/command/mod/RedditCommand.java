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

import static net.dv8tion.jda.api.Permission.MANAGE_CHANNEL;
import static zav.discord.blanc.runtime.internal.DatabaseUtils.getOrCreate;

import java.sql.SQLException;
import java.util.List;
import java.util.Locale;
import javax.inject.Inject;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.Webhook;
import zav.discord.blanc.api.Argument;
import zav.discord.blanc.command.AbstractGuildCommand;
import zav.discord.blanc.databind.WebHookEntity;
import zav.discord.blanc.db.WebHookTable;
import zav.discord.blanc.reddit.SubredditObservable;

/**
 * This command allows to link subreddits to channels.
 */
public class RedditCommand extends AbstractGuildCommand {
  private static final String WEBHOOK = "Reddit";

  @Argument(index = 0)
  @SuppressWarnings({"UnusedDeclaration"})
  private String subreddit;
  
  @Argument(index = 1, useDefault = true)
  @SuppressWarnings({"UnusedDeclaration"})
  private TextChannel target;
  
  @Inject
  private SubredditObservable observable;
  
  @Inject
  private WebHookTable webhookTable;
  
  private WebHookEntity webhookEntity;
  
  private Webhook webhook;
  
  protected RedditCommand() {
    super(MANAGE_CHANNEL);
  }
  
  @Override
  public void postConstruct() {
    List<Webhook> webhooks = target.retrieveWebhooks().complete();
    
    webhook = webhooks.stream()
          .filter(e -> e.getName().equals(WEBHOOK))
          .findFirst()
          .orElseGet(() -> target.createWebhook(WEBHOOK).complete());
    
    webhookEntity = getOrCreate(webhookTable, webhook);
    subreddit = subreddit.toLowerCase(Locale.ENGLISH);
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

      channel.sendMessageFormat(i18n.getString("remove_subreddit"), subreddit, target.getAsMention()).complete();
    } else {
      // Add subreddit to database
      webhookEntity.getSubreddits().add(subreddit);
      observable.addListener(subreddit, webhook);
  
      channel.sendMessageFormat(i18n.getString("add_subreddit"), subreddit, target.getAsMention()).complete();
    }
  
    //Update the persistence file
    webhookTable.put(webhookEntity);
  }
}
