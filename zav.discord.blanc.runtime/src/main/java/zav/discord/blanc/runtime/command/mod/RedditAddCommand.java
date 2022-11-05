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

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import java.util.Locale;
import net.dv8tion.jda.api.entities.Webhook;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import zav.discord.blanc.command.GuildCommandManager;
import zav.discord.blanc.databind.GuildEntity;
import zav.discord.blanc.databind.TextChannelEntity;
import zav.discord.blanc.databind.WebhookEntity;
import zav.discord.blanc.runtime.internal.PersistenceUtils;

/**
 * This command allows the user to register webhooks to Reddit feeds. New submissions are directly
 * posted to this webhook. The webhook is expected to have the name {@code Reddit}. A new webhook
 * is created if none with this name exists. If the feed is de-registered and no other feeds share
 * the same webhook, is deleted if and only if it was created by this program.
 */
public class RedditAddCommand extends AbstractRedditCommand {
  private final SlashCommandEvent event;
  private final EntityManagerFactory factory;
  private final Webhook webhook;
  
  /**
   * Creates a new instance of this command.
   *
   * @param event The event triggering this command.
   * @param manager The manager instance for this command.
   */
  public RedditAddCommand(SlashCommandEvent event, GuildCommandManager manager) {
    super(event, manager);
    this.event = event;
    this.factory = manager.getClient().getEntityManagerFactory();
    webhook = getWebhook().orElseGet(this::createWebhook);
  }

  @Override
  public void run() {
    PersistenceUtils.handle(factory, event, this::modify);
  }

  private String modify(EntityManager entityManager, GuildEntity entity) {
    String name = event.getOption("name").getAsString().toLowerCase(Locale.ENGLISH);
    
    WebhookEntity webhookEntity = WebhookEntity.getOrCreate(entityManager, webhook);
    TextChannelEntity channelEntity = TextChannelEntity.getOrCreate(entityManager, channel);
    
    if (!webhookEntity.getSubreddits().contains(name)) {
      // Add subreddit to the database
      webhookEntity.getSubreddits().add(name);

      // Add subreddit to the Reddit job
      reddit.addListener(name, webhook);
      
      // Add bi-directional dependencies
      channelEntity.add(webhookEntity);
      entity.add(webhookEntity);
      entity.add(channelEntity);

      // Webhook has already been created, so we don't need to do it here
      return getMessage("subreddit_add", name);
    } else {
      return getMessage("subreddit_already_added", name);
    }
  }
}
