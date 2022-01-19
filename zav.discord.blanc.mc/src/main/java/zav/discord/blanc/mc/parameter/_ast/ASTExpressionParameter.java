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

package zav.discord.blanc.mc.parameter._ast;

import de.monticore.prettyprint.IndentPrinter;
import java.math.BigDecimal;
import java.util.Optional;
import zav.discord.blanc.api.Parameter;
import zav.mc.math.ArithmeticExpressionsPrettyPrinter;
import zav.mc.math.ArithmeticExpressionsValueCalculator;

/**
 * Interface between an arithmetic expression and a command argument.
 */
public class ASTExpressionParameter extends ASTExpressionParameterTOP implements Parameter {
  private static final ArithmeticExpressionsPrettyPrinter prettyPrinter =
        new ArithmeticExpressionsPrettyPrinter(new IndentPrinter());
  
  @Override
  public Optional<BigDecimal> asNumber() {
    return ArithmeticExpressionsValueCalculator.valueOf(getExpression());
  }

  @Override
  public Optional<String> asString() {
    return Optional.of(prettyPrinter.prettyprint(getExpression()));
  }
}
