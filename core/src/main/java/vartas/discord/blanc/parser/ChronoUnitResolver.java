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

import vartas.discord.blanc.Errors;

import javax.annotation.Nonnull;
import java.time.temporal.ChronoUnit;
import java.util.Locale;
/**
 * Resolves the provided {@link Argument} into a {@link ChronoUnit}.
 */
@Nonnull
public class ChronoUnitResolver extends TypeResolver<ChronoUnit>{
    /**
     * Attempts to get the {@link ChronoUnit} with the same name.
     * The {@link IllegalArgumentException} caused by an invalid module is catched and logged.
     * @see ChronoUnit#valueOf(String)
     * @param argument The name of the interval.
     */
    @Override
    public void visit(@Nonnull StringArgument argument){
        try {
            this.type = ChronoUnit.valueOf(argument.getContent().toUpperCase(Locale.ENGLISH));
        }catch(IllegalArgumentException e){
            log.error(Errors.UNKNOWN_ENTITY.toString(), e);
        }
    }
}
