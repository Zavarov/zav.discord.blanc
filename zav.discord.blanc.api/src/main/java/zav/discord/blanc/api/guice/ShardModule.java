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

package zav.discord.blanc.api.guice;

import static zav.discord.blanc.api.Constants.PATTERN;
import static zav.discord.blanc.api.Constants.SITE;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;
import java.time.Duration;
import java.util.regex.Pattern;
import org.jetbrains.annotations.Contract;
import zav.discord.blanc.api.Site;

/**
 * The module containing the fields of each individual shard.
 */
public class ShardModule extends AbstractModule {
  private static final int MAX_CACHE_SIZE = 1024;
  private static final Cache<Long, Site> SITE_CACHE = CacheBuilder.newBuilder()
        .expireAfterAccess(Duration.ofHours(1))
        .maximumSize(MAX_CACHE_SIZE)
        .build();
  
  private static final Cache<Long, Pattern> PATTERN_CACHE = CacheBuilder.newBuilder()
        .expireAfterAccess(Duration.ofHours(1))
        .maximumSize(MAX_CACHE_SIZE)
        .build();
  
  @Override
  @Contract(mutates = "this")
  protected void configure() {
    bind(new TypeLiteral<Cache<Long, Site>>(){})
          .annotatedWith(Names.named(SITE))
          .toInstance(SITE_CACHE);
    
    bind(new TypeLiteral<Cache<Long, Pattern>>(){})
          .annotatedWith(Names.named(PATTERN))
          .toInstance(PATTERN_CACHE);
  }
}
