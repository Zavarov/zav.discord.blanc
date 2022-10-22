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

package zav.discord.blanc.api.util;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.stream.Stream;
import org.apache.commons.jexl3.JexlException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * This class checks whether the Jexl implementation is able to process mathematical expressions.
 *
 */
public class JexlParserTest {
  
  static Stream<Arguments> getArguments() {
    return Stream.of(
          Arguments.of("tan(1.5)", Math.tan(1.5)),
          Arguments.of("sqrt(1.5)", Math.sqrt(1.5)),
          Arguments.of("sin(1.5)", Math.sin(1.5)),
          Arguments.of("min(-1,2)", Math.min(-1, 2)),
          Arguments.of("max(-1,2)", Math.max(-1, 2)),
          Arguments.of("max(-1,2)", Math.max(-1, 2)),
          Arguments.of("log(1.5)", Math.log(1.5)),
          Arguments.of("log10(1.5)", Math.log10(1.5)),
          Arguments.of("floor(1.5)", Math.floor(1.5)),
          Arguments.of("cos(1.5)", Math.cos(1.5)),
          Arguments.of("ceil(1.5)", Math.ceil(1.5)),
          Arguments.of("atan(1.5)", Math.atan(1.5)),
          Arguments.of("asin(1.0)", Math.asin(1.0)),
          Arguments.of("acos(1.0)", Math.acos(1.0)),
          Arguments.of("abs(1.0)", Math.abs(-1)),
          Arguments.of("pow(2.0, 3.0)", Math.pow(2, 3)),
          Arguments.of("7-3.33", 7 - 3.33),
          Arguments.of("7+3.33", 7 + 3.33),
          Arguments.of("5%3", 5 % 3),
          Arguments.of("3/2", 1),
          Arguments.of("3/2.0", 1.5),
          Arguments.of("3.0/2", 1.5),
          Arguments.of("1.5*3", 1.5 * 3),
          Arguments.of("e", Math.E),
          Arguments.of("pi", Math.PI),
          Arguments.of("1.0", 1.0),
          Arguments.of("-1.0", -1.0),
          Arguments.of("1.0F", 1.0),
          Arguments.of("-1.0F", -1.0),
          Arguments.of("1", 1),
          Arguments.of("-1", -1),
          Arguments.of("1L", 1),
          Arguments.of("-1L", -1),
          Arguments.of("pow(sin(e), pi)", Math.pow(Math.sin(Math.E), Math.PI)),
          Arguments.of("sin(e)+cos(e)", Math.sin(Math.E) + Math.cos(Math.E)),
          Arguments.of("asin(1.5)", Double.NaN),
          Arguments.of("acos(1.5)", Double.NaN)
    );
  }
  
  static Stream<String> getInvalidArguments() {
    return Stream.of(
          "x",
          "x^1",
          "1^x",
          "random(x,1)",
          "random(1,x)",
          "random(99999999,1)",
          "random(1,99999999)",
          "abs(x)",
          "acos(x)",
          "asin(x)",
          "atan(x)",
          "ceil(x)",
          "cos(x)",
          "floor(x)",
          "log(x)",
          "ln(x)",
          "max(x,1)",
          "max(1,x)",
          "min(x,1)",
          "min(1,x)",
          "sin(x)",
          "sqrt(x)",
          "tan(x)"
    );
  }
  
  JexlParser parser;
  
  @BeforeEach
  public void setUp() {
    parser = new JexlParser();
  }
  
  /**
   * Calculates the numerical value and compares it to the expected value.<br>
   * Arguments are provided via {@link #getArguments()}.
   *
   * @param key The arithmetic expression that is handled.
   * @param value The expected numerical value.
   */
  @ParameterizedTest
  @MethodSource("getArguments")
  public void testExpression(String key, Number value) {
    assertEquals(parser.evaluate(key).doubleValue(), value.doubleValue(), 1e-15);
  }
  
  /**
   * Use Case: Invalid expressions should throw an exception.
   *
   * @param key The invalid arithmetic expression that is handled.
   */
  @ParameterizedTest
  @MethodSource("getInvalidArguments")
  public void testExpression(String key) {
    assertThrows(JexlException.class, () -> parser.evaluate(key));
  }
}
