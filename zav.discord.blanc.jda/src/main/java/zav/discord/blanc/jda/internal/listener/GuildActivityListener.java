/*
 * Copyright (c) 2021 Zavarov.
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

package zav.discord.blanc.jda.internal.listener;

import com.google.common.base.Supplier;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.time.Duration;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import zav.discord.blanc.api.Guild;

/**
 * This listener measures the activity of every guild in its registered shard.<br>
 * It intercepts and counts all guild messages
 */
public class GuildActivityListener extends ListenerAdapter {
  /**
   * Cache builder for the message cache.<br>
   * All messages should only be kept for a minute, in order to compute the activity per minute.
   */
  private static final CacheBuilder<Object, Object> cacheBuilder = CacheBuilder
        .newBuilder()
        .expireAfterWrite(Duration.ofMinutes(1));
  
  /**
   * Cache over all guilds.<br>
   * Each guild should only cache the activity over one day.
   */
  private static final LoadingCache<Long, Cache<Long, Message>> guildCache = CacheBuilder
        .newBuilder()
        .expireAfterWrite(Duration.ofDays(1))
        .build(CacheLoader.from((Supplier<Cache<Long, Message>>) cacheBuilder::build));

  public GuildActivityListener() {
  }
  
  @Override
  public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
    Cache<Long, Message> messageCache = guildCache.getUnchecked(event.getGuild().getIdLong());
    messageCache.put(event.getMessageIdLong(), event.getMessage());
  }
  
  /**
   * Calculates the estimated activity of the given guild per minute.
   *
   * @param guild A view over one of the guilds in the observed shard.
   * @return The activity of the specified guild
   */
  public static double getActivity(Guild guild) {
    Cache<Long, Message> messageCache = guildCache.getUnchecked(guild.getAbout().getId());
    
    // Get as accurate as possible and delete outdated messages
    messageCache.cleanUp();
    
    return messageCache.size();
  }
}
