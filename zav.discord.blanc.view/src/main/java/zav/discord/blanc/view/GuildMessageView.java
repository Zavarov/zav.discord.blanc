package zav.discord.blanc.view;

public interface GuildMessageView extends MessageView {
  // Views
  GuildView getGuild();
  @Override
  TextChannelView getMessageChannel();
  @Override
  MemberView getAuthor();
}
