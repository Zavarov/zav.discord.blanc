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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.github.stefanbirkner.systemlambda.SystemLambda;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import zav.discord.blanc.command.CommandManager;
import zav.discord.blanc.databind.Rank;
import zav.discord.blanc.runtime.command.AbstractTest;

/**
 * Check whether all threads are terminated.
 */
@ExtendWith(MockitoExtension.class)
public class KillCommandTest extends AbstractTest {
  CommandManager manager;
  KillCommand command;
  
  /**
   * Initializes the command with no arguments.
   */
  @BeforeEach
  public void setUp() {
    manager = new CommandManager(shard, event);
    command = new KillCommand(event, manager);
  }
  
  @Test
  public void testShutdown() throws Exception {
    SystemLambda.catchSystemExit(() -> command.run());
    
    verify(jda, times(1)).shutdown();
    verify(queue, times(1)).shutdown();
  }
  
  @Test
  public void testGetRequiredRank() {
    assertEquals(command.getRequiredRank(), Rank.DEVELOPER);
  }
}
