package zav.discord.blanc.view;

/**
 * Base interface for all functions that are performed on this application.<br>
 * The bot has to be inside a guild.
 */
public interface SelfMemberView extends MemberView, SelfUserView {

  void setNickname(String nickname);
}
