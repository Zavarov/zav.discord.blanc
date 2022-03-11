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
import zav.discord.blanc.command.parser.NumberParameter;

/**
 * Test case for the number argument.<br>
 * Verifies that the correct decimal and string representation is retrieved from an argument.
 */
public class NumberParameterTest {
  NumberParameter argument;
  
  /**
   * Initializes the resolver. Furthermore, an argument that always returns the decimal value of
   * 10 and the corresponding string is returned.
   */
  @BeforeEach
  public void setUp() {
    argument = mock(NumberParameter.class);
    when(argument.asNumber()).thenReturn(Optional.of(BigDecimal.TEN));
    when(argument.asString()).thenCallRealMethod();
  }
  
  @Test
  public void testGetString() {
    assertThat(argument.asString()).contains("10");
  }
}
