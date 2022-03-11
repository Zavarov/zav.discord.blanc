/*
 * Copyright (c) 2020 Zavarov
 *
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

import java.math.BigDecimal;
import java.util.Objects;
import zav.discord.blanc.api.Argument;
import zav.discord.blanc.command.AbstractCommand;

/**
 * This command can solve simple mathematical expressions.
 */
public class MathCommand extends AbstractCommand {
  @Argument(index = 0)
  @SuppressWarnings({"UnusedDeclaration"})
  private BigDecimal value;
  
  @Override
  public void postConstruct() throws Exception {
    Objects.requireNonNull(value, i18n.getString("invalid_expression"));
  }
  
  @Override
  public void run() {
    channel.sendMessage(value.toPlainString()).complete();
  }
}
