package zav.discord.blanc.api.util;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import java.time.Duration;
import java.util.Optional;
import net.dv8tion.jda.api.entities.Guild;
import org.jetbrains.annotations.Contract;
import zav.discord.blanc.api.Client;
import zav.discord.blanc.databind.GuildEntity;

/**
 * A cache built upon the JPA persistence layer. For each guild, it stores a regular expression
 * including all registered entries. This means that the expensive task of parsing this expression
 * only has to be done once. The entry has to be invalidated, whenever one of the entries is
 * modified.
 */
public class AutoResponseCache {
  private static final int MAX_CACHE_SIZE = 1024;
  private final Cache<Guild, RegularExpressionMatcher> cache;
  private final EntityManagerFactory factory;
  
  /**
   * Creates a new instance for the given persistence manager. The instance is managed by the
   * {@link Client} class.
   *
   * @param factory The persistence manager.
   */
  @SuppressFBWarnings(value = "EI_EXPOSE_REP")
  public AutoResponseCache(EntityManagerFactory factory) {
    this.factory = factory;
    this.cache = Caffeine.newBuilder()
          .expireAfterAccess(Duration.ofHours(1))
          .maximumSize(MAX_CACHE_SIZE)
          .build();
  }

  /**
   * Removes the provided guild from the cache.
   *
   * @param guild One of the guilds available to the program.
   */
  @Contract(mutates = "this")
  public void invalidate(Guild guild) {
    cache.invalidate(guild);
  }
  
  /**
   * Returns the matcher corresponding to this guild. The matcher returns the answer that should be
   * send if one of the registered expression matches a given input message. If no responses are
   * specified, {@link Optional#empty()} is returned.<br>
   * If this method is called for the first time, a new matcher instance is created and stored in
   * cache. Further calls reuse this instance.
   *
   * @param guild One of the guilds visible to the application.
   * @return As described.
   */
  @Contract(pure = true)
  public Optional<RegularExpressionMatcher> get(Guild guild) {
    return Optional.ofNullable(cache.get(guild, this::fetch));
  }
  
  private RegularExpressionMatcher fetch(Guild guild) {
    try (EntityManager entityManager = factory.createEntityManager()) {
      GuildEntity entity = GuildEntity.getOrCreate(entityManager, guild);
      
      if (entity.getAutoResponses().isEmpty()) {
        return null;
      }
      
      return new RegularExpressionMatcher(entity.getAutoResponses());
    }
  }
}
