package zav.discord.blanc.api.util;

import java.util.concurrent.ExecutionException;
import net.dv8tion.jda.api.entities.MessageEmbed;

/**
 * This exception is thrown whenever a user tries to executes a command with insufficient
 * authorization.<br>
 * Here a descriptive error message should be returned, instead of just the normal stack trace.
 */
public abstract class ValidationException extends ExecutionException {
  private static final long serialVersionUID = 8927239723989071327L;
  
  public abstract MessageEmbed getErrorMessage();
}
