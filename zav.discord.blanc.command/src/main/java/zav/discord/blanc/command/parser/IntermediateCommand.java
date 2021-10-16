package zav.discord.blanc.command.parser;

import java.util.List;
import java.util.Optional;

public interface IntermediateCommand {
  Optional<String> getPrefix();
  String getName();
  List<String> getFlags();
  List<? extends Argument> getArguments();
}
