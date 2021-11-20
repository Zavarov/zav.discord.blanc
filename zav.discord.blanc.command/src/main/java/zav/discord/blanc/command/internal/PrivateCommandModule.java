package zav.discord.blanc.command.internal;

import com.google.inject.AbstractModule;
import zav.discord.blanc.view.MessageChannelView;
import zav.discord.blanc.view.MessageView;
import zav.discord.blanc.view.PrivateChannelView;
import zav.discord.blanc.view.PrivateMessageView;
import zav.discord.blanc.view.ShardView;
import zav.discord.blanc.view.UserView;

/**
 * Injector module for all guild commands.<br>
 * It prepares the following classes for injection:
 * <pre>
 *   - UserView
 *   - PrivateChannelView
 *   - MessageChannelView
 *   - PrivateMessageView
 *   - MessageView
 *   - ShardView
 * </pre>
 */
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
