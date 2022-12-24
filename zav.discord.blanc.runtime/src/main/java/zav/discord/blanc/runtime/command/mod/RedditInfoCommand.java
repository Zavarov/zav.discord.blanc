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

import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import zav.discord.blanc.command.AbstractGuildCommand;
import zav.discord.blanc.command.GuildCommandManager;
import zav.discord.blanc.databind.GuildEntity;
import zav.discord.blanc.databind.TextChannelEntity;
import zav.discord.blanc.databind.WebhookEntity;
import zav.discord.blanc.runtime.internal.SubredditUtils;

/**
 * This command displays all currently registered Reddit feeds.
 */
public class RedditInfoCommand extends AbstractGuildCommand {
  private final GuildCommandManager manager;
  private final TextChannel channel;
  private final Guild guild;
  
  /**
   * Creates a new instance of this command.
   *
   * @param event The event triggering this command.
   * @param manager The manager instance for this command.
   */
  public RedditInfoCommand(SlashCommandEvent event, GuildCommandManager manager) {
    super(manager);
    this.guild = event.getGuild();
    this.channel = event.getTextChannel();
    this.manager = manager;
  }
  
  private List<String> getSubreddits() {
    GuildEntity entity = GuildEntity.find(guild);
    
    // Check all persisted webhooks
    for (WebhookEntity webhookEntity : entity.getWebhooks()) {
      TextChannelEntity channelEntity = webhookEntity.getChannel();
      if (channel.getIdLong() == channelEntity.getId()) {
        return Collections.unmodifiableList(webhookEntity.getSubreddits());
      }
    }
    
    return Collections.emptyList();
  }

  @Override
  public void run() {
    manager.submit(SubredditUtils.getPages(getSubreddits()), "Subreddit Feeds");
  }
  
  @Override
  protected Set<Permission> getPermissions() {
    return EnumSet.of(Permission.MANAGE_CHANNEL);
  }
}
