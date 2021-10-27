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
    bind(MemberView.class).toInstance(msg.getAuthor());
    bind(TextChannelView.class).toInstance(msg.getMessageChannel());
    bind(GuildView.class).toInstance(msg.getGuild());
    bind(ShardView.class).toInstance(msg.getShard());
  }
}
