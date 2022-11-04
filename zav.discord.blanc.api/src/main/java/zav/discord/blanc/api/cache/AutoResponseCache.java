package zav.discord.blanc.api.cache;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import net.dv8tion.jda.api.entities.Guild;
import zav.discord.blanc.api.Client;
import zav.discord.blanc.api.util.RegularExpressionMatcher;
import zav.discord.blanc.databind.GuildEntity;

/**
 * A cache built upon the JPA persistence layer. For each guild, it stores a regular expression
 * including all registered entries. This means that the expensive task of parsing this expression
 * only has to be done once. The entry has to be invalidated, whenever one of the entries is
 * modified.
 */
public class AutoResponseCache extends AbstractCache<Guild, RegularExpressionMatcher> {
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
  }
  
  @Override
  protected RegularExpressionMatcher fetch(Guild guild) {
    try (EntityManager entityManager = factory.createEntityManager()) {
      GuildEntity entity = GuildEntity.getOrCreate(entityManager, guild);
      
      if (entity.getAutoResponses().isEmpty()) {
        return null;
      }
      
      return new RegularExpressionMatcher(entity.getAutoResponses());
    }
  }
}
