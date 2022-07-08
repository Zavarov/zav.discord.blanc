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

package zav.discord.blanc.api;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.dv8tion.jda.api.JDA;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.jetbrains.annotations.Contract;
import zav.discord.blanc.databind.Credentials;
import zav.discord.blanc.reddit.SubredditObservable;
import zav.jrc.client.UserlessClient;

/**
 * The application instance over all shards.
 */
@Singleton
@NonNullByDefault
public class Client {
  private final List<JDA> shards = new ArrayList<>();
  private final EntityManagerFactory factory;
  private final Credentials credentials;
  private final ScheduledExecutorService eventQueue;
  private final PatternCache patternCache;
  private final SiteCache siteCache;
  private final SubredditObservable subredditObservable;
  

  /** 
   * Initializes the client instance.
   *
   * @param credentials The credentials used to authenticate this program to Discord.
   * @param client The JRC client.
   */
  @Inject
  @SuppressFBWarnings(value = "EI_EXPOSE_REP2")
  public Client(Credentials credentials, UserlessClient client) {
    this.credentials = credentials;
    this.factory = Persistence.createEntityManagerFactory("discord-entities");
    this.eventQueue = Executors.newScheduledThreadPool(4);
    this.patternCache = new PatternCache(factory);
    this.siteCache = new SiteCache();
    this.subredditObservable = new SubredditObservable(client, eventQueue);
  }
  
  /**
   * Creates and initializes all shards.
   *
   * @param supplier The provider used to create those instances.
   */
  @Inject
  @Contract(mutates = "this")
  public void postConstruct(ShardSupplier supplier) {
    supplier.forEachRemaining(shards::add);
  }
  
  /**
   * Returns an immutable list of all shard instance.
   *
   * @return As described.
   */
  @Contract(pure = true)
  public List<JDA> getShards() {
    return List.copyOf(shards);
  }
  
  /**
   * The shard in which a guild is located is determined using the following formula:
   * {@code (guild id >> 22) / #shards}.
   *
   * @param guildId A guild id.
   * @return The shard in which the guild is located.
   */
  @Contract(pure = true)
  public JDA getShard(long guildId) {
    // @See https://discord.com/developers/docs/topics/gateway#sharding
    long index = (guildId >> 22) % shards.size();
    return shards.get((int) index);
  }
  
  /**
   * Returns the configuration file.
   *
   * @return As described.
   */
  @Contract(pure = true)
  @SuppressFBWarnings(value = "EI_EXPOSE_REP")
  public Credentials getCredentials() {
    return credentials;
  }
  
  /**
   * Returns the shared executor pool.
   *
   * @return As described.
   */
  @Contract(pure = true)
  public ScheduledExecutorService getEventQueue() {
    return eventQueue;
  }
  
  /**
   * Returns the cache of all blacklisted expressions.
   *
   * @return As described.
   */
  @Contract(pure = true)
  public PatternCache getPatternCache() {
    return patternCache;
  }
  
  /**
   * Returns the cache of all interactive message.
   *
   * @return As described.
   */
  @Contract(pure = true)
  @SuppressFBWarnings(value = "EI_EXPOSE_REP")
  public SiteCache getSiteCache() {
    return siteCache;
  }
  
  /**
   * Returns the global subreddit observable.
   *
   * @return As described.
   */
  @Contract(pure = true)
  @SuppressFBWarnings(value = "EI_EXPOSE_REP")
  public SubredditObservable getSubredditObservable() {
    return subredditObservable;
  }

  /**
   * Returns the global JPA persistence manager.
   *
   * @return As described.
   */
  @Contract(pure = true)
  @SuppressFBWarnings(value = "EI_EXPOSE_REP")
  public EntityManagerFactory getEntityManagerFactory() {
    return factory;
  }
}
