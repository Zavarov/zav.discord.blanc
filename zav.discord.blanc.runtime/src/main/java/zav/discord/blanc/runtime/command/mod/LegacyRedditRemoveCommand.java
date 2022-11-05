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
import java.util.EnumSet;
import java.util.Locale;
import java.util.Set;
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
public class LegacyRedditRemoveCommand extends AbstractGuildCommand {
  private final Client client;
  private final SubredditObservable reddit;
  private final TextChannel channel;
  private final SlashCommandEvent event;
  private final Guild guild;
  
  /**
   * Creates a new instance of this command.
   *
   * @param event The event triggering this command.
   * @param manager The manager instance for this command.
   */
  public LegacyRedditRemoveCommand(SlashCommandEvent event, GuildCommandManager manager) {
    super(manager);
    this.event = event;
    this.guild = event.getGuild();
    this.client = manager.getClient();
    this.reddit = client.get(SubredditObservable.class);
    this.channel = event.getTextChannel();
  }
  
  @Override
  @SuppressFBWarnings(value = "RCN_REDUNDANT_NULLCHECK_WOULD_HAVE_BEEN_A_NPE")
  public void run() {
    GuildEntity guildEntity = GuildEntity.find(guild);
    TextChannelEntity channelEntity = TextChannelEntity.find(channel);

    final String response = modify(channelEntity);

    if (channelEntity.isEmpty()) {
      guildEntity.remove(channelEntity);
    }

    guildEntity.merge();

    event.reply(response).complete();
  }

  private String modify(TextChannelEntity entity) {
    OptionMapping name = event.getOption("name");
    OptionMapping index = event.getOption("index");
    
    if (name != null) {
      return removeByName(entity, name.getAsString().toLowerCase(Locale.ENGLISH));
    } else if (index != null) {
      return removeByIndex(entity, (int) index.getAsLong());
    }
    
    return getMessage("subreddit_invalid_argument");
  }
  
  private String removeByName(TextChannelEntity entity, String name) {
    if (entity.getSubreddits().remove(name)) {
      // Remove subreddit from the Reddit job
      reddit.removeListener(name, channel);
      
      return getMessage("subreddit_remove", name, channel.getAsMention());
    }
    
    return getMessage("subreddit_name_not_found", name, channel.getAsMention());
  }
  
  private String removeByIndex(TextChannelEntity entity, int index) {
    if (index >= 0 && index < entity.getSubreddits().size()) {
      return removeByName(entity, entity.getSubreddits().get(index));
    }
    
    return getMessage("subreddit_index_not_found", index, channel.getAsMention());
  }
  
  @Override
  protected Set<Permission> getPermissions() {
    return EnumSet.of(Permission.MANAGE_CHANNEL);
  }
}
