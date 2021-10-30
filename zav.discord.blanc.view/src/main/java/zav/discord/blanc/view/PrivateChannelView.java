package zav.discord.blanc.view;

import zav.discord.blanc.Argument;

public interface PrivateChannelView extends MessageChannelView {
  // Views
  @Override
  PrivateMessageView getMessage(Argument argument);
}
