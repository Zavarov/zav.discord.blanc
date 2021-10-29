package zav.discord.blanc.command;

import zav.discord.blanc.Rank;
import zav.discord.blanc.view.PrivateChannelView;
import zav.discord.blanc.view.PrivateMessageView;

import javax.inject.Inject;

public abstract class AbstractPrivateCommand extends AbstractCommand {
  @Inject
  protected PrivateChannelView channel;
  @Inject
  protected PrivateMessageView message;
  
  public AbstractPrivateCommand(Rank rank) {
    super(rank);
  }
  
  public AbstractPrivateCommand() {
    this(Rank.USER);
  }
}
