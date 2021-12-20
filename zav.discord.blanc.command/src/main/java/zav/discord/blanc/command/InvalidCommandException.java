package zav.discord.blanc.command;

/**
 * Base class for all command-related exception.<br>
 */
public abstract class InvalidCommandException extends Exception {
  public InvalidCommandException() {
    super();
  }
  
  public InvalidCommandException(String message) {
    super(message);
  }
}
