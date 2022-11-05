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

package zav.discord.blanc.api.listener;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import net.dv8tion.jda.api.events.channel.text.TextChannelDeleteEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zav.discord.blanc.databind.GuildEntity;
import zav.discord.blanc.databind.TextChannelEntity;

/**
 * This listener removes the corresponding entries from the text channel table, whenever a text
 * channel is deleted or the bot is kicked from a guild.
 */
@NonNullByDefault
public class TextChannelListener extends ListenerAdapter {
  private static final Logger LOGGER = LoggerFactory.getLogger(TextChannelListener.class);
  
  @Override
  @SuppressFBWarnings(value = "RCN_REDUNDANT_NULLCHECK_WOULD_HAVE_BEEN_A_NPE")
  public void onTextChannelDelete(TextChannelDeleteEvent event) {
    // Update text-channels, webhooks and guilds are updated via a cascade
    TextChannelEntity.remove(event.getChannel());

    LOGGER.info("Delete all database entries associated with {}.", event.getChannel());
  }
  
  @Override
  @SuppressFBWarnings(value = "RCN_REDUNDANT_NULLCHECK_WOULD_HAVE_BEEN_A_NPE")
  public void onGuildLeave(GuildLeaveEvent event) {
    // Update guilds, text-channels and webhooks are updated via the cascade
    GuildEntity.remove(event.getGuild());

    LOGGER.info("Delete all database entries associated with {}.", event.getGuild());
  }
}
