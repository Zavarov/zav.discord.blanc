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

import static net.dv8tion.jda.api.Permission.MESSAGE_MANAGE;
import static org.apache.commons.lang3.StringUtils.LF;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import zav.discord.blanc.api.Client;
import zav.discord.blanc.api.Site;
import zav.discord.blanc.command.AbstractGuildCommand;
import zav.discord.blanc.command.GuildCommandManager;
import zav.discord.blanc.databind.GuildEntity;
import zav.discord.blanc.databind.TextChannelEntity;
import zav.discord.blanc.databind.WebhookEntity;

/**
 * This command displays all currently registered Reddit feeds.
 */
public class RedditConfigurationCommand extends AbstractGuildCommand {  
  private final EntityManagerFactory factory;
  private final GuildCommandManager manager;
  private final Client client;
  private final Guild guild;
  
  /**
   * Creates a new instance of this command.
   *
   * @param event The event triggering this command.
   * @param manager The manager instance for this command.
   */
  @Inject
  public RedditConfigurationCommand(SlashCommandEvent event, GuildCommandManager manager) {
    super(manager, MESSAGE_MANAGE);
    this.guild = event.getGuild();
    this.manager = manager;
    this.client = manager.getClient();
    this.factory = client.getEntityManagerFactory();
  }

  @Override
  @SuppressFBWarnings(value = "RCN_REDUNDANT_NULLCHECK_OF_NONNULL_VALUE")
  public void run() {
    List<Site.Page> pages = new ArrayList<>();
    
    try (EntityManager entityManager = factory.createEntityManager()) {
      // Subreddit Name -> Text Channels
      Multimap<String, String> subreddits = ArrayListMultimap.create();
      GuildEntity guildEntity = GuildEntity.getOrCreate(entityManager, guild);
      
      // Check all persisted webhooks
      for (WebhookEntity webhookEntity : guildEntity.getWebhooks()) {
        TextChannelEntity channelEntity = webhookEntity.getChannel();
        TextChannel textChannel = guild.getTextChannelById(channelEntity.getId());
        // Channel may have been deleted
        if (textChannel != null) {
          for (String subredditName : webhookEntity.getSubreddits()) {
            subreddits.put(subredditName, textChannel.getAsMention());
          }
        }
      }
      
      // Check all persisted textchannels
      for (TextChannelEntity channelEntity : guildEntity.getTextChannels()) {
        TextChannel textChannel = guild.getTextChannelById(channelEntity.getId());
        // Channel may have been deleted
        if (textChannel != null) {
          for (String subredditName : channelEntity.getSubreddits()) {
            subreddits.put(subredditName, textChannel.getAsMention());
          }
        }
      }
      
      // Skip, if the guild doesn't contain any subreddit feeds
      if (!subreddits.isEmpty()) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("Subreddit Feeds");
        
        // Create a field for each subreddit
        subreddits.asMap().forEach((key, values) -> {
          String value = values.stream().reduce((u, v) -> u + LF + v).orElseThrow();
          builder.addField("r/" + key, value, false);
        });
        
        MessageEmbed content = builder.build();
        
        Site.Page mainPage = Site.Page.create("Subreddit Feeds", List.of(content));
        
        pages.add(mainPage);
      }
    }
    
    manager.submit(pages);
  }
}
