package zav.discord.blanc.command;

import java.util.List;
import zav.discord.blanc.api.Argument;
import zav.discord.blanc.api.Job;

/**
 * Base interface implemented by all commands.
 */
public interface Command extends Job {
  void validate() throws InvalidCommandException;
  
  default void postConstruct(List<? extends Argument> args) {
  
  }
}
