package zav.discord.blanc.view;

public interface PrivateMessageView extends MessageView {
  PrivateChannelView getMessageChannel();
  UserView getAuthor();
}
