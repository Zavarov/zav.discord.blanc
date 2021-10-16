package zav.discord.blanc.command;

import zav.discord.blanc.view.GuildView;
import zav.discord.blanc.view.MemberView;
import zav.discord.blanc.view.TextChannelView;

import javax.inject.Inject;

public abstract class AbstractGuildCommand implements Command {
  @Inject
  protected GuildView guild;
  @Inject
  protected TextChannelView channel;
  @Inject
  protected MemberView author;
}
