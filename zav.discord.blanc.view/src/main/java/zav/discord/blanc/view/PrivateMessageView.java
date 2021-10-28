package zav.discord.blanc.view;

public interface PrivateMessageView extends MessageView {
  @Override
  PrivateChannelView getMessageChannel();
  UserView getAuthor();
}
