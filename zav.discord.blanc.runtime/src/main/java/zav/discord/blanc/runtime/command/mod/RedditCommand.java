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

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import javax.inject.Inject;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.Webhook;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import zav.discord.blanc.api.Client;
import zav.discord.blanc.command.AbstractGuildCommand;
import zav.discord.blanc.command.GuildCommandManager;
import zav.discord.blanc.databind.GuildEntity;
import zav.discord.blanc.databind.TextChannelEntity;
import zav.discord.blanc.databind.WebhookEntity;
import zav.discord.blanc.reddit.SubredditObservable;

/**
 * This command allows the user to register webhooks to Reddit feeds. New submissions are directly
 * posted to this webhook. The webhook is expected to have the name {@code Reddit}. A new webhook
 * is created if none with this name exists. If the feed is de-registered and no other feeds share
 * the same webhook, is deleted if and only if it was created by this program.
 */
public class RedditCommand extends AbstractGuildCommand {
  private static final String WEBHOOK = "Reddit";
  private final ResourceBundle i18n;
  private final Client client;
  private final EntityManagerFactory factory;
  private final SubredditObservable reddit;
  private final String subreddit;
  private final SlashCommandEvent event;
  private final Guild guild;
  private final TextChannel target;
  private final Webhook webhook;
  
  /**
   * Creates a new instance of this command.
   *
   * @param event The event triggering this command.
   * @param manager The manager instance for this command.
   */
  @Inject
  public RedditCommand(SlashCommandEvent event, GuildCommandManager manager) {
    super(manager, Permission.MANAGE_CHANNEL);
    this.event = event;
    this.guild = event.getGuild();
    this.client = manager.getClient();
    this.reddit = client.getSubredditObservable();
    this.factory = client.getEntityManagerFactory();
    this.i18n = manager.getResourceBundle();

    this.subreddit = Objects.requireNonNull(event.getOption("subreddit"))
        .getAsString()
        .toLowerCase(Locale.ENGLISH);

    this.target = (TextChannel) Optional.ofNullable(event.getOption("channel"))
        .map(OptionMapping::getAsGuildChannel)
        .orElse(event.getTextChannel());
    
    this.webhook = target.retrieveWebhooks()
        .complete()
        .stream()
        .filter(e -> WEBHOOK.equals(e.getName()))
        .findFirst()
        .orElseGet(() -> target.createWebhook(WEBHOOK).complete());
  }
  
  @Override
  @SuppressFBWarnings(value = "RCN_REDUNDANT_NULLCHECK_WOULD_HAVE_BEEN_A_NPE")
  public void run() {
    try (EntityManager entityManager = factory.createEntityManager()) {
      GuildEntity guildEntity = GuildEntity.getOrCreate(entityManager, guild);
      TextChannelEntity channelEntity = TextChannelEntity.getOrCreate(entityManager, target);
      WebhookEntity webhookEntity = WebhookEntity.getOrCreate(entityManager, webhook);
      String response;
      
      // Update entity
      if (webhookEntity.getSubreddits().contains(subreddit)) {
        // Remove subreddit from database
        webhookEntity.getSubreddits().remove(subreddit);
    
        // Remove subreddit from the Reddit job
        reddit.removeListener(subreddit, webhook);
        
        // Delete webhook if it's no longer needed
        if (webhookEntity.getSubreddits().isEmpty() && webhookEntity.isOwner()) {
          webhook.delete().complete();
        }
    
        response = i18n.getString("remove_subreddit");
      } else {
        // Add subreddit to the database
        webhookEntity.getSubreddits().add(subreddit);

        // Add subreddit to the Reddit job
        reddit.addListener(subreddit, webhook);

        // Webhook has already been created, so we don't need to do it here
        response = i18n.getString("add_subreddit");
      }
      
      // Update bi-directional associations
      guildEntity.add(channelEntity);
      guildEntity.add(webhookEntity);
      channelEntity.add(webhookEntity);
  
      //Update the persistence file
      entityManager.getTransaction().begin();
      entityManager.merge(guildEntity);
      entityManager.merge(channelEntity);
      entityManager.merge(webhookEntity);
      entityManager.getTransaction().commit();
      
      event.replyFormat(response, subreddit, target.getAsMention()).complete();
    }
  }
}
