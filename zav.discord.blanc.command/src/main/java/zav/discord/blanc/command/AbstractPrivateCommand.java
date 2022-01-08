package zav.discord.blanc.command;

import javax.inject.Inject;
import org.eclipse.jdt.annotation.Nullable;
import zav.discord.blanc.api.PrivateChannel;
import zav.discord.blanc.api.PrivateMessage;

/**
 * Base class for all private commands.<br>
 */
public abstract class AbstractPrivateCommand extends AbstractCommand {
  @Inject
  protected PrivateChannel channel;
  @Inject
  protected PrivateMessage message;
  
  public AbstractPrivateCommand(Rank rank) {
    super(rank);
  }
  
  public AbstractPrivateCommand() {
    this(Rank.USER);
  }
}
