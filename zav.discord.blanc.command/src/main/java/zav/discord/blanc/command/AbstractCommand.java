package zav.discord.blanc.command;

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
  @Inject @Nullable
  protected Shard shard;
  @Inject @Nullable
  protected MessageChannel channel;
  @Inject @Nullable
  protected User author;
  @Inject @Nullable
  protected Message message;
  // Package-private
  final Rank rank;
  
  public AbstractCommand(Rank rank) {
    this.rank = rank;
  }
  
  public AbstractCommand() {
    this(Rank.USER);
  }
  
  @Override
  public void validate() throws InvalidCommandException {
    assert author != null;
    
    // Does the user have the required rank?
    if (!author.getAbout().getRanks().contains(rank.name())) {
      throw new InsufficientRankException();
    }
  }
}
