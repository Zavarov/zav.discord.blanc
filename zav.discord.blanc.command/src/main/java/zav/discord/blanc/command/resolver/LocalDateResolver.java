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

package zav.discord.blanc.command.resolver;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import org.apache.commons.lang3.StringUtils;
import zav.discord.blanc.Argument;
import zav.discord.blanc.command.parser.StringArgument;

/**
 * Resolves the provided {@link Argument} into a {@link LocalDate}.
 */
public class LocalDateResolver extends TypeResolver<LocalDate> {
  /**
   * Parses the value contained in the provided {@link StringArgument}.<br>
   * The {@link DateTimeParseException} caused by a malformed String is caught and logged.
   *
   * @see DateTimeFormatter#ISO_LOCAL_DATE
   * @see LocalDate#parse(CharSequence)
   * @param argument the {@link Argument} associated with the {@link LocalDate}.
   */
  @Override
  public LocalDate apply(Argument argument) {
    try {
      return argument.asString()
            .map(StringUtils::deleteWhitespace)
            .map(LocalDate::parse)
            .orElseThrow(IllegalArgumentException::new);
    } catch (DateTimeParseException e) {
      throw new IllegalArgumentException(e);
    }
  }
}
