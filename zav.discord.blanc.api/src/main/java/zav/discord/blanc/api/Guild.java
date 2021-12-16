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
import zav.discord.blanc.databind.GuildValueObject;
import zav.discord.blanc.databind.RoleValueObject;
import zav.discord.blanc.databind.TextChannelValueObject;

/**
 * Base interface for all functions that are performed over guild.
 */
public interface Guild {
  GuildValueObject getAbout();
  
  /**
   * Self members allow additional functionality exclusive to oneself, such as changing the profile
   * pic or updating ones nickname.
   *
   * @return A view over the member instance corresponding to this program.
   */
  SelfMember getSelfMember();
  
  Collection<Role> getRoles();
  
  /**
   * Roles may either be identified by their name or id.
   *
   * @param argument An argument representing the requested role.
   * @return The view over the specified role.
   * @throws NoSuchElementException If none or more than one role matching the argument was found.
   */
  Role getRole(Argument argument) throws NoSuchElementException;

  Collection<Member> getMembers();
  
  /**
   * Members may be identified by their user name, nickname or id.
   *
   * @param argument An argument representing the requested member.
   * @return The view over the specified member.
   * @throws NoSuchElementException If none or more than one member matching the argument was found.
   */
  Member getMember(Argument argument) throws NoSuchElementException;

  Collection<TextChannel> getTextChannels();
  
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
   * Creates a snapshot of the guild activity at the current time.<br>
   * The activity includes the numbers of messages per minute, the total number of members and the
   * number of members that are currently online.
   */
  void updateActivity();
  
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
  
  /**
   * Checks whether the given member is allowed to interact with the role.<br>
   * Relevant e.g. for checking whether a user can assign a specific role.
   *
   * @param member The view over member of this guild.
   * @param role A role of this guild.
   * @return {@code true}, when the member is allowed to assign this role to a user.
   */
  boolean canInteract(Member member, RoleValueObject role);
  
  /**
   * Creates an activity chart over this guild. The chart plots the total number of members, the
   * number of members that have been online at a given time as well as the number of messages per
   * minute.<br>
   * Additionally, the activity per minutes in {@code channels} is plotted as well.
   *
   * @param channels A list of text channels in this guild.
   * @return An line chart plotting the recent guild activity.
   */
  BufferedImage getActivity(List<TextChannelValueObject> channels);
}
