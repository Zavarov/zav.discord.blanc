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

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import zav.discord.blanc.databind.RoleDto;

/**
 * Base interface for all functions that are performed over a guild member.
 */
public interface Member extends User {
  
  Set<? extends Role> getRoles();
  
  Set<Permission> getPermissions();
  
  void modifyRoles(Collection<RoleDto> rolesToAdd, Collection<RoleDto> rolesToRemove);
  
  default void removeRoles(Collection<RoleDto> roles) {
    modifyRoles(Collections.emptySet(), roles);
  }
  
  default void removeRole(RoleDto role) {
    removeRoles(Collections.singleton(role));
  }
}
