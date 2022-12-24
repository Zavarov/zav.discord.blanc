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

package zav.discord.blanc.runtime.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import zav.discord.blanc.api.Command;
import zav.discord.blanc.api.CommandProvider;
import zav.discord.blanc.runtime.command.AbstractTest;

/**
 * Test case to check whether the correct commands are returned for a given slash event.
 */
@ExtendWith(MockitoExtension.class)
public class SimpleCommandParserTest extends AbstractTest {
  
  @Mock Command command;
  @Mock CommandProvider provider;
  SimpleCommandParser parser;
  
  @BeforeEach
  public void setUp() {
    parser = new SimpleCommandParser(shard, provider);
  }
  
  @Test
  public void testParseCommand() {
    when(provider.create(any(), any())).thenReturn(Optional.of(command));
    
    Optional<Command> response = parser.parse(event);
    
    assertEquals(response.get(), command);
  }
  
  @Test
  public void testParseUnknownCommand() {
    Optional<Command> response = parser.parse(event);
    
    assertTrue(response.isEmpty());
  }
}
