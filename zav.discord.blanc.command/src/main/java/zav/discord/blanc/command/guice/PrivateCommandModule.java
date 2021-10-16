package zav.discord.blanc.command.guice;

import com.google.inject.AbstractModule;
import zav.discord.blanc.view.PrivateChannelView;
import zav.discord.blanc.view.PrivateMessageView;
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
  }
}
