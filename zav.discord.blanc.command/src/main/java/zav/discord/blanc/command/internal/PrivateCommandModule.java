package zav.discord.blanc.command.internal;

import com.google.inject.AbstractModule;
import zav.discord.blanc.view.PrivateChannelView;
import zav.discord.blanc.view.PrivateMessageView;
import zav.discord.blanc.view.ShardView;
import zav.discord.blanc.view.UserView;

public class PrivateCommandModule extends AbstractModule {
  private final PrivateMessageView msg;
  public PrivateCommandModule(PrivateMessageView msg) {
    this.msg = msg;
  }
  
  @Override
  protected void configure() {
    bind(UserView.class).toInstance(msg.getAuthor());
    bind(PrivateChannelView.class).toInstance(msg.getMessageChannel());
    bind(ShardView.class).toInstance(msg.getShard());
  }
}