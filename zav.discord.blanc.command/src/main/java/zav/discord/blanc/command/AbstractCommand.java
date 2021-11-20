package zav.discord.blanc.command;

import javax.inject.Inject;
import org.eclipse.jdt.annotation.Nullable;
import zav.discord.blanc.Rank;
import zav.discord.blanc.view.MessageChannelView;
import zav.discord.blanc.view.MessageView;
import zav.discord.blanc.view.ShardView;
import zav.discord.blanc.view.UserView;

/**
 * Abstract base class for all commands.<br>
 * Commands can be either executed in a guild or private channel.
 */
public abstract class AbstractCommand implements Command {
  @Inject @Nullable
  protected ShardView shard;
  @Inject @Nullable
  protected MessageChannelView channel;
  @Inject @Nullable
  protected UserView author;
  @Inject @Nullable
  protected MessageView message;
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
