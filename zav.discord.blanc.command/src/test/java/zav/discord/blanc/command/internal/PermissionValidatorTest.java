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

package zav.discord.blanc.command.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import zav.discord.blanc.command.InsufficientPermissionException;
import zav.discord.blanc.databind.Rank;
import zav.discord.blanc.databind.UserEntity;

/**
 * Checks if the correct exception is thrown when called by a user with insufficient permissions.
 */
@ExtendWith(MockitoExtension.class)
public class PermissionValidatorTest {
  UserEntity userEntity;
  
  @Mock Member author;
  @Mock User user;
  @Mock TextChannel textChannel;
  MockedStatic<UserEntity> mocked;
  
  PermissionValidator validator;
  Set<Permission> permissions;
  
  /**
   * Initializes the permission validator. By default, every user has administrative permissions.
   */
  @BeforeEach
  public void setUp() {
    userEntity = new UserEntity();
    validator = new PermissionValidator(author, textChannel);
    permissions = EnumSet.of(Permission.ADMINISTRATOR);
    
    mocked = mockStatic(UserEntity.class);
    mocked.when(() -> UserEntity.find(user)).thenReturn(userEntity);

    when(author.getUser()).thenReturn(user);
  }
  
  @AfterEach
  public void tearDown() {
    mocked.close();
  }
  
  /**
   * Use Case: Super-users should bypass any and all validations.
   *
   * @throws Exception Thrown by the validation method.
   */
  @Test
  public void testValidateAsRoot() throws Exception {
    userEntity.setRanks(List.of(Rank.ROOT));

    when(author.getPermissions(textChannel)).thenReturn(EnumSet.noneOf(Permission.class));
  
    validator.validate(permissions);
  }
  
  /**
   * Use Case: Users with the required permissions should pass the validation check.
   *
   * @throws Exception Thrown by the validation method.
   */
  @Test
  public void testValidate() throws Exception {
    userEntity.setRanks(List.of(Rank.USER));

    when(author.getPermissions(textChannel)).thenReturn(EnumSet.of(Permission.ADMINISTRATOR));

    validator.validate(permissions);
  }
  
  /**
   * Use Case: Users with insufficient permissions should trigger an exception.
   */
  @Test
  public void testValidateWithInsufficientPermissions() {
    userEntity.setRanks(List.of(Rank.USER));

    when(author.getPermissions(textChannel)).thenReturn(EnumSet.noneOf(Permission.class));

    InsufficientPermissionException error = validateError(() -> validator.validate(permissions));
    MessageEmbed errorMessage = error.getErrorMessage();

    assertEquals(errorMessage.getTitle(), "Insufficient Permissions");
    assertTrue(errorMessage.getDescription().contains(Permission.ADMINISTRATOR.getName()));
  }
  
  private InsufficientPermissionException validateError(Executable checker) {
    return assertThrows(InsufficientPermissionException.class, checker);
  }
}
