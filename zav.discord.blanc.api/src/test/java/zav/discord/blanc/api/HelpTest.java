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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Checks whether the correct help file is read from disk.
 */
public class HelpTest {
  
  Command command;
  
  @BeforeEach
  public void setUp() {
    command = new TestCommand();
  }
  
  @Test
  public void getHelpTest() throws IOException {
    assertThat(Help.getHelp(command.getClass()).getDescription()).contains("TestCommand");
  }
  
  private static final class TestCommand implements Command {
    @Override
    public void validate() {
      // Do nothing
    }
  
    @Override
    public void run() {
      // Do nothing
    }
  }
}
