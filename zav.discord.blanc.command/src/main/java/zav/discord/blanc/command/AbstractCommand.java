package zav.discord.blanc.command;

import java.sql.SQLException;
import java.util.List;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Named;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.Contract;
import zav.discord.blanc.api.Parameter;
import zav.discord.blanc.api.Rank;
import zav.discord.blanc.api.command.Command;
import zav.discord.blanc.db.UserDatabase;

/**
 * Abstract base class for all commands.<br>
 * Commands can be either executed in a guild or private channel.
 */
public abstract class AbstractCommand implements Command {
  @Inject
  protected JDA shard;
  @Inject
  protected MessageChannel channel;
  @Inject
  protected User author;
  @Inject
  protected Message message;
  @Inject
  @Named("params")
  protected List<? extends Parameter> params;
  
  private final Rank requiredRank;
  
  public AbstractCommand(Rank requiredRank) {
    this.requiredRank = requiredRank;
  }
  
  public AbstractCommand() {
    this(Rank.USER);
  }
  
  @Override
  @Contract(pure = true)
  public void validate() throws Exception {
    Set<Rank> effectiveRanks;
    
    try {
      effectiveRanks = Rank.getEffectiveRanks(UserDatabase.get(author.getIdLong()).getRanks());
    } catch (SQLException e) {
      effectiveRanks = Set.of(Rank.USER);
    }
    
    // Does the user have the required rank?
    if (!effectiveRanks.contains(requiredRank)) {
      throw new InsufficientRankException(requiredRank);
    }
  }
}
