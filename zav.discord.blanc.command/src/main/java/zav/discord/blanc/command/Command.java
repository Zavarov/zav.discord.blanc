package zav.discord.blanc.command;

import zav.discord.blanc.job.Job;

public interface Command extends Job {
  void validate() throws InvalidCommandException;
}
