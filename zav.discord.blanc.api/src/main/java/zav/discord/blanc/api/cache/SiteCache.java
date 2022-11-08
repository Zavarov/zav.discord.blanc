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

import net.dv8tion.jda.api.entities.Message;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.jetbrains.annotations.Contract;
import zav.discord.blanc.api.Site;

/**
 * A cache for keeping track of all interactive messages. The user can use buttons and menus to
 * change the type of information that is displayed by the message.<br>
 * Those messages are not persisted.
 */
@NonNullByDefault
public class SiteCache extends AbstractCache<Message, Site.Group> {
  
  /**
   * Caches a new site.
   *
   * @param message The message corresponding to the site.
   * @param group A collection of sites.
   */
  @Contract(mutates = "this")
  public void put(Message message, Site.Group group) {
    cache.put(message, group);
  }

  @Override
  protected Site.Group fetch(Message key) {
    return null;
  }
}
