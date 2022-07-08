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

package zav.discord.blanc.api;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import java.time.Duration;
import java.util.Optional;
import java.util.regex.Pattern;
import net.dv8tion.jda.api.entities.Guild;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.jetbrains.annotations.Contract;
import zav.discord.blanc.databind.GuildEntity;

/**
 * A cache built on top of the JPA persistence layer storing all blacklisted guild patterns.<br>
 * Since the pattern is applied to every guild message, database access should be avoided as much as
 * possible. Messages matching the pattern are deleted automatically.
 */
@NonNullByDefault
public class PatternCache {
  private static final int MAX_CACHE_SIZE = 1024;
  private final LoadingCache<Guild, Optional<Pattern>> cache;
  private final EntityManagerFactory factory;
  
  /**
   * Creates a new instance for the given persistence manager. The instance is managed by the
   * {@link Client} class.
   *
   * @param factory The persistence manager.
   */
  /*package*/ PatternCache(EntityManagerFactory factory) {
    this.factory = factory;
    this.cache = CacheBuilder.newBuilder()
          .expireAfterAccess(Duration.ofHours(1))
          .maximumSize(MAX_CACHE_SIZE)
          .build(CacheLoader.from(this::fetch));
  }

  /**
   * Removes the provided guild from the cache.
   *
   * @param guild One of the guilds available to the program.
   */
  @Contract(mutates = "this")
  public void invalidate(Guild guild) {
    cache.invalidate(guild);
  }

  /**
   * Returns the pattern cached for the provided guild. If the pattern isn't cached yet, it is
   * retrieved from the database. If no pattern is cached, returns {@link Optional#empty()},
   * otherwise the optional contains the corresponding pattern.
   *
   * @param guild One of the guilds available to the program.
   * @return As described.
   */
  @Contract(pure = true)
  public Optional<Pattern> get(Guild guild) {
    return cache.getUnchecked(guild);
  }
  
  private Optional<Pattern> fetch(Guild guild) {
    try (EntityManager entityManager = factory.createEntityManager()) {
      return GuildEntity.getOrCreate(entityManager, guild).getPattern();
    }
  }
}
