package zav.discord.blanc.command;

import javax.inject.Inject;

import zav.discord.blanc.Rank;
import zav.discord.blanc.view.MessageChannelView;
import zav.discord.blanc.view.MessageView;
import zav.discord.blanc.view.ShardView;
import zav.discord.blanc.view.UserView;

public abstract class AbstractCommand implements Command {
  @Inject
  protected ShardView shard;
  @Inject
  protected MessageChannelView channel;
  @Inject
  protected UserView author;
  @Inject
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
    // Does the user have the required rank?
    if (!author.getAbout().getRanks().contains(rank.name())) {
      throw new InsufficientRankException();
    }
  }
}
