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

package zav.discord.blanc.api.cache;

import java.util.regex.Pattern;
import net.dv8tion.jda.api.entities.Guild;
import org.eclipse.jdt.annotation.NonNullByDefault;
import zav.discord.blanc.databind.GuildEntity;

/**
 * A cache built on top of the JPA persistence layer storing all blacklisted guild patterns.<br>
 * Since the pattern is applied to every guild message, database access should be avoided as much as
 * possible. Messages matching the pattern are deleted automatically.
 */
@NonNullByDefault
public class PatternCache extends AbstractCache<Guild, Pattern> {
  
  @Override
  protected Pattern fetch(Guild guild) {
    return GuildEntity.find(guild).getPattern().orElse(null);
  }
}
