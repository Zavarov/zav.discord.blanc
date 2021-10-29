package zav.discord.blanc.view;

import zav.discord.blanc.databind.Message;

public interface MessageView {
  MessageChannelView getMessageChannel();
  ShardView getShard();
  Message getAbout();
  void delete();
}
