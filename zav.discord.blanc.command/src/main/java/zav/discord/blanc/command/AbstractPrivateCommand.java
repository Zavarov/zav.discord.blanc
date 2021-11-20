package zav.discord.blanc.command;

import javax.inject.Inject;
import org.eclipse.jdt.annotation.Nullable;
import zav.discord.blanc.Rank;
import zav.discord.blanc.view.PrivateChannelView;
import zav.discord.blanc.view.PrivateMessageView;

/**
 * Base class for all private commands.<br>
 */
public abstract class AbstractPrivateCommand extends AbstractCommand {
  @Inject
  protected @Nullable PrivateChannelView channel;
  @Inject
  protected @Nullable PrivateMessageView message;
  
  public AbstractPrivateCommand(Rank rank) {
    super(rank);
  }
  
  public AbstractPrivateCommand() {
    this(Rank.USER);
  }
}
