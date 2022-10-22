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

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import org.apache.commons.jexl3.JexlBuilder;
import org.apache.commons.jexl3.JexlEngine;
import org.apache.commons.jexl3.JexlExpression;
import org.apache.commons.jexl3.MapContext;
import org.eclipse.jdt.annotation.NonNullByDefault;

/**
 * The Jexl parser implements the simple evaluation of mathematical expressions. Supported are
 * numbers, the constants {@link Math#PI} and {@link Math#E}, as well as all methods provided by the
 * {@link Math} class.
 */
@NonNullByDefault
public final class JexlParser {
  private static class InstanceHolder {
    private static final Map<String, Object> CTX = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    private static final Map<String, Object> NS = new HashMap<>();
    
    private static final JexlEngine JEXL = new JexlBuilder()
          .cache(512)
          .strict(true)
          .silent(false)
          .namespaces(NS)
          .create();
    
    static {
      // Use Math as default namespace. e.g. sin(5) instead of math:sin(5)
      NS.put(null, Math.class);
      // Define mathematical constants
      CTX.put("e", Math.E);
      CTX.put("pi", Math.PI);
    }
  }
  
  /**
   * Evaluates the given arithmetical expression.<br>
   * Example: {@code "1*sin(pi)"} returns {@code 0}
   *
   * @param value The string representation of an
   * @return The numerical result.
   */
  public Number evaluate(String value) {
    JexlExpression expression = JexlParser.InstanceHolder.JEXL.createExpression(value);
    Object result = expression.evaluate(new MapContext(InstanceHolder.CTX));
    return (Number) result;
  }
}
