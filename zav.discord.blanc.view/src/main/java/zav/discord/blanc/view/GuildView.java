package zav.discord.blanc.view;

import zav.discord.blanc.Argument;
import zav.discord.blanc.databind.Guild;
import zav.discord.blanc.databind.Role;
import zav.discord.blanc.databind.TextChannel;

import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

public interface GuildView {
  // Databind
  Guild getAbout();
  // Views
  SelfMemberView getSelfMember();
  Collection<RoleView> getRoles();
  RoleView getRole(Argument argument);
  Collection<MemberView> getMembers();
  MemberView getMember(Argument argument);
  Collection<TextChannelView> getTextChannels();
  TextChannelView getTextChannel(Argument argument);
  // Misc
  void updateActivity();
  void updateBlacklist(Pattern pattern);
  void leave();
  boolean canInteract(MemberView member, Role role);
  BufferedImage getActivity(List<TextChannel> channels);
}
