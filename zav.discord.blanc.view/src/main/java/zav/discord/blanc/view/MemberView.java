package zav.discord.blanc.view;

import zav.discord.blanc.Permission;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

public interface MemberView extends UserView {
  Set<Permission> getPermissions();
  Set<RoleView> getRoles();
  void modifyRoles(Collection<RoleView> rolesToAdd, Collection<RoleView> rolesToRemove);
  default void addRoles(Collection<RoleView> roles) {
    modifyRoles(roles, Collections.emptySet());
  }
  default void addRole(RoleView role) {
    addRoles(Collections.singleton(role));
  }
  default void removeRoles(Collection<RoleView> roles) {
    modifyRoles(Collections.emptySet(), roles);
  }
  default void removeRole(RoleView role) {
    removeRoles(Collections.singleton(role));
  }
}
