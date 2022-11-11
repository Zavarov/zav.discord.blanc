package zav.discord.blanc.api.util;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import org.jetbrains.annotations.Contract;

/**
 * Abstract implementation of an application context. Classes can be bound to singleton instances.
 */
public abstract class AbstractApplicationContext implements ApplicationContext {
  private final Map<Class<?>, Object> context = new HashMap<>();

  @Override
  @Contract(pure = true)
  public <T> T get(Class<T> clazz) {
    Object result = context.get(clazz);

    if (result == null) {
      throw new NoSuchElementException("No element bound for " + clazz.toString());
    }

    return clazz.cast(result);
  }

  @Override
  @Contract(mutates = "this")
  public <T> void bind(Class<T> clazz, T object) {
    context.put(clazz, object);
  }
}
