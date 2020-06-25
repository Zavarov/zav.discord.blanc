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

import vartas.discord.blanc.Guild;
import vartas.discord.blanc.TextChannel;
import vartas.discord.blanc.parser.visitor.ParserVisitor;

import javax.annotation.Nonnull;
import java.util.Optional;
import java.util.function.BiFunction;

/**
 * This interface is used for all data types that exist within the scope of a {@link Guild}
 * @param <T> the resolved type.
 */
@Nonnull
public interface GuildTypeResolver  <T> extends ParserVisitor {
    Optional<T> apply(Guild guild, TextChannel textChannel, Argument argument);
}
