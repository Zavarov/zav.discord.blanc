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

package zav.discord.blanc.command;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jdt.annotation.NonNullByDefault;
import zav.discord.blanc.api.util.ValidationException;

/**
 * This exception is thrown whenever a user executes a guild command for which they lack the
 * required permission.
 *
 * @see Permission
 */
@NonNullByDefault
public class InsufficientPermissionException extends ValidationException {
  private static final long serialVersionUID = -7228912003018402683L;
  private final List<Permission> permissions;
  
  public InsufficientPermissionException(Collection<Permission> permissions) {
    this.permissions = List.copyOf(permissions);
  }
  
  @Override
  public MessageEmbed getErrorMessage() {
    return new EmbedBuilder()
        .setTitle(getTitle())
        .setDescription(getDescription())
        .build();
  }
  
  private String getTitle() {
    return "Insufficient Permissions";
  }
  
  private String getDescription() {
    return "You require the all of the following permission(s) to execute this command: "
          + StringUtils.join(getPermissions(), ",");
  }
  
  private List<String> getPermissions() {
    return permissions.stream()
        .map(permission -> permission.getName())
        .collect(Collectors.toUnmodifiableList());
  }
}
