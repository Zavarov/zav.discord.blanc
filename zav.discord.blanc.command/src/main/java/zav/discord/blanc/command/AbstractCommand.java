package zav.discord.blanc.command;

import java.sql.SQLException;
import java.util.List;
import java.util.Set;
import javax.inject.Inject;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import org.apache.commons.lang3.Validate;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.jetbrains.annotations.Contract;
import zav.discord.blanc.api.Command;
import zav.discord.blanc.api.Rank;
import zav.discord.blanc.databind.UserEntity;
import zav.discord.blanc.db.UserDatabaseTable;

/**
 * Abstract base class for all commands.<br>
 * Commands can be either executed in a guild or private channel.
 */
@NonNullByDefault
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
  private UserDatabaseTable db;
  
  private final Rank requiredRank;
  
  protected AbstractCommand(Rank requiredRank) {
    this.requiredRank = requiredRank;
  }
  
  protected AbstractCommand() {
    this(Rank.USER);
  }
  
  @Override
  @Contract(pure = true)
  public void validate() throws Exception {
    Set<Rank> effectiveRanks;
    
    try {
      List<UserEntity> responses = db.get(author.getIdLong());
      
      Validate.validState(responses.size() <= 1);
      
      if (responses.size() == 0) {
        effectiveRanks = Set.of(Rank.USER);
      } else {
        effectiveRanks = Rank.getEffectiveRanks(responses.get(0).getRanks());
      }
    } catch (SQLException e) {
      effectiveRanks = Set.of(Rank.USER);
    }
    
    // Does the user have the required rank?
    if (!effectiveRanks.contains(requiredRank)) {
      throw new InsufficientRankException(requiredRank);
    }
  }
}
