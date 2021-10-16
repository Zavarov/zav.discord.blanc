package zav.discord.blanc.command.parser;

import zav.discord.blanc.databind.Message;

import java.util.Optional;

public interface Parser {
  Optional<? extends IntermediateCommand> parse(Message content);
}
