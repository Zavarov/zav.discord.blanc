package zav.discord.blanc.api.cache;

import net.dv8tion.jda.api.entities.Guild;
import zav.discord.blanc.api.util.RegularExpressionMatcher;
import zav.discord.blanc.databind.GuildEntity;

/**
 * A cache built upon the JPA persistence layer. For each guild, it stores a regular expression
 * including all registered entries. This means that the expensive task of parsing this expression
 * only has to be done once. The entry has to be invalidated, whenever one of the entries is
 * modified.
 */
public class AutoResponseCache extends AbstractCache<Guild, RegularExpressionMatcher> {
  
  @Override
  protected RegularExpressionMatcher fetch(Guild guild) {
    GuildEntity entity = GuildEntity.find(guild);

    if (entity.getAutoResponses().isEmpty()) {
      return null;
    }

    return new RegularExpressionMatcher(entity.getAutoResponses());
  }
}
