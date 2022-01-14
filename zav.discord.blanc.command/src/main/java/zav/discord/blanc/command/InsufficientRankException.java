package zav.discord.blanc.command;

import org.apache.commons.lang3.StringUtils;
import zav.discord.blanc.api.Rank;

/**
 * This exception is thrown whenever a user executes a command for which they lack the required
 * rank.
 *
 * @see Rank
 */
public class InsufficientRankException extends Exception {
  public InsufficientRankException(Rank... ranks) {
    super(getMessage(ranks));
  }
  
  private static String getMessage(Rank... ranks) {
    return "You require the following rank(s) to execute this command: "
          + StringUtils.join(ranks, ",");
  }
}
