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
  protected final Cache<U, V> cache;

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
   * @param key One of the cached elements.
   * @return The value associated with the key.
   */
  @Contract(pure = true)
  public Optional<V> get(U key) {
    return Optional.ofNullable(cache.get(key, this::fetch));
  }

  protected abstract V fetch(U key);
}
