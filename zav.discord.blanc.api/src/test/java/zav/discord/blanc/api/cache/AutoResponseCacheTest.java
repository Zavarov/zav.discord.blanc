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

package zav.discord.blanc.api.cache;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mockStatic;

import java.util.Collections;
import net.dv8tion.jda.api.entities.Guild;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import zav.discord.blanc.api.util.RegularExpressionMatcher;
import zav.discord.blanc.databind.AutoResponseEntity;
import zav.discord.blanc.databind.GuildEntity;

/**
 * This test case checks whether the response cache is able to retrieve the persisted entity as well
 * as update then when necessary.
 */
@ExtendWith(MockitoExtension.class)
public class AutoResponseCacheTest {
  
  GuildEntity entity;
  AutoResponseCache cache;
  AutoResponseEntity autoResponse;
  @Mock Guild guild;
  MockedStatic<GuildEntity> mocked;
  
  /**
   * Initializes the pattern cache. The cache will load a single guild entity, containing an entry
   * to automatically response to the string {@code foo} with {@code bar}.
   */
  @BeforeEach
  public void setUp() {
    autoResponse = new AutoResponseEntity();
    autoResponse.setPattern("foo");
    autoResponse.setAnswer("bar");
    
    entity = new GuildEntity();
    entity.setId(1000L);
    entity.add(autoResponse);
    
    mocked = mockStatic(GuildEntity.class);
    mocked.when(() -> GuildEntity.find(guild)).thenReturn(entity);

    // Initialize the cache and load the entity
    cache = new AutoResponseCache();
    cache.get(guild);
  }
  
  @AfterEach
  public void tearDown() {
    mocked.close();
  }
  
  /**
   * Use Case: The cached entity should be invalidated whenever the entity has changed.
   */
  @Test
  public void testInvalidate() {
    assertNotNull(cache.fetch(guild));

    entity.setAutoResponses(Collections.emptyList());

    cache.invalidate(guild);

    assertNull(cache.fetch(guild));
  }
  
  /**
   * Use Case: Fetch the matcher from the database, if available.
   */
  @Test
  public void testGet() {
    RegularExpressionMatcher result = cache.get(guild).orElseThrow();
    
    assertEquals(result.match("foo").orElseThrow(), "bar");
  }
}
