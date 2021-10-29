package zav.discord.blanc.view;

import zav.discord.blanc.databind.Guild;
import zav.discord.blanc.databind.Role;
import zav.discord.blanc.databind.TextChannel;

import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

public interface GuildView {
  Guild getAbout();
  void updateActivity();
  void updateBlacklist(Pattern pattern);
  void leave();
  boolean canInteract(MemberView member, Role role);
  BufferedImage getActivity(List<TextChannel> channels);
  MemberView getMember(long id);
  SelfMemberView getSelfMember();
  TextChannelView getTextChannel(long id);
  Collection<RoleView> getRoles();
  RoleView getRole(long id);
}
