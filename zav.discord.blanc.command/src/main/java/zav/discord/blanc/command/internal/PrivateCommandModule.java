package zav.discord.blanc.command.internal;

import com.google.inject.AbstractModule;
import zav.discord.blanc.view.*;

public class PrivateCommandModule extends AbstractModule {
  private final PrivateMessageView msg;
  public PrivateCommandModule(PrivateMessageView msg) {
    this.msg = msg;
  }
  
  @Override
  protected void configure() {
    // AbstractPrivateCommand
    bind(PrivateChannelView.class).toInstance(msg.getMessageChannel());
    bind(PrivateMessageView.class).toInstance(msg);
  
    // AbstractCommand
    bind(ShardView.class).toInstance(msg.getShard());
    bind(UserView.class).toInstance(msg.getAuthor());
    bind(MessageChannelView.class).toInstance(msg.getMessageChannel());
    bind(MessageView.class).toInstance(msg);
  }
}
