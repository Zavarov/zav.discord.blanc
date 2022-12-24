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

package zav.discord.blanc.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import zav.discord.blanc.databind.Rank;

/**
 * Checks whether guild commands fail if a user with insufficient rank or permission tries to
 * execute them.
 */
@ExtendWith(MockitoExtension.class)
public class AbstractGuildCommandTest {
  
  @Mock GuildCommandManager manager;
  AbstractGuildCommand command;
  
  /**
   * Initializes a dummy guild command with the mocked permission and rank validator.
   */
  @BeforeEach
  public void setUp() {
    command = new GuildCommand(manager);
  }
  
  /**
   * Use Case: Execute developer or moderator commands. This command should only be available to
   * users with the required authorization.
   *
   * @throws Exception Thrown by the validation method.
   */
  @Test
  public void testValidate() throws Exception {
    // Everything ok
    command.validate();
  }
  
  /**
   * Use Case: If the user doesn't have enough permissions, an exception should be thrown.
   *
   * @throws Exception Thrown by the validation method.
   */
  @Test
  public void testValidateInsufficientPermission() throws Exception {
    // Not enough permissions
    doNothing().when(manager).validate(any(Rank.class));
    doThrow(InsufficientPermissionException.class).when(manager).validate(anySet());
    assertThrows(InsufficientPermissionException.class, () -> command.validate());
  }
  
  /**
   * Use Case: If the user doesn't have the required rank, an exception should be thrown.
   *
   * @throws Exception Thrown by the validation method.
   */
  @Test
  public void testValidateInsufficientRank() throws Exception {
    // Not enough ranks & ranks are validated before permissions
    doThrow(InsufficientRankException.class).when(manager).validate(any(Rank.class));
    assertThrows(InsufficientRankException.class, () -> command.validate());
  }
  
  /**
   * Use Case: Language specific messages should be read from the local properties file.
   */
  @Test
  public void testGetMessage() {
    assertEquals(command.getMessage("foo"), "bar");
  }
  
  private static final class GuildCommand extends AbstractGuildCommand {
    
    private GuildCommand(GuildCommandManager manager) {
      super(manager);
    }
  
    @Override
    public void run() {}
  }
}
