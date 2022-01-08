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

package zav.discord.blanc.api.site;

import java.util.function.Consumer;
import zav.discord.blanc.databind.message.PageDto;

/**
 * Interface for message components. Every command implementing this interface will be notified
 * whenever an user interacts with the page that is displayed.
 *
 * @see <a href="https://discord.com/developers/docs/interactions/message-components">here</a>
 */
public interface SiteListener {
  boolean canMoveLeft();
  
  void moveLeft(Consumer<PageDto> consumer);
  
  boolean canMoveRight();
  
  void moveRight(Consumer<PageDto> consumer);
  
  void changeSelection(String label, Consumer<PageDto> consumer);
}
