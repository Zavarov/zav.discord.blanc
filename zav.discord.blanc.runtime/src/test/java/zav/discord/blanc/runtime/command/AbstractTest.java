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

package zav.discord.blanc.runtime.command;

import static com.google.inject.name.Names.named;
import static org.assertj.core.api.Assertions.assertThat;
import static zav.discord.blanc.api.Constants.CLIENT;
import static zav.discord.blanc.api.Constants.DISCORD_TOKEN;
import static zav.discord.blanc.api.Constants.INVITE_SUPPORT_SERVER;
import static zav.discord.blanc.api.Constants.PATTERN;
import static zav.discord.blanc.api.Constants.SHARD_COUNT;
import static zav.discord.blanc.api.Constants.SITE;
import static zav.discord.blanc.db.sql.SqlQuery.ENTITY_DB_PATH;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import zav.discord.blanc.api.Client;
import zav.discord.blanc.api.Command;
import zav.discord.blanc.api.Site;
import zav.discord.blanc.db.Table;
import zav.discord.blanc.reddit.SubredditObservable;

@ExtendWith(MockitoExtension.class)
public abstract class AbstractTest {
  protected static String SUPPORT_SERVER = "https://discord.gg/xxxxxxxxxx";
  protected static long SHARDS = 1L;
  protected static String TOKEN = "xxxxxxxxxx";
  
  protected @Mock Client client;
  protected @Mock ScheduledExecutorService executorService;
  protected @Mock SubredditObservable subredditObservable;
  protected Injector injector;
  protected Cache<Long, Site> siteCache;
  protected Cache<Long, Pattern> patternCache;
  
  @BeforeEach
  public void setUp() throws Exception {
    siteCache = CacheBuilder.newBuilder().build();
    patternCache = CacheBuilder.newBuilder().build();
  }
  
  @AfterEach
  public void tearDown() throws IOException {
    Files.deleteIfExists(ENTITY_DB_PATH);
    Files.deleteIfExists(ENTITY_DB_PATH.getParent());
  }
  
  protected <T> T get(Table<T> db, Object... keys) throws SQLException {
    List<T> response = db.get(keys);
    assertThat(response).hasSize(1);
    return response.get(0);
  }
  
  protected <T> void update(Table<T> db, T entity, Consumer<T> consumer) throws SQLException {
    consumer.accept(entity);
    db.put(entity);
  }
  
  protected <T extends Command> void run(Class<T> clazz) throws Exception {
    T cmd = injector.getInstance(clazz);
    
    cmd.postConstruct();
    cmd.validate();
    cmd.run();;
  }
  
  protected class TestModule extends AbstractModule {
    @Override
    protected void configure() {
      bind(String.class).annotatedWith(named(INVITE_SUPPORT_SERVER)).toInstance(SUPPORT_SERVER);
      bind(String.class).annotatedWith(named(DISCORD_TOKEN)).toInstance(TOKEN);
      bind(Long.class).annotatedWith(named(SHARD_COUNT)).toInstance(SHARDS);
      bind(Client.class).toInstance(client);
      bind(ScheduledExecutorService.class).toInstance(executorService);
      bind(SubredditObservable.class).toInstance(subredditObservable);
  
      bind(new TypeLiteral<Cache<Long, Site>>(){})
            .annotatedWith(Names.named(SITE))
            .toInstance(siteCache);
  
  
      bind(new TypeLiteral<Cache<Long, Pattern>>(){})
            .annotatedWith(Names.named(PATTERN))
            .toInstance(patternCache);
    }
  }
}
