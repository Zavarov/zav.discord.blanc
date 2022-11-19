package zav.discord.blanc.api.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import java.time.Duration;
import java.util.Optional;
import org.jetbrains.annotations.Contract;

/**
 * The base class for all object caches. Due to the cost of accessing the database, it is
 * recommended to keep frequently used objects in memory. For as long as possible. An object is
 * requested once from the database and then reused until it is evicted by the cleanup policy.<br>
 * If the database is updated, the cache entry has to be explicitly invalidated, in order to return
 * the new value.
 *
 * @param <U> The unique id of the stored object.
 * @param <V> The object to be stored.
 */
public abstract class AbstractCache<U, V> {
  private static final int MAX_CACHE_SIZE = 1024;
  /**
   * The internal object cache.
   */
  protected final Cache<U, V> cache;

  /**
   * Creates a new cache instance. The elements are set to expire after one hour and the cache may
   * hold up to {@link #MAX_CACHE_SIZE} elements.
   */
  protected AbstractCache() {
    this.cache = Caffeine.newBuilder()
        .expireAfterAccess(Duration.ofHours(1))
        .maximumSize(MAX_CACHE_SIZE)
        .build();
  }

  /**
   * Removes the provided element from the cache.
   *
   * @param key One of the cached elements.
   */
  @Contract(mutates = "this")
  public void invalidate(U key) {
    cache.invalidate(key);
  }

  /**
   * Returns the value associated with the given key.<br>
   * If this method is called for the first time or if the key has been
   * invalidated, a new value is computed via {@link #fetch(Object)}.<br>
   * In case a valid value can't be computed, {@link Optional#empty()} is
   * returned.
   *
   * @param key The key of the cached element.
   * @return The value associated with the key.
   */
  @Contract(pure = true)
  public Optional<V> get(U key) {
    return Optional.ofNullable(cache.get(key, this::fetch));
  }

  /**
   * Retrieves the element with the given key from the cache. May be {@code null} if no element with
   * the given key is stored in the cache.
   *
   * @param key The key of the cached element.
   * @return The value associated with the key or {@code null} if no such entry exists.
   */
  protected abstract V fetch(U key);
}
