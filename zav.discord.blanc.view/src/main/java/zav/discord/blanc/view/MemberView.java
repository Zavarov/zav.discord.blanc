package zav.discord.blanc.view;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import zav.discord.blanc.Permission;
import zav.discord.blanc.databind.Role;

/**
 * Base interface for all functions that are performed over a guild member.
 */
public interface MemberView extends UserView {
  
  Set<RoleView> getRoles();
  
  Set<Permission> getPermissions();
  
  void modifyRoles(Collection<Role> rolesToAdd, Collection<Role> rolesToRemove);
  
  default void removeRoles(Collection<Role> roles) {
    modifyRoles(Collections.emptySet(), roles);
  }
  
  default void removeRole(Role role) {
    removeRoles(Collections.singleton(role));
  }
}
