/*
 * Copyright (c) 2022 Zavarov.
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package zav.discord.blanc.reddit;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zav.discord.blanc.databind.TextChannelEntity;

/**
 * Utility class for initializing all subreddit feeds that have been mapped to a
 * {@link TextChannel}.
 *
 * @deprecated Deprecated in favor of {@link WebhookInitializer}.
 */
@Singleton
@Deprecated
@NonNullByDefault
public class TextChannelInitializer {
  private static final Logger LOGGER = LoggerFactory.getLogger(TextChannelInitializer.class);
  
  private final EntityManagerFactory factory;
  private final SubredditObservable observable;
  
  /**
   * Creates a new instance of this class.
   *
   * @param factory The JPA persistence manager.
   * @param observable The global subreddit observable.
   */
  @Inject
  @SuppressFBWarnings(value = "EI_EXPOSE_REP2")
  public TextChannelInitializer(EntityManagerFactory factory, SubredditObservable observable) {
    this.factory = factory;
    this.observable = observable;
  }
  
  /**
   * Initialize the listeners for all registered subreddits per text channel.
   *
   * @param guild One of the guilds visible to the bot.
   */
  public void load(Guild guild) {
    for (TextChannel textChannel : guild.getTextChannels()) {
      load(textChannel);
    }
  }

  @SuppressFBWarnings(value = "RCN_REDUNDANT_NULLCHECK_WOULD_HAVE_BEEN_A_NPE")
  private void load(TextChannel textChannel) {
    try (EntityManager entityManager = factory.createEntityManager()) {
      long channelId = textChannel.getIdLong();
      @Nullable
      TextChannelEntity entity = entityManager.find(TextChannelEntity.class, channelId);
    
      if (entity != null) {
        for (String subreddit : entity.getSubreddits()) {
          observable.addListener(subreddit, textChannel);
          LOGGER.info("Add subreddit '{}' to textChannel '{}'.", subreddit, textChannel.getName());
        }
      }
    }
  }
}
