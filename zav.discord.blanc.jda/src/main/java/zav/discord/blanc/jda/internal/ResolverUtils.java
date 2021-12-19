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

import static zav.discord.blanc.jda.internal.ArgumentUtils.resolveById;
import static zav.discord.blanc.jda.internal.ArgumentUtils.resolveByName;

import java.util.NoSuchElementException;
import java.util.Optional;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import zav.discord.blanc.api.Argument;

/**
 * Utility class for resolving JDA entities from a number or string.
 */
public final class ResolverUtils {
  private static final Logger LOGGER = LogManager.getLogger(ResolverUtils.class);
  
  /**
   * Attempts to resolve the text channel corresponding to the provided argument.<br>
   * A channel can either be determined by its (unique) name or by its id. In case multiple channels
   * with the same name are found (including upper/lower cases), an exception is thrown.
   *
   * @param guild The guild containing the requested channel.
   * @param argument Either a channel name or id.
   * @return A text channel within the provided guild.
   * @throws NoSuchElementException If no text channel matching the provided argument exists in the
   *                                provided guild.
   */
  public static TextChannel resolveTextChannel(Guild guild, Argument argument) throws NoSuchElementException  {
    Optional<TextChannel> textChannel = resolveById(argument, guild::getTextChannelById);
  
    // A text channel with matching id was found
    if (textChannel.isPresent()) {
      return textChannel.get();
    }
  
    textChannel = resolveByName(argument, name -> guild.getTextChannelsByName(name, true));
  
    // A text channel with matching name was found
    if (textChannel.isPresent()) {
      return textChannel.get();
    }
  
    LOGGER.error("No matching text channel for {} has been found.", argument);
    throw new NoSuchElementException();
  }
  
  /**
   * Attempts to resolve the guild member corresponding to the provided argument.<br>
   * A member can either be determined by its (unique) name, nickname or by its id. In case multiple
   * members with the same name are found (including upper/lower cases), an exception is thrown.
   *
   * @param guild The guild containing the requested member.
   * @param argument Either a member name or id.
   * @return A member within the provided guild.
   * @throws NoSuchElementException If no member matching the provided argument exists in the
   *                                provided guild.
   */
  public static Member resolveMember(Guild guild, Argument argument) throws NoSuchElementException {
    Optional<Member> member = resolveById(argument, guild::getMemberById);
  
    // A member with matching id was found
    if (member.isPresent()) {
      return member.get();
    }
  
    member = resolveByName(argument, name -> guild.getMembersByName(name, true));
  
    // A member with matching name was found
    if (member.isPresent()) {
      return member.get();
    }
  
    member = resolveByName(argument, name -> guild.getMembersByNickname(name, true));
  
    // A member with matching nickname was found
    if (member.isPresent()) {
      return member.get();
    }
  
    LOGGER.error("No matching member for {} has been found.", argument);
    throw new NoSuchElementException();
  }
  
  /**
   * Attempts to resolve the role corresponding to the provided argument.<br>
   * A role can either be determined by its (unique) name or by its id. In case multiple roles with
   * the same name are found (including upper/lower cases), an exception is thrown.
   *
   * @param guild The guild containing the requested role.
   * @param argument Either a role name or id.
   * @return A role within the provided guild.
   * @throws NoSuchElementException If no role matching the provided argument exists in the provided
   *                                guild.
   */
  public static Role resolveRole(Guild guild, Argument argument) throws NoSuchElementException {
    Optional<Role> role = ArgumentUtils.resolveById(argument, guild::getRoleById);
  
    // A role with matching id was found
    if (role.isPresent()) {
      return role.get();
    }
  
    role = ArgumentUtils.resolveByName(argument, name -> guild.getRolesByName(name, true));
  
    // A unique role matching this name has been found
    if (role.isPresent()) {
      return role.get();
    }
  
    LOGGER.error("No matching role for {} has been found.", argument);
    throw new NoSuchElementException();
  }
  
  /**
   * Attempts to resolve the message corresponding to the provided argument.<br>
   * A message can only be identified by its id.
   *
   * @param messageChannel The channel containing the requested message.
   * @param argument A message id.
   * @return A message within the provided channel.
   * @throws NoSuchElementException If no message matching the provided argument exists in the
   *                                provided channel.
   */
  public static Message resolveMessage(MessageChannel messageChannel, Argument argument) throws NoSuchElementException {
    Optional<Message> jdaMessage = ArgumentUtils.resolveById(argument, id -> messageChannel.retrieveMessageById(id).complete());
  
    if (jdaMessage.isPresent()) {
      return jdaMessage.get();
    }
  
    LOGGER.error("No matching message for {} has been found.", argument);
    throw new NoSuchElementException();
  }
  
  /**
   * Attempts to resolve the user corresponding to the provided argument.<br>
   * A user can either be determined by its (unique) name or by its id. In case multiple user with
   * the same name are found (including upper/lower cases), an exception is thrown.
   *
   * @param jda The shard containing the requested user.
   * @param argument Either a user name or id.
   * @return A user within the provided shard.
   * @throws NoSuchElementException If no user matching the provided argument exists in the provided
   *                                shard.
   */
  public static User resolveUser(JDA jda, Argument argument) {
    Optional<User> jdaUser = ArgumentUtils.resolveById(argument, id -> jda.retrieveUserById(id).complete());
  
    // A user with matching id was found
    if (jdaUser.isPresent()) {
      return jdaUser.get();
    }
  
    jdaUser = ArgumentUtils.resolveByName(argument, name -> jda.getUsersByName(name, true));
  
    // A unique user matching this name has been found
    if (jdaUser.isPresent()) {
      return jdaUser.get();
    }
  
    LOGGER.error("No matching user for {} has been found.", argument);
    throw new NoSuchElementException();
  }
  
  /**
   * Attempts to resolve the guild corresponding to the provided argument.<br>
   * A guild can either be determined by its (unique) name or by its id. In case multiple guilds
   * with the same name are found (including upper/lower cases), an exception is thrown.
   *
   * @param jda The shard containing the requested guild.
   * @param argument Either a guild name or id.
   * @return A guild within the provided shard.
   * @throws NoSuchElementException If no guild matching the provided argument exists in the
   *                                provided shard.
   */
  public static Guild resolveGuild(JDA jda, Argument argument) {
    Optional<Guild> jdaGuild = ArgumentUtils.resolveById(argument, jda::getGuildById);
    
    // A guild with matching id was found
    if (jdaGuild.isPresent()) {
      return jdaGuild.get();
    }
    
    jdaGuild = ArgumentUtils.resolveByName(argument, name -> jda.getGuildsByName(name, true));
    
    // A unique guild matching this name has been found
    if (jdaGuild.isPresent()) {
      return jdaGuild.get();
    }
    
    LOGGER.error("No matching guild for {} has been found.", argument);
    throw new NoSuchElementException();
  }
}
