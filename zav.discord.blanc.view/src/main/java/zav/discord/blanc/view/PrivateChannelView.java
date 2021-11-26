package zav.discord.blanc.view;

import zav.discord.blanc.Argument;

/**
 * Base interface for all functions that are performed over a private channel.
 */
public interface PrivateChannelView extends MessageChannelView {
  @Override
  PrivateMessageView getMessage(Argument argument);
}
