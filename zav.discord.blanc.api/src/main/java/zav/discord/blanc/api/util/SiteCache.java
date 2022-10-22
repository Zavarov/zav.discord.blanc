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

package zav.discord.blanc.api.util;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import java.time.Duration;
import java.util.Optional;
import net.dv8tion.jda.api.entities.Message;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.jetbrains.annotations.Contract;
import zav.discord.blanc.api.Client;
import zav.discord.blanc.api.Site;

/**
 * A cache for keeping track of all interactive messages. The user can use buttons and menus to
 * change the type of information that is displayed by the message.<br>
 * Those messages are not persisted.
 */
@NonNullByDefault
public class SiteCache {
  private static final int MAX_CACHE_SIZE = 1024;
  private final Cache<Long, Site> cache;
  
  /**
   * Creates a new instance. The instance is managed by the {@link Client} class.
   */
  public SiteCache() {
    cache = CacheBuilder.newBuilder()
          .expireAfterAccess(Duration.ofHours(1))
          .maximumSize(MAX_CACHE_SIZE)
          .build();
  }
  
  /**
   * Caches a new site.
   *
   * @param message The message corresponding to the site.
   * @param site The site instance.
   */
  @Contract(mutates = "this")
  public void put(Message message, Site site) {
    cache.put(message.getIdLong(), site);
  }
  
  /**
   * Returns the site cached for the given message. Returns {@link Optional#empty()}, if no matching
   * site is cached. Otherwise the optional contains the corresponding site.
   *
   * @param message The message corresponding to the site.
   * @return As described.
   */
  @Contract(pure = true)
  public Optional<Site> get(Message message) {
    return Optional.ofNullable(cache.getIfPresent(message.getIdLong()));
  }
}
