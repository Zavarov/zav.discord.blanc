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

package vartas.discord.blanc.parser;

import org.apache.commons.lang3.StringUtils;
import vartas.discord.blanc.Errors;

import javax.annotation.Nonnull;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Resolves the provided {@link Argument} into a {@link LocalDate}.
 */
@Nonnull
public class LocalDateResolver extends TypeResolver<LocalDate>{
    /**
     * Parses the value contained in the provided {@link StringArgument}.
     * <p>
     * The {@link DateTimeParseException} caused by a malformed String is catched and logged.
     * @see DateTimeFormatter#ISO_LOCAL_DATE
     * @see LocalDate#parse(CharSequence)
     * @param argument the {@link Argument} associated with the {@link LocalDate}.
     */
    @Override
    public void visit(@Nonnull StringArgument argument){
        try {
            this.type = LocalDate.parse(StringUtils.deleteWhitespace(argument.getContent()));
        } catch(DateTimeParseException e) {
            log.error(Errors.UNKNOWN_ENTITY.toString(), e);
        }
    }
}
