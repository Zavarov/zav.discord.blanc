package zav.discord.blanc.command.parser;

import java.util.List;
import java.util.Optional;
import zav.discord.blanc.Argument;

/**
 * Base interface for the intermediate representation of a command.<br>
 * All commands consist of a (guild-specific) prefix, a distinct name and optionally, flags and
 * arguments. As an example:
 * <pre>
 *   b:foo -f bar
 *
 *   (prefix) b
 *   (name) foo
 *   (flags) [f]
 *   (arguments) [bar]
 * </pre>
 */
public interface IntermediateCommand {
  Optional<String> getPrefix();
  
  String getName();
  
  List<String> getFlags();
  
  List<? extends Argument> getArguments();
}
