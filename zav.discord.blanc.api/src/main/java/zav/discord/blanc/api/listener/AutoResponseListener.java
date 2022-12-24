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

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import zav.discord.blanc.api.cache.AutoResponseCache;

/**
 * The listener for automatically responding to specific messages. Per guild, an arbitrary number
 * of regular expressions can be mapped to pre-defined strings. Whenever a message matches at least
 * one of those messages, this string is returned.
 */
public class AutoResponseListener extends ListenerAdapter {
  private final AutoResponseCache responseCache;
  
  /**
   * Creates a new instance of this class.
   *
   * @param responseCache The global cache of all automatic responses.
   */
  public AutoResponseListener(AutoResponseCache responseCache) {
    this.responseCache = responseCache;
  }
  
  /**
   * Checks for every guild message whether it matches one of the registered auto-responses. Replies
   * with the first valid, pre-defined answer on success.
   *
   * @param event The event containing the received guild messages.
   */
  @Override
  public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
    if (event.getAuthor().isBot()) {
      return;
    }
    
    responseCache.get(event.getGuild()).ifPresent(matcher -> {
      matcher.match(event.getMessage().getContentRaw()).ifPresent(response -> {
        event.getMessage().reply(response).queue();
      });
    });
  }
}
