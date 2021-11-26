package zav.discord.blanc.view;

/**
 * Base interface for all functions that are performed over a private message.
 */
public interface PrivateMessageView extends MessageView {

  UserView getAuthor();

  @Override
  PrivateChannelView getMessageChannel();
}
