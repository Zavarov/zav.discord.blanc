package zav.discord.blanc.command.parser;

import zav.discord.blanc.command.Command;
import zav.discord.blanc.databind.Message;
import zav.discord.blanc.view.GuildMessageView;
import zav.discord.blanc.view.PrivateMessageView;

import java.util.Optional;

public interface Parser {
  IntermediateCommand parse(Message content);
  Optional<? extends Command> parse(GuildMessageView source);
  Optional<? extends Command> parse(PrivateMessageView source);
}
