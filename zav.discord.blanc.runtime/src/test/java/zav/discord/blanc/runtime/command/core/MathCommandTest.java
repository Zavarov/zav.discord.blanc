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

package zav.discord.blanc.runtime.command.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import zav.discord.blanc.api.util.JexlParser;
import zav.discord.blanc.runtime.command.AbstractTest;

/**
 * Checks whether the correct answer is returned for a given arithmetic expression.
 */
@ExtendWith(MockitoExtension.class)
public class MathCommandTest extends AbstractTest {
  
  @Mock OptionMapping value;
  JexlParser parser;
  MathCommand command;
  
  /**
   * Initializes the command with argument {@code 12345}.
   */
  @BeforeEach
  public void setUp() {
    when(event.getOption(anyString())).thenReturn(value);
    when(value.getAsString()).thenReturn("12345");
    
    parser = new JexlParser();
    command = new MathCommand(event, manager, parser);
  }
  
  @Test
  public void testEvaluate() {
    when(event.reply(response.capture())).thenReturn(reply);
    
    command.run();
    
    assertEquals(response.getValue(), "12345");
  }
}
