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
import java.util.List;
import java.util.regex.Pattern;
import net.dv8tion.jda.api.entities.Guild;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import zav.discord.blanc.databind.GuildEntity;

/**
 * This test case checks whether the pattern cache is able to retrieve the persisted entity as well
 * as update then when necessary.
 */
@ExtendWith(MockitoExtension.class)
public class PatternCacheTest {
  
  GuildEntity entity;
  PatternCache cache;
  @Mock Guild guild;
  MockedStatic<GuildEntity> mocked;
  
  /**
   * Initializes the pattern cache. The cache will load a single guild entity, containing both the
   * words {@code banana} and {@code pizza} as blacklisted expressions.
   */
  @BeforeEach
  public void setUp() {
    entity = new GuildEntity();
    entity.setId(1000L);
    entity.setBlacklist(List.of("banana", "pizza"));
    
    mocked = mockStatic(GuildEntity.class);
    mocked.when(() -> GuildEntity.find(guild)).thenReturn(entity);

    cache = new PatternCache();
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

    entity.setBlacklist(Collections.emptyList());

    cache.invalidate(guild);

    assertNull(cache.fetch(guild));
  }
  
  /**
   * Use Case: Fetch the pattern from the database, if available.
   */
  @Test
  public void testGet() {
    Pattern result = cache.get(guild).orElseThrow();

    assertEquals(result.toString(), "banana|pizza");
  }
}
