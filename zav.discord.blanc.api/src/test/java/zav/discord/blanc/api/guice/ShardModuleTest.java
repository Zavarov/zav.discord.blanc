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

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static zav.discord.blanc.api.Constants.PATTERN;
import static zav.discord.blanc.api.Constants.SITE;

import com.google.common.cache.Cache;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;
import java.util.regex.Pattern;
import net.dv8tion.jda.api.entities.Guild;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import zav.discord.blanc.api.Site;

/**
 * Test case to check that the caches are properly injected.
 */
public class ShardModuleTest {
  Injector injector;
  
  @BeforeEach
  public void setUp() {
    injector = Guice.createInjector(new ShardModule());
  }
  
  /**
   * Use Case: Cache all interactive messages such that the program can respond to user actions.
   */
  @Test
  public void testGetSiteCache() {
    Key<Cache<Long, Site>> key = Key.get(new TypeLiteral<>(){}, Names.named(SITE));
    assertNotNull(injector.getInstance(key));
  }
  
  /**
   * Use Case: Cache the regular expression over all banned expressions such that it doesn't
   * have to be computed over and over again.
   */
  @Test
  public void testPatternCache() {
    Key<Cache<Guild, Pattern>> key = Key.get(new TypeLiteral<>(){}, Names.named(PATTERN));
    assertNotNull(injector.getInstance(key));
  }
}
