package zav.discord.blanc.command;

import zav.discord.blanc.view.PrivateChannelView;

import javax.inject.Inject;

public abstract class AbstractPrivateCommand extends AbstractCommand {
  @Inject
  protected PrivateChannelView channel;
}
