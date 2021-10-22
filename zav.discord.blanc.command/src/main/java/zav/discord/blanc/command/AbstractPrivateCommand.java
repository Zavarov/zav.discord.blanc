package zav.discord.blanc.command;

import zav.discord.blanc.view.PrivateChannelView;
import zav.discord.blanc.view.ShardView;
import zav.discord.blanc.view.UserView;

import javax.inject.Inject;

public abstract class AbstractPrivateCommand implements Command {
  @Inject
  protected ShardView shard;
  @Inject
  protected PrivateChannelView channel;
  @Inject
  protected UserView author;
}
