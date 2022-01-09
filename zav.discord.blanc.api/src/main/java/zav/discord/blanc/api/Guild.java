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
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.regex.Pattern;
import zav.discord.blanc.databind.GuildDto;
import zav.discord.blanc.databind.RoleDto;
import zav.discord.blanc.databind.TextChannelDto;

/**
 * Base interface for all functions that are performed over guild.
 */
public interface Guild {
  GuildDto getAbout();
  
  /**
   * Self members allow additional functionality exclusive to oneself, such as changing the profile
   * pic or updating ones nickname.
   *
   * @return A view over the member instance corresponding to this program.
   */
  SelfMember getSelfMember();
  
  Collection<? extends Role> getRoles();
  
  /**
   * Roles may either be identified by their name or id.
   *
   * @param argument An argument representing the requested role.
   * @return The view over the specified role.
   * @throws NoSuchElementException If none or more than one role matching the argument was found.
   */
  Role getRole(Argument argument) throws NoSuchElementException;

  Collection<? extends Member> getMembers();
  
  /**
   * Members may be identified by their user name, nickname or id.
   *
   * @param argument An argument representing the requested member.
   * @return The view over the specified member.
   * @throws NoSuchElementException If none or more than one member matching the argument was found.
   */
  Member getMember(Argument argument) throws NoSuchElementException;

  Collection<? extends TextChannel> getTextChannels();
  
  /**
   * Text channels may either be identified by their name or id.
   *
   * @param argument An argument representing the requested text channel.
   * @return The view over the specified text channel.
   * @throws NoSuchElementException If none or more than one text channel matching the argument was
   *                                found.
   */
  TextChannel getTextChannel(Argument argument);
  
  /**
   * Updates all expressions that can't be used in this guild.<br>
   * Every message that contains at least one forbidden expression is automatically deleted.
   *
   * @param pattern The new pattern matching all forbidden expressions.
   */
  void updateBlacklist(Pattern pattern);
  
  /**
   * Forces the bot to leave this guild.
   */
  void leave();
}
