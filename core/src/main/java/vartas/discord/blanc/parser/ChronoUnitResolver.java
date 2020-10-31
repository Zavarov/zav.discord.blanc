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

import javax.annotation.Nonnull;
import java.time.temporal.ChronoUnit;
import java.util.Locale;
/**
 * Resolves the provided {@link Argument} into an {@link ChronoUnit}.
 */
@Nonnull
public class ChronoUnitResolver extends TypeResolver<ChronoUnit>{
    /**
     * Attempts to get the interval matching the name.
     * @see ChronoUnit#valueOf(String)
     * @param argument the name of the interval.
     */
    @Override
    public void visit(@Nonnull StringArgument argument){
        try {
            this.type = ChronoUnit.valueOf(argument.getContent().toUpperCase(Locale.ENGLISH));
        }catch(IllegalArgumentException ignored){
            //Is thrown when no matching chrono unit exists.
        }
    }
}