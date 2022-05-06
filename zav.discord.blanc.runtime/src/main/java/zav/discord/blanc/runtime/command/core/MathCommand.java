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

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import org.apache.commons.jexl3.JexlBuilder;
import org.apache.commons.jexl3.JexlEngine;
import org.apache.commons.jexl3.JexlExpression;
import org.apache.commons.jexl3.MapContext;
import zav.discord.blanc.command.AbstractCommand;

/**
 * This command can solve simple mathematical expressions.
 */
public class MathCommand extends AbstractCommand {
  private static class InstanceHolder {
    
    private static Map<String, Object> CTX = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    private static Map<String, Object> NS = new HashMap<>();
    
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
  
  private String value;
  
  @Override
  public void postConstruct() {
    value = Objects.requireNonNull(event.getOption("value")).getAsString();
  }
  
  @Override
  public void run() {
    JexlExpression expression = InstanceHolder.JEXL.createExpression(value);
    Object result = expression.evaluate(new MapContext(InstanceHolder.CTX));
    event.reply(result.toString()).complete();
  }
}
