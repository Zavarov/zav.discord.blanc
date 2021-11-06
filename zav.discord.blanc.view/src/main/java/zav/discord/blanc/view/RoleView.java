package zav.discord.blanc.view;

import zav.discord.blanc.databind.Role;

public interface RoleView {
  // Databind
  Role getAbout();
  // Misc
  boolean canInteract(MemberView member, Role role);
}
