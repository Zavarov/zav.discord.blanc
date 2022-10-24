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
import zav.discord.blanc.command.GuildCommandManager;
import zav.discord.blanc.databind.WebhookEntity;

/**
 * This command allows the user to register webhooks to Reddit feeds. New submissions are directly
 * posted to this webhook. The webhook is expected to have the name {@code Reddit}. A new webhook
 * is created if none with this name exists. If the feed is de-registered and no other feeds share
 * the same webhook, is deleted if and only if it was created by this program.
 */
public class RedditAddCommand extends AbstractRedditCommand {
  
  /**
   * Creates a new instance of this command.
   *
   * @param event The event triggering this command.
   * @param manager The manager instance for this command.
   */
  public RedditAddCommand(SlashCommandEvent event, GuildCommandManager manager) {
    super(event, manager);
  }

  @Override
  protected String modify(WebhookEntity entity, SlashCommandEvent event) {
    String name = event.getOption("name").getAsString().toLowerCase(Locale.ENGLISH);
    
    if (!entity.getSubreddits().contains(name)) {
      // Add subreddit to the database
      entity.getSubreddits().add(name);

      // Add subreddit to the Reddit job
      reddit.addListener(name, webhook);

      // Webhook has already been created, so we don't need to do it here
      return getMessage("subreddit_add", name);
    } else {
      return getMessage("subreddit_already_added", name);
    }
  }
}
