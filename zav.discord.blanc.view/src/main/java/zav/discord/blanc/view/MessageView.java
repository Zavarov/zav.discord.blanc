package zav.discord.blanc.view;

import zav.discord.blanc.databind.Message;

public interface MessageView {
  // Databind
  Message getAbout();
  // Views
  MessageChannelView getMessageChannel();
  UserView getAuthor();
  ShardView getShard();
  // Misc
  void delete();
  void react(String reaction);
}
