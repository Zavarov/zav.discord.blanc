package zav.discord.blanc.view;

import java.util.Collection;

public interface GuildView {
  MemberView getMember(long id);
  MemberView getSelfMember();
  TextChannelView getTextChannel(long id);
  Collection<RoleView> getRoles();
  RoleView getRole(long id);
}
