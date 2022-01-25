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

import static net.dv8tion.jda.api.requests.GatewayIntent.ALL_INTENTS;
import static net.dv8tion.jda.api.requests.GatewayIntent.getIntents;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.spy;
import static zav.discord.blanc.api.Constants.DISCORD_TOKEN;
import static zav.discord.blanc.api.Constants.SHARD_COUNT;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.name.Names;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import javax.security.auth.login.LoginException;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import org.apache.commons.lang3.concurrent.TimedSemaphore;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import zav.discord.blanc.api.Parser;

/**
 * Test case for initializing the JDA instances for all requested shards.
 */
public class JdaShardSupplierTest {
  JdaShardSupplier supplier;

  MockedStatic<JDABuilder> jdaBuilder;
  MockedConstruction<TimedSemaphore> rateLimiter;
  
  /**
   * Create a mock of the JDA builder used for creating shard instances.
   *
   * @throws LoginException May only be thrown when the test tries to connect to the Discord server.
   *                        Should never happen.
   */
  @BeforeEach
  @SuppressWarnings("all") // Suppress unused return type of JDABuilder.create(...)
  public void setUp() throws LoginException {
    JDABuilder builder = spy(JDABuilder.create(getIntents(ALL_INTENTS)));
    doReturn(mock(JDA.class)).when(builder).build();
    
    rateLimiter = mockConstruction(TimedSemaphore.class);
    
    jdaBuilder = mockStatic(JDABuilder.class);
    jdaBuilder.when(() -> JDABuilder.create(anyCollection())).thenReturn(builder);
    
    Injector injector = Guice.createInjector(new TestModule());
    supplier = injector.getInstance(JdaShardSupplier.class);
  }
  
  @AfterEach
  public void tearDown() {
    jdaBuilder.close();
    rateLimiter.close();
  }
  
  @Test
  public void testHasNext() {
    assertThat(supplier.hasNext()).isTrue();
    supplier.next();
    assertThat(supplier.hasNext()).isTrue();
    supplier.next();
    assertThat(supplier.hasNext()).isFalse();
  }
  
  @Test
  public void testNext() {
    assertThat(supplier.next()).isNotNull();
    assertThat(supplier.next()).isNotNull();
    assertThatThrownBy(() -> supplier.next()).isInstanceOf(Exception.class);
  }
  
  private static class TestModule extends AbstractModule {
    @Override
    protected void configure() {
      bind(ExecutorService.class).toInstance(Executors.newScheduledThreadPool(1));
      bind(ScheduledExecutorService.class).toInstance(Executors.newScheduledThreadPool(1));
      
      bind(Long.class).annotatedWith(Names.named(SHARD_COUNT)).toInstance(2L);
      bind(String.class).annotatedWith(Names.named(DISCORD_TOKEN)).toInstance("token");
      
      bind(Parser.class).toInstance(mock(Parser.class));
    }
  }
}
