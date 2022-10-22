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
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import zav.discord.blanc.command.GuildCommandManager;
import zav.discord.blanc.databind.WebhookEntity;

/**
 * This command allows the user to register webhooks to Reddit feeds. New submissions are directly
 * posted to this webhook. The webhook is expected to have the name {@code Reddit}. A new webhook
 * is created if none with this name exists. If the feed is de-registered and no other feeds share
 * the same webhook, is deleted if and only if it was created by this program.
 */
public class RedditRemoveCommand extends AbstractRedditCommand {

  
  /**
   * Creates a new instance of this command.
   *
   * @param event The event triggering this command.
   * @param manager The manager instance for this command.
   */
  public RedditRemoveCommand(SlashCommandEvent event, GuildCommandManager manager) {
    super(event, manager);
  }

  @Override
  protected String modify(WebhookEntity entity, SlashCommandEvent event) {
    OptionMapping name = event.getOption("name");
    OptionMapping index = event.getOption("index");
    
    if (name != null) {
      return removeByName(entity, name.getAsString().toLowerCase(Locale.ENGLISH));
    } else if (index != null) {
      return removeByIndex(entity, (int) index.getAsLong());
    }
    
    return getMessage("subreddit_invalid_argument");
  }
  
  private String removeByName(WebhookEntity entity, String name) {
    if (entity.getSubreddits().remove(name)) {
      // Remove subreddit from the Reddit job
      reddit.removeListener(name, webhook);
      
      // Delete webhook if it's no longer needed
      if (entity.getSubreddits().isEmpty() && entity.isOwner()) {
        webhook.delete().complete();
      }
      
      return getMessage("subreddit_remove", name, channel.getAsMention());
    }
    
    return getMessage("subreddit_name_not_found", name, channel.getAsMention());
  }
  
  private String removeByIndex(WebhookEntity entity, int index) {
    if (index >= 0 && index < entity.getSubreddits().size()) {
      return removeByName(entity, entity.getSubreddits().get(index));
    }
    
    return getMessage("subreddit_index_not_found", index, channel.getAsMention());
  }
}
