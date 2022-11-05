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

import java.util.Locale;
import net.dv8tion.jda.api.entities.Webhook;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import zav.discord.blanc.command.GuildCommandManager;
import zav.discord.blanc.databind.GuildEntity;
import zav.discord.blanc.databind.TextChannelEntity;
import zav.discord.blanc.databind.WebhookEntity;

/**
 * This command allows the user to register webhooks to Reddit feeds. New submissions are directly
 * posted to this webhook. The webhook is expected to have the name {@code Reddit}. A new webhook
 * is created if none with this name exists. If the feed is de-registered and no other feeds share
 * the same webhook, is deleted if and only if it was created by this program.
 */
public class RedditRemoveCommand extends AbstractRedditCommand {
  private final Webhook webhook;
  
  /**
   * Creates a new instance of this command.
   *
   * @param event The event triggering this command.
   * @param manager The manager instance for this command.
   */
  public RedditRemoveCommand(SlashCommandEvent event, GuildCommandManager manager) {
    super(event, manager);
    this.webhook = getWebhook().orElse(null);
  }

  @Override
  public void run() {
    GuildEntity entity = GuildEntity.find(event.getGuild());
    event.reply(modify(entity)).complete();
    entity.merge();
  }

  private String modify(GuildEntity entity) {
    OptionMapping name = event.getOption("name");
    OptionMapping index = event.getOption("index");
    
    // Sanity check
    if (webhook == null) {
      return getMessage("subreddit_missing_webhook", WEBHOOK);
    }
    
    if (name == null && index == null) {
      return getMessage("subreddit_invalid_argument");
    }
    String response = null;
    WebhookEntity webhookEntity = WebhookEntity.find(webhook);
    TextChannelEntity channelEntity = TextChannelEntity.find(channel);
    
    if (name != null) {
      response = removeByName(webhookEntity, name.getAsString().toLowerCase(Locale.ENGLISH));
    } else {
      response = removeByIndex(webhookEntity, (int) index.getAsLong());
    }
    
    // Cleanup
    if (webhookEntity.isEmpty()) {
      channelEntity.remove(webhookEntity);
      entity.remove(webhookEntity);
    }
    
    if (channelEntity.isEmpty()) {
      entity.remove(channelEntity);
    }
    
    return response;
  }
  
  private String removeByName(WebhookEntity entity, String name) {
    if (entity.getSubreddits().remove(name)) {
      // Remove subreddit from the Reddit job
      reddit.removeListener(name, webhook);
      
      // Delete webhook if it's no longer needed
      if (entity.getSubreddits().isEmpty() && entity.isOwner()) {
        webhook.delete().complete();
      }
      
      return getMessage("subreddit_remove", name);
    }
    
    return getMessage("subreddit_name_not_found", name);
  }
  
  private String removeByIndex(WebhookEntity entity, int index) {
    if (index >= 0 && index < entity.getSubreddits().size()) {
      return removeByName(entity, entity.getSubreddits().get(index));
    }
    
    return getMessage("subreddit_index_not_found");
  }
}
