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

import org.eclipse.jdt.annotation.NonNull;
import zav.discord.blanc.command.parser.Argument;

import java.util.Optional;
import java.util.function.Function;

/**
 * This class is used to resolve an arbitrary data type.
 * @param <T> The resolved type.
 */
@NonNull
public abstract class TypeResolver <T> implements Function<Argument, T> {

    /**
     * Transforms the {@link Argument} into an instance of {@link T}
     * @param argument The {@link Argument} associated with {@link T}.
     * @return An {@link Optional} containing the resolved type. In case the argument couldn't be resolved,
     *         {@link Optional#empty()} is returned.
     */
    @Override
    public abstract T apply(Argument argument);
}
