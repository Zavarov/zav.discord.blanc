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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vartas.discord.blanc.parser.visitor.ParserVisitor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.LocalDate;
import java.util.Optional;
import java.util.function.Function;

/**
 * This class is used to resolve an arbitrary data type.
 * @param <T> the resolved type.
 */
@Nonnull
public abstract class TypeResolver <T> implements Function<Argument, Optional<T>>, ParserVisitor {
    /**
     * This class's {@link Logger}.
     */
    @Nonnull
    protected final Logger log = LoggerFactory.getLogger(getClass().getSimpleName());
    /**
     * The resolved {@link LocalDate}
     */
    @Nullable
    protected T type;

    /**
     * Transforms the {@link Argument} into an instance of {@link T}
     * @param argument the {@link Argument} associated with {@link T}.
     * @return the resolved {@link T} associated with the {@link Argument}.
     */
    @Override
    public Optional<T> apply(Argument argument) {
        type = null;
        argument.accept(this);
        return Optional.ofNullable(type);
    }
}
