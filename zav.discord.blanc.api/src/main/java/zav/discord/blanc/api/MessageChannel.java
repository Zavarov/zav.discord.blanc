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
import java.util.List;
import zav.discord.blanc.api.site.SiteListener;
import zav.discord.blanc.databind.GuildDto;
import zav.discord.blanc.databind.RoleDto;
import zav.discord.blanc.databind.UserDto;
import zav.discord.blanc.databind.message.SiteDto;
import zav.jrc.databind.LinkValueObject;

/**
 * Base interface for all functions that are performed in message channels.<br>
 * This includes both private and guild channels.
 */
public interface MessageChannel {
  
  Message getMessage(long messageId);
  
  default void send(String format, Object... args) {
    send(String.format(format, args));
  }
  
  void send(BufferedImage image, String imageName);
  
  void send(Object content);
  
  void send(GuildDto guild);
  
  void send(RoleDto role);
  
  void send(UserDto user);
  
  void send(LinkValueObject link);
  
  void send(SiteListener listener, List<SiteDto> sites);
}
