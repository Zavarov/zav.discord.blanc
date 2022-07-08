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
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import javax.inject.Inject;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import zav.discord.blanc.api.Client;
import zav.discord.blanc.command.AbstractGuildCommand;
import zav.discord.blanc.command.GuildCommandManager;
import zav.discord.blanc.databind.GuildEntity;
import zav.discord.blanc.databind.TextChannelEntity;
import zav.discord.blanc.reddit.SubredditObservable;

/**
 * This command allows the user to register text channels to Reddit feeds. New submissions are
 * directly posted to this channel. <br>
 * Note: This command only exists for legacy reason. It is no longer possible to registed new feeds
 * this way. Instead, it is only possible to remove already existing feeds. New feeds have to be
 * registered via webhooks.
 */
@Deprecated
public class RedditLegacyCommand extends AbstractGuildCommand {
  private final EntityManagerFactory factory;
  private final SubredditObservable reddit;
  private final SlashCommandEvent event;
  private final ResourceBundle i18n;
  private final TextChannel target;
  private final String subreddit;
  private final Client client;
  private final Guild guild;
  
  /**
   * Creates a new instance of this command.
   *
   * @param event The event triggering this command.
   * @param manager The manager instance for this command.
   */
  @Inject
  public RedditLegacyCommand(SlashCommandEvent event, GuildCommandManager manager) {
    super(manager, Permission.MANAGE_CHANNEL);
    this.i18n = manager.getResourceBundle();
    this.client = manager.getClient();
    this.factory = client.getEntityManagerFactory();
    this.reddit = client.getSubredditObservable();
    this.event = event;
    this.guild = event.getGuild();
    this.subreddit = Objects.requireNonNull(event.getOption("subreddit"))
        .getAsString()
        .toLowerCase(Locale.ENGLISH);
    this.target = (TextChannel) Optional.ofNullable(event.getOption("channel"))
        .map(OptionMapping::getAsGuildChannel)
        .orElse(event.getTextChannel());
  }
  
  @Override
  public void run() {
    try (EntityManager entityManager = factory.createEntityManager()) {
      GuildEntity guildEntity = GuildEntity.getOrCreate(entityManager, guild);
      TextChannelEntity channelEntity = TextChannelEntity.getOrCreate(entityManager, target);
      String response;
      
      // Remove subreddit from database
      if (channelEntity.getSubreddits().contains(subreddit)) {
        channelEntity.getSubreddits().remove(subreddit);
    
        // Update the Reddit job
        reddit.removeListener(subreddit, target);
        
        // Update bi-directional associations
        guildEntity.add(channelEntity);
    
        //Update the persistence file
        entityManager.getTransaction().begin();
        entityManager.merge(guildEntity);
        entityManager.merge(channelEntity);
        entityManager.getTransaction().commit();
    
        response = i18n.getString("remove_subreddit");
      } else {
        response = i18n.getString("add_subreddit_deprecated");
      }
      
      event.replyFormat(response, subreddit, target.getAsMention()).complete();
    }
  }
}
