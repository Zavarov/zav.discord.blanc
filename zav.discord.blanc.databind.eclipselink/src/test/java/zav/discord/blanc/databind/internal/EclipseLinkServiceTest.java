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

package zav.discord.blanc.databind.internal;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

import net.dv8tion.jda.api.entities.Guild;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test whether the entity manager factory is correctly created within the OSGi
 * lifecycle.
 */
public class EclipseLinkServiceTest {
  EclipseLinkService service;
  
  /**
   * Imitates the service creation performed by OSGi when loading this fragment.
   */
  @BeforeEach
  public void setUp() {
    PersistenceUtil.closeEntityManagerFactory();
    
    service = new EclipseLinkService();
  }
  
  @Test
  public void testLifecycle() {
    assertThrows(NullPointerException.class, () -> PersistenceUtil.find(mock(Guild.class)));
    
    service.activate();
    
    assertNotNull(PersistenceUtil.find(mock(Guild.class)));
    
    service.deactivate();
    
    assertThrows(NullPointerException.class, () -> PersistenceUtil.find(mock(Guild.class)));
  }
}
