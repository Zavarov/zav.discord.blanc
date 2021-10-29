package zav.discord.blanc.view;

import zav.discord.blanc.Permission;
import zav.discord.blanc.databind.Role;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

public interface MemberView extends UserView {
  Set<Permission> getPermissions();
  Set<RoleView> getRoles();
  void modifyRoles(Collection<Role> rolesToAdd, Collection<Role> rolesToRemove);
  default void addRoles(Collection<Role> roles) {
    modifyRoles(roles, Collections.emptySet());
  }
  default void addRole(Role role) {
    addRoles(Collections.singleton(role));
  }
  default void removeRoles(Collection<Role> roles) {
    modifyRoles(Collections.emptySet(), roles);
  }
  default void removeRole(Role role) {
    removeRoles(Collections.singleton(role));
  }
}
