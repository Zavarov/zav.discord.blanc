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

package zav.discord.blanc.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

/**
 * Test class for managing executable commands.
 */
public class CommandsTest {
  
  @AfterEach
  public void tearDown() {
    Commands.clear();
  }
  
  @Test
  public void testBind() {
    assertTrue(Commands.bind("command", Command.class));
    // Duplicate
    assertFalse(Commands.bind("command", Command.class));
  }
  
  @Test
  public void testGet() {
    assertTrue(Commands.bind("command", Command.class));
    assertThat(Commands.get("command")).contains(Command.class);
    
    Commands.clear();
    
    assertThat(Commands.get("command")).isEmpty();
  }
}
