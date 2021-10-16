package zav.discord.blanc.command;

public class InvalidCommandException extends Exception {
  private InvalidCommandException(Exception cause) {
    super(cause);
  }
  
  public static InvalidCommandException wrap(Exception cause) {
    return new InvalidCommandException(cause);
  }
}
