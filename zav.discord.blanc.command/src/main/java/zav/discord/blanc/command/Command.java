package zav.discord.blanc.command;

import zav.discord.blanc.Argument;
import zav.discord.blanc.job.Job;

import java.util.List;

public interface Command extends Job {
  void validate() throws InvalidCommandException;
  default void postConstruct(List<? extends Argument> args) {
  
  }
}
