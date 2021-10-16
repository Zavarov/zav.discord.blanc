package zav.discord.blanc.command.parser;

import zav.discord.blanc.command.Command;
import zav.discord.blanc.databind.Message;

public interface Parser {
  Command parse(Message content);
}
