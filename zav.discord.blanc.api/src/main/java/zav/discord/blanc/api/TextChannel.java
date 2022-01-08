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

import zav.discord.blanc.databind.TextChannelDto;

/**
 * Base interface for all functions that are performed on text channels.
 */
public interface TextChannel extends MessageChannel {
  
  TextChannelDto getAbout();
  
  @Override
  GuildMessage getMessage(Argument argument);
  
  WebHook getWebHook(String argument, boolean create);
  
  default WebHook getWebHook(String argument) {
    return getWebHook(argument, false);
  }
  
  String getAsMention();
}
