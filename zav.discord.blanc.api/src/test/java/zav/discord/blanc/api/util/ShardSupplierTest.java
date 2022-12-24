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

import static net.dv8tion.jda.api.requests.GatewayIntent.ALL_INTENTS;
import static net.dv8tion.jda.api.requests.GatewayIntent.getIntents;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.spy;

import javax.security.auth.login.LoginException;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import org.apache.commons.lang3.concurrent.TimedSemaphore;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import zav.discord.blanc.api.Client;
import zav.discord.blanc.databind.Credentials;

/**
 * Test case for initializing the JDA instances for all requested shards.
 */
@ExtendWith(MockitoExtension.class)
public class ShardSupplierTest {
  ShardSupplier supplier;
  Credentials credentials;

  @Mock Client client;
  MockedStatic<JDABuilder> jdaBuilder;
  MockedConstruction<TimedSemaphore> rateLimiter;
  
  /**
   * Create a mock of the JDA builder used for creating shard instances.
   *
   * @throws LoginException May only be thrown when the test tries to connect to the Discord server.
   *                        Should never happen.
   */
  @BeforeEach
  public void setUp() throws LoginException {
    JDABuilder builder = spy(JDABuilder.create(getIntents(ALL_INTENTS)));
    doReturn(mock(JDA.class)).when(builder).build();
    
    rateLimiter = mockConstruction(TimedSemaphore.class);
    
    jdaBuilder = mockStatic(JDABuilder.class);
    jdaBuilder.when(() -> JDABuilder.create(anyCollection())).thenReturn(builder);
    
    credentials = spy(new Credentials());
    doReturn("token").when(credentials).getToken();
    doReturn(2L).when(credentials).getShardCount();
    
    supplier = new ShardSupplier(client, credentials);
  }
  
  @AfterEach
  public void tearDown() {
    jdaBuilder.close();
    rateLimiter.close();
  }
  
  /**
   * Use Case: Only create as name JDA instances as there are shards.
   */
  @Test
  public void testHasNext() {
    assertTrue(supplier.hasNext());
    supplier.next();
    assertTrue(supplier.hasNext());
    supplier.next();
    assertFalse(supplier.hasNext());
  }
  
  /**
   * Use Case: Create a JDA instance for each shard.
   */
  @Test
  public void testNext() {
    assertNotNull(supplier.next());
    assertNotNull(supplier.next());
    assertThrows(Exception.class, () -> supplier.next());
  }
}
