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

package zav.discord.blanc.api.internal;

import static zav.discord.blanc.api.Constants.PATTERN;
import static zav.discord.blanc.api.Constants.SITE;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ScheduledExecutorService;
import java.util.regex.Pattern;
import net.dv8tion.jda.api.entities.Message;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import zav.discord.blanc.api.Parser;
import zav.discord.blanc.api.Site;

/**
 * The base class for all Listener related JUnit tests.
 */
public abstract class AbstractListenerTest {
  
  protected @Mock Parser parser;
  protected @Mock ScheduledExecutorService queue;
  
  protected Cache<Message, Site> siteCache;
  protected Cache<Long, Pattern> patternCache;
  protected Injector injector;
  
  /**
   * Initializes the Guice injector.
   *
   * @throws Exception When the initialization failed.
   */
  @BeforeEach
  public void setUp() throws Exception {
    siteCache = CacheBuilder.newBuilder().build();
    patternCache = CacheBuilder.newBuilder().build();
    injector = Guice.createInjector(new TestModule());
  }
  
  /**
   * Guice module used for Unit testing.
   */
  protected class TestModule extends AbstractModule {
    @Override
    protected void configure() {
      bind(Parser.class).toInstance(parser);
      bind(ScheduledExecutorService.class).toInstance(queue);
      bind(new TypeLiteral<Cache<Message, Site>>(){})
            .annotatedWith(Names.named(SITE))
            .toInstance(siteCache);
      bind(new TypeLiteral<Cache<Long, Pattern>>(){})
            .annotatedWith(Names.named(PATTERN))
            .toInstance(patternCache);
    }
  }
}
