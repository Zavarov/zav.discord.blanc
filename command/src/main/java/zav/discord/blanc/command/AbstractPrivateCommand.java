package zav.discord.blanc.command;

import zav.discord.blanc.view.PrivateChannelView;
import zav.discord.blanc.view.UserView;

import javax.inject.Inject;

public abstract class AbstractPrivateCommand implements Command {
  @Inject
  protected PrivateChannelView channel;
  @Inject
  protected UserView author;
}
