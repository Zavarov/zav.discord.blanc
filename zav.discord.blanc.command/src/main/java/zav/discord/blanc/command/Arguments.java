package zav.discord.blanc.command;

import zav.discord.blanc.Argument;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public final class Arguments {
  private static final Map<Class<?>, Function<Argument, ?>> arguments = new HashMap<>();
  
  private Arguments() {}
  
  public static <T> boolean bind(Class<T> key, Function<Argument, T> argument) {
    return arguments.put(key, argument) != null;
  }
  
  public static <T> T get(Class<T> key, Argument argument) {
    return Optional.ofNullable(arguments.get(key))
          .map(factory -> factory.apply(argument))
          .map(key::cast)
          .orElseThrow();
  }
  
  public static void clear() {
    arguments.clear();
  }
}
