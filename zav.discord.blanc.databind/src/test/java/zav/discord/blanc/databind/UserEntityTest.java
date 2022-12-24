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

package zav.discord.blanc.databind;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.Mockito.when;

import net.dv8tion.jda.api.entities.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class UserEntityTest {
  UserEntity userEntity;
  
  @Mock User user;
  
  @BeforeEach
  public void setUp() {
    userEntity = UserEntity.find(user);
    userEntity.merge();
  }
  
  @AfterEach
  public void tearDown() {
    UserEntity.remove(user);
  }
  
  @Test
  public void testFindUser() {
    assertEquals(UserEntity.find(user).getId(), userEntity.getId());
  }
  
  @Test
  public void testFindUnknownUser() {
    when(user.getIdLong()).thenReturn(Long.MAX_VALUE);
    
    assertNotEquals(UserEntity.find(user).getId(), userEntity.getId());
  }
}
