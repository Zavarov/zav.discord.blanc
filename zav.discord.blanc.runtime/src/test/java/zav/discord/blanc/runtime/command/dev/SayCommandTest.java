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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import zav.discord.blanc.runtime.command.AbstractTest;

/**
 * Check whether the command repeats the given argument.
 */
@ExtendWith(MockitoExtension.class)
public class SayCommandTest extends AbstractTest {
  @Mock OptionMapping content;
  SayCommand command;
  
  /**
   * Initializes the command with the argument {@code Hello World}.
   */
  @BeforeEach
  public void setUp() {
    when(event.getOption(anyString())).thenReturn(content);
    when(content.getAsString()).thenReturn("Hello World");
    
    command = new SayCommand(event, manager);
  }
  
  @Test
  public void testSend() {
    command.run();
    
    assertThat(response.getValue()).isEqualTo("Hello World");
  }
  
}
