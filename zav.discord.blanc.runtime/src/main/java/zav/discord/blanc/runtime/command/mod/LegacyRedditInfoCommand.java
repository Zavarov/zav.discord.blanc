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
import java.util.List;
import javax.inject.Inject;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import zav.discord.blanc.command.GuildCommandManager;
import zav.discord.blanc.databind.GuildEntity;
import zav.discord.blanc.databind.TextChannelEntity;

/**
 * This command displays all currently registered Reddit feeds.
 */
public class LegacyRedditInfoCommand extends AbstractRedditInfoCommand {
  
  /**
   * Creates a new instance of this command.
   *
   * @param event The event triggering this command.
   * @param manager The manager instance for this command.
   */
  @Inject
  public LegacyRedditInfoCommand(SlashCommandEvent event, GuildCommandManager manager) {
    super(event, manager);
  }

  @Override
  public List<String> getSubreddits(GuildEntity entity) {
    for (TextChannelEntity channelEntity : entity.getTextChannels()) {
      if (channel.getIdLong() == channelEntity.getId()) {
        return Collections.unmodifiableList(channelEntity.getSubreddits());
      }
    }
    
    return Collections.emptyList();
  }
}
