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

package zav.discord.blanc.command.internal.resolver;

import java.util.function.BiFunction;
import net.dv8tion.jda.api.entities.Message;
import org.eclipse.jdt.annotation.NonNullByDefault;
import zav.discord.blanc.api.Parameter;

/**
 * Base class for all entity resolvers. The resolvers are used to deserialize a {@link Parameter}
 * into a Java object.
 *
 * @param <T> The target type.
 */
@NonNullByDefault
public interface EntityResolver<T> extends BiFunction<Parameter, Message, T> {
}
