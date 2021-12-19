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

package zav.discord.blanc.jda.api;

import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import zav.discord.blanc.api.Permission;
import zav.discord.blanc.databind.RoleValueObject;
import zav.discord.blanc.jda.internal.GuiceUtils;

/**
 * Implementation of a member view, backed by JDA.
 */
public class JdaMember extends JdaUser implements zav.discord.blanc.api.Member {
  @Inject
  protected Member jdaMember;
  
  @Override
  public Set<JdaRole> getRoles() {
    return jdaMember.getRoles()
          .stream()
          .map(GuiceUtils::injectRole)
          .collect(Collectors.toUnmodifiableSet());
  }
  
  @Override
  public Set<Permission> getPermissions() {
    return jdaMember.getPermissions()
          .stream()
          .map(net.dv8tion.jda.api.Permission::getOffset)
          .map(Permission::getPermission)
          .collect(Collectors.toUnmodifiableSet());
  }
  
  @Override
  public void modifyRoles(Collection<RoleValueObject> rolesToAdd, Collection<RoleValueObject> rolesToRemove) {
    Guild jdaGuild = jdaMember.getGuild();
    
    Set<Role> jdaRolesToAdd = rolesToAdd.stream()
          .map(RoleValueObject::getId)
          .map(jdaGuild::getRoleById)
          .map(Objects::requireNonNull)
          .collect(Collectors.toUnmodifiableSet());
    
    Set<Role> jdaRolesToRemove = rolesToRemove.stream()
          .map(RoleValueObject::getId)
          .map(jdaGuild::getRoleById)
          .map(Objects::requireNonNull)
          .collect(Collectors.toUnmodifiableSet());
    
    jdaGuild.modifyMemberRoles(jdaMember, jdaRolesToAdd, jdaRolesToRemove).complete();
  }
}
