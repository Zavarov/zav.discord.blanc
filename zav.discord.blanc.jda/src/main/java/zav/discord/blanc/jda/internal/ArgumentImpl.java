/*
 * Copyright (c) 2021 Zavarov.
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

package zav.discord.blanc.jda.internal;

import java.math.BigDecimal;
import java.util.Optional;
import zav.discord.blanc.api.Argument;
import zav.discord.blanc.command.parser.NumberArgument;
import zav.discord.blanc.databind.GuildValueObject;

/**
 * Utility class for creating {@link Argument} instances.
 */
public final class ArgumentImpl implements NumberArgument {
  private final long value;
  
  private ArgumentImpl(long value) {
    this.value = value;
  }
  
  public static ArgumentImpl of(GuildValueObject guild) {
    return new ArgumentImpl(guild.getId());
  }
  
  public static ArgumentImpl of(long guildId) {
    return new ArgumentImpl(guildId);
  }
  
  @Override
  public Optional<BigDecimal> asNumber() {
    return Optional.of(BigDecimal.valueOf(value));
  }
}
