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

package zav.discord.blanc.command.internal.resolver;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zav.discord.blanc.api.Parameter;
import zav.discord.blanc.command.internal.ArgumentUtils;

/**
 * Derives a member based on its (unique) id name or nickname, in this order. In case multiple
 * members with the same (nick-)name exist, none are selected.
 */
@NonNullByDefault
public final class MemberResolver implements EntityResolver<Member> {
  private static final Logger LOGGER = LoggerFactory.getLogger(MemberResolver.class);
  
  /**
   * Attempts to resolve the guild member corresponding to the provided argument.<br>
   * A member can either be determined by its (unique) name, nickname or by its id. In case multiple
   * members with the same name are found (including upper/lower cases), an exception is thrown.
   *
   * @param message The received guild message.
   * @param parameter Either a member name or id.
   * @return A member within the current guild.
   */
  @Override
  public @Nullable Member apply(Parameter parameter, Message message) {
    Guild guild = message.getGuild();
    Member member = ArgumentUtils.resolveById(parameter, guild::getMemberById);
  
    // A member with matching id was found
    if (member != null) {
      return member;
    }
  
    member = ArgumentUtils.resolveByName(parameter, name -> guild.getMembersByName(name, true));
  
    // A member with matching name was found
    if (member != null) {
      return member;
    }
  
    member = ArgumentUtils.resolveByName(parameter, name -> guild.getMembersByNickname(name, true));
  
    // A member with matching nickname was found
    if (member != null) {
      return member;
    }
  
    LOGGER.error("No matching member for {} has been found.", parameter);
    return null;
  }
}
