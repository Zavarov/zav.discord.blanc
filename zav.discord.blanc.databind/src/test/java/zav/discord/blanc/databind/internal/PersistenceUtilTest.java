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
import static org.mockito.Mockito.when;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import net.dv8tion.jda.api.entities.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;

public class PersistenceUtilTest {
  
  MockedStatic<Persistence> mocked;
  @Mock
  EntityManagerFactory emf;
  
  @BeforeEach
  public void setUp() {
    emf = mock(EntityManagerFactory.class);
    when(emf.createEntityManager()).thenReturn(mock(EntityManager.class));
  }
  
  @AfterEach
  public void tearDown() {
    PersistenceUtil.openEntityManagerFactory();
  }
  
  @Test
  public void testUnknownPersistenceProvider() {
    PersistenceUtil.closeEntityManagerFactory();
    
    assertThrows(NullPointerException.class, () -> PersistenceUtil.find(mock(User.class)));
  }
  
  @Test
  public void testCustomPersistenceProvider() {
    PersistenceUtil.setEntityManagerFactory(emf);
    
    assertNotNull(PersistenceUtil.find(mock(User.class)));
  }
}
