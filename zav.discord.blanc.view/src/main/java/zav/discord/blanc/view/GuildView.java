package zav.discord.blanc.view;

import zav.discord.blanc.Argument;
import zav.discord.blanc.databind.Guild;
import zav.discord.blanc.databind.TextChannel;

import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

public interface GuildView {
  // Databind
  Guild getAbout();
  // Views
  Collection<RoleView> getRoles();
  SelfMemberView getSelfMember();
  MemberView getMember(Argument argument);
  TextChannelView getTextChannel(Argument argument);
  RoleView getRole(Argument argument);
  // Misc
  void updateActivity();
  void updateBlacklist(Pattern pattern);
  void leave();
  BufferedImage getActivity(List<TextChannel> channels);
}
