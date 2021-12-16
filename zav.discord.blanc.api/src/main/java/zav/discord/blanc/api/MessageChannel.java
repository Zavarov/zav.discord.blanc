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

package zav.discord.blanc.api;

import java.awt.image.BufferedImage;
import zav.discord.blanc.databind.GuildValueObject;
import zav.discord.blanc.databind.RoleValueObject;
import zav.discord.blanc.databind.UserValueObject;
import zav.jrc.databind.Link;

/**
 * Base interface for all functions that are performed in message channels.<br>
 * This includes both private and guild channels.
 */
public interface MessageChannel {
  
  Message getMessage(Argument argument);
  
  default void send(String format, Object... args) {
    send(String.format(format, args));
  }
  
  void send(BufferedImage image, String imageName);
  
  void send(Object content);
  
  void send(GuildValueObject guild);
  
  void send(RoleValueObject role);
  
  void send(UserValueObject user);
  
  void send(Link link);
}
