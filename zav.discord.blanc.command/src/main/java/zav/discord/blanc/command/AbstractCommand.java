package zav.discord.blanc.command;

import java.util.Set;
import javax.inject.Inject;
import org.eclipse.jdt.annotation.Nullable;
import zav.discord.blanc.api.Message;
import zav.discord.blanc.api.MessageChannel;
import zav.discord.blanc.api.Shard;
import zav.discord.blanc.api.User;

/**
 * Abstract base class for all commands.<br>
 * Commands can be either executed in a guild or private channel.
 */
public abstract class AbstractCommand implements Command {
  @Inject
  protected Shard shard;
  @Inject
  protected MessageChannel channel;
  @Inject
  protected User author;
  @Inject
  protected Message message;
  
  private final Rank requiredRank;
  
  public AbstractCommand(Rank requiredRank) {
    this.requiredRank = requiredRank;
  }
  
  public AbstractCommand() {
    this(Rank.USER);
  }
  
  @Override
  public void validate() throws InvalidCommandException {
    Set<Rank> effectiveRanks = Rank.getEffectiveRank(author.getAbout().getRanks());
    
    // Does the user have the required rank?
    if (!effectiveRanks.contains(requiredRank)) {
      throw new InsufficientRankException(requiredRank);
    }
  }
}
