package zav.discord.blanc.command;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class Commands {
  private static final Map<String, Class<? extends Command>> commands = new HashMap<>();
  
  private Commands() {}
  
  public static boolean bind(String key, Class<? extends Command> command) {
    return commands.put(key, command) != null;
  }
  
  public static Optional<Class<? extends Command>> get(String key) {
    return Optional.ofNullable(commands.get(key));
  }
  
  public static void clear() {
    commands.clear();
  }
}
