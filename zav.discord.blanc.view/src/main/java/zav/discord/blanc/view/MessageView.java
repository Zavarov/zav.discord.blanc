package zav.discord.blanc.view;

import zav.discord.blanc.databind.MessageValueObject;

/**
 * Base interface for all functions that are performed over a messages.<br>
 * This includes both private and guild messages.
 */
public interface MessageView {
  
  MessageValueObject getAbout();

  MessageChannelView getMessageChannel();

  UserView getAuthor();

  ShardView getShard();

  void delete();

  void react(String reaction);
}
