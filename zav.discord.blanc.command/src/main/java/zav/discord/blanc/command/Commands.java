package zav.discord.blanc.command;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import zav.discord.blanc.command.parser.Argument;

public final class Commands {
  private static final Map<String, Function<List<? extends Argument>, Command>> commands = new HashMap<>();
  
  private Commands() {}
  
  public static boolean bind(String key, Function<List<? extends Argument>, Command> command) {
    return commands.put(key, command) != null;
  }
  
  public static Optional<Command> get(String key, List<? extends Argument> args) {
    return Optional.ofNullable(commands.get(key)).map(factory -> factory.apply(args));
  }
  
  public static void clear() {
    commands.clear();
  }
}
