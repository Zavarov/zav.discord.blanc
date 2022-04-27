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

import java.math.BigDecimal;
import java.util.Optional;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.jetbrains.annotations.Contract;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zav.discord.blanc.api.Parameter;

/**
 * This interface is for arguments that only have a string representation.<br>
 * It implements {@link Parameter#asNumber()} using {@link BigDecimal#BigDecimal(String)}. If the
 * string can't be converted into a number, {@link Optional#empty()} is returned.
 */
@NonNullByDefault
public interface StringParameter extends Parameter {
  Logger LOGGER = LoggerFactory.getLogger(StringParameter.class);
  @Override
  @Contract(pure = true)
  default Optional<BigDecimal> asNumber() {
    try {
      return asString().map(BigDecimal::new);
    } catch (NumberFormatException e) {
      LOGGER.debug(e.getMessage(), e);
      return Optional.empty();
    }
  }
}
