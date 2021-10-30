package zav.discord.blanc.view;

public interface PrivateChannelView extends MessageChannelView {
  @Override
  PrivateMessageView getMessage(long id);
}
