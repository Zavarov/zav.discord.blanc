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
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zav.discord.blanc.api.Parameter;
import zav.discord.blanc.command.internal.ArgumentUtils;

/**
 * Derives a role based on its (unique) id or name, in this order. In case multiple roles with the
 * same name exist, none are selected.
 */
@NonNullByDefault
public class RoleResolver implements EntityResolver<Role> {
  private static final Logger LOGGER = LoggerFactory.getLogger(RoleResolver.class);
  
  /**
   * Attempts to resolve the role corresponding to the provided argument.<br>
   * A role can either be determined by its (unique) name or by its id. In case multiple roles with
   * the same name are found (including upper/lower cases), an exception is thrown.
   *
   * @param message The received guild message.
   * @param parameter Either a role name or id.
   * @return A role within the current guild.
   */
  @Override
  public @Nullable Role apply(Parameter parameter, Message message) {
    Guild guild = message.getGuild();
    Role role = ArgumentUtils.resolveById(parameter, guild::getRoleById);
  
    // A role with matching id was found
    if (role != null) {
      return role;
    }
  
    role = ArgumentUtils.resolveByName(parameter, name -> guild.getRolesByName(name, true));
  
    // A unique role matching this name has been found
    if (role != null) {
      return role;
    }
  
    LOGGER.error("No matching role for {} has been found.", parameter);
    return null;
  }
}
