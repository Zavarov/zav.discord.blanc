package zav.discord.blanc.command.internal;

import com.google.inject.AbstractModule;
import zav.discord.blanc.view.*;

public class GuildCommandModule extends AbstractModule {
  private final GuildMessageView msg;
  public GuildCommandModule(GuildMessageView msg) {
    this.msg = msg;
  }
  
  @Override
  protected void configure() {
    // AbstractGuildCommand
    bind(MemberView.class).toInstance(msg.getAuthor());
    bind(TextChannelView.class).toInstance(msg.getMessageChannel());
    bind(GuildView.class).toInstance(msg.getGuild());
    bind(GuildMessageView.class).toInstance(msg);
  
    // AbstractCommand
    bind(ShardView.class).toInstance(msg.getShard());
    bind(UserView.class).toInstance(msg.getAuthor());
    bind(MessageChannelView.class).toInstance(msg.getMessageChannel());
    bind(MessageView.class).toInstance(msg);
  }
}
