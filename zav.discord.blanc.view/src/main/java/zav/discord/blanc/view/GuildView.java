package zav.discord.blanc.view;

public interface GuildView {
  MemberView getMember(long id);
  TextChannelView getTextChannel(long id);
  RoleView getRole(long id);
}
