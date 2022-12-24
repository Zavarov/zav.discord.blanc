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

import net.dv8tion.jda.api.entities.Guild;
import zav.discord.blanc.api.util.RegularExpressionMatcher;
import zav.discord.blanc.databind.GuildEntity;

/**
 * A cache built upon the JPA persistence layer. For each guild, it stores a regular expression
 * including all registered entries. This means that the expensive task of parsing this expression
 * only has to be done once. The entry has to be invalidated, whenever one of the entries is
 * modified.
 */
public class AutoResponseCache extends AbstractCache<Guild, RegularExpressionMatcher> {
  
  @Override
  protected RegularExpressionMatcher fetch(Guild guild) {
    GuildEntity entity = GuildEntity.find(guild);

    if (entity.getAutoResponses().isEmpty()) {
      return null;
    }

    return new RegularExpressionMatcher(entity.getAutoResponses());
  }
}
