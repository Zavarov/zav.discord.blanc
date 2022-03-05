package zav.discord.blanc.command;

import javax.inject.Inject;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.PrivateChannel;
import org.eclipse.jdt.annotation.NonNullByDefault;
import zav.discord.blanc.api.Rank;

/**
 * Base class for all private commands.<br>
 */
@NonNullByDefault
public abstract class AbstractPrivateCommand extends AbstractCommand {
  @Inject
  protected PrivateChannel channel;
  @Inject
  protected Message message;
  
  protected AbstractPrivateCommand(Rank rank) {
    super(rank);
  }
  
  protected AbstractPrivateCommand() {
    super();
  }
}
