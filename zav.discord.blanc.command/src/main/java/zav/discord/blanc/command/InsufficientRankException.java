package zav.discord.blanc.command;

/**
 * This exception is thrown whenever a user executes a command for which they lack the required
 * rank.
 *
 * @see Rank
 */
public class InsufficientRankException extends InvalidCommandException {
}
