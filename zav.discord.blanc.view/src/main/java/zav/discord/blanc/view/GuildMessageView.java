package zav.discord.blanc.view;

public interface GuildMessageView extends MessageView {
  GuildView getGuild();
  @Override
  TextChannelView getMessageChannel();
  MemberView getAuthor();
}
