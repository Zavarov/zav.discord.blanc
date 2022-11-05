package zav.discord.blanc.api.util;

public interface ApplicationContext {
  <T> T get(Class<T> clazz);
  <T> void bind(Class<T> clazz, T object);
}
