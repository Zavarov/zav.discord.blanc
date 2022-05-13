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

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import zav.discord.blanc.command.internal.PermissionValidator;
import zav.discord.blanc.command.internal.RankValidator;

/**
 * Checks whether guild commands fail if a user with insufficient rank or permission tries to
 * execute them.
 */
@ExtendWith(MockitoExtension.class)
public class AbstractGuildCommandTest {
  
  @Mock PermissionValidator permissionValidator;
  @Mock RankValidator rankValidator;
  AbstractGuildCommand command;
  
  @BeforeEach
  public void setUp() {
    command = new GuildCommand();
    command.setValidator(permissionValidator);
    command.setValidator(rankValidator);
  }
  
  @Test
  public void testValidate() throws Exception {
    // Everything ok
    command.validate();

    // Not enough permissions
    doThrow(InsufficientPermissionException.class).when(permissionValidator).validate(any());
    assertThrows(InsufficientPermissionException.class, () -> command.validate());
  
    // Not enough ranks & ranks are validated before permissions
    doThrow(InsufficientRankException.class).when(rankValidator).validate(any());
    assertThrows(InsufficientRankException.class, () -> command.validate());
  }
  
  private static final class GuildCommand extends AbstractGuildCommand {
    @Override
    public void run() {}
  }
}
