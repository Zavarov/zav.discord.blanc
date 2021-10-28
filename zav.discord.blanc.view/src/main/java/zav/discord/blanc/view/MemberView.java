package zav.discord.blanc.view;

import java.util.Collection;
import java.util.Collections;

public interface MemberView extends UserView {
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
