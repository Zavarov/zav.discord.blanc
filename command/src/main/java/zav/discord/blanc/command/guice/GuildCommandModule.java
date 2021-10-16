package zav.discord.blanc.command.guice;

import com.google.inject.AbstractModule;
import zav.discord.blanc.view.GuildMessageView;
import zav.discord.blanc.view.GuildView;
import zav.discord.blanc.view.MemberView;
import zav.discord.blanc.view.TextChannelView;

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
  }
}
