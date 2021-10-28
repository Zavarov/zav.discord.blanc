package zav.discord.blanc.view;

import zav.discord.blanc.databind.Guild;

import java.util.Collection;

public interface GuildView {
  Guild getAbout();
  MemberView getMember(long id);
  MemberView getSelfMember();
  TextChannelView getTextChannel(long id);
  Collection<RoleView> getRoles();
  RoleView getRole(long id);
}
