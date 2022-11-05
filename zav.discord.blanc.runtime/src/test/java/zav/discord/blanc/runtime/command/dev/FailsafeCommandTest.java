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

package zav.discord.blanc.runtime.command.dev;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import zav.discord.blanc.command.CommandManager;
import zav.discord.blanc.databind.Rank;
import zav.discord.blanc.runtime.command.AbstractTest;

/**
 * Check whether developer can request and relinquish super-user privileges.
 */
@ExtendWith(MockitoExtension.class)
public class FailsafeCommandTest extends AbstractTest {
  
  CommandManager manager;
  FailsafeCommand command;
  
  /**
   * Initializes the command with no arguments.
   */
  @BeforeEach
  public void setUp() {
    manager = new CommandManager(client, event);
    command = new FailsafeCommand(event, manager);
  }
  
  @Test
  public void testBecomeRoot() throws Exception {
    userEntity.setRanks(new ArrayList<>(List.of(Rank.DEVELOPER)));
    
    command.run();
    
    assertEquals(userEntity.getRanks(), List.of(Rank.ROOT));
  }
  
  @Test
  public void testBecomeDeveloper() throws Exception {
    userEntity.setRanks(new ArrayList<>(List.of(Rank.ROOT)));
    
    command.run();
    
    assertEquals(userEntity.getRanks(), List.of(Rank.DEVELOPER));
  }
}
