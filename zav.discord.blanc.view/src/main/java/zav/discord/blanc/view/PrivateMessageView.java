package zav.discord.blanc.view;

public interface PrivateMessageView extends MessageView {
  // Databind
  UserView getAuthor();
  // Views
  @Override
  PrivateChannelView getMessageChannel();
}
