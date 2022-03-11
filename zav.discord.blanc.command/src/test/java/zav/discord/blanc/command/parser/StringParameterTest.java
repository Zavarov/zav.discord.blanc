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

package zav.discord.blanc.command.parser;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import zav.discord.blanc.command.parser.StringParameter;

/**
 * Test case for the string argument.<br>
 * Verifies that the correct decimal and string representation is retrieved from an argument.
 */
public class StringParameterTest {
  StringParameter argument;
  
  @BeforeEach
  public void setUp() {
    argument = mock(StringParameter.class);
    when(argument.asNumber()).thenCallRealMethod();
  }
  
  @Test
  public void testGetInvalidNumber() {
    when(argument.asString()).thenReturn(Optional.of("x"));
    assertThat(argument.asNumber()).isEmpty();
  }
  
  @Test
  public void testGetNumber() {
    when(argument.asString()).thenReturn(Optional.of("21"));
    assertThat(argument.asNumber()).contains(BigDecimal.valueOf(21));
    
    when(argument.asString()).thenReturn(Optional.of("3.14"));
    assertThat(argument.asNumber()).contains(BigDecimal.valueOf(3.14));
  }
}
