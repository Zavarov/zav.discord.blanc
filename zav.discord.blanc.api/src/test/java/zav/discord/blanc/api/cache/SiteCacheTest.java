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
import static org.junit.jupiter.api.Assertions.assertTrue;

import net.dv8tion.jda.api.entities.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import zav.discord.blanc.api.Site;

/**
 * This test case checks whether the site cache returns the stored site. Note: Given that the cache
 * is not persisted, it is perfectly valid to return an empty site.
 */
@ExtendWith(MockitoExtension.class)
public class SiteCacheTest {
  SiteCache cache;
  @Mock Site.Group group;
  @Mock Message message;
  
  @BeforeEach
  public void setUp() {
    cache = new SiteCache();
  }
  
  /**
   * Use Case: The result should be empty, if no site has been cached for the given message.
   */
  @Test
  public void testGetNone() {
    assertTrue(cache.get(message).isEmpty());
  }
  
  /**
   * Use Case: Return the site cached for the given message.
   */
  @Test
  public void testGet() {
    cache.put(message, group);
    assertEquals(cache.get(message).orElseThrow(), group);
  }
}
