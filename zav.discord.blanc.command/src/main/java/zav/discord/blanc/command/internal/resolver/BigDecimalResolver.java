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

import java.math.BigDecimal;
import net.dv8tion.jda.api.entities.Message;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import zav.discord.blanc.api.Parameter;

/**
 * Extracts the numerical value of a {@link Parameter}.
 */
@NonNullByDefault
public class BigDecimalResolver implements EntityResolver<BigDecimal> {
  @Override
  public @Nullable BigDecimal apply(Parameter parameter, Message message) {
    return parameter.asNumber().orElse(null);
  }
}
