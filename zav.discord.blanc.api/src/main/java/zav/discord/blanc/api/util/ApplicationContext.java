package zav.discord.blanc.api.util;

import java.util.NoSuchElementException;

/**
 * Central interface for managing all application-wide objects. Each class can be mapped to a
 * singleton instance and be accessed from anywhere in the program.
 */
public interface ApplicationContext {
  /**
   * Creates the object bound to the given class.<br>
   * If no instance is found, a {@link NoSuchElementException} is thrown.
   *
   * @param <T> The type of the requested object.
   * @param clazz The class of the requested object.
   * @return A singleton instance of the requested class.
   */
  <T> T get(Class<T> clazz);
  
  /**
   * Maps the provided object to the given class. If the class is already bound to another object,
   * the old value is overwritten.
   *
   * @param <T> The type of the object to bound.
   * @param clazz The class of the object to bound.
   * @param object The object to bound.
   */
  <T> void bind(Class<T> clazz, T object);
}
