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

package zav.discord.blanc.api.internal;

import static zav.discord.blanc.api.Constants.DISCORD_TOKEN;
import static zav.discord.blanc.api.Constants.SHARD_COUNT;

import com.google.inject.Injector;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.apache.commons.lang3.concurrent.TimedSemaphore;
import org.jetbrains.annotations.Contract;
import zav.discord.blanc.api.guice.ShardModule;

/**
 * Utility class for initializing Discord shards.
 */
@Singleton
public class ShardSupplier implements Iterator<JDA> {
  /**
   * A set of all intents that are required for this bot to work.
   */
  private static final Set<GatewayIntent> intents = Set.of(
        GatewayIntent.DIRECT_MESSAGES,
        GatewayIntent.DIRECT_MESSAGE_REACTIONS,
        GatewayIntent.GUILD_MESSAGES,
        GatewayIntent.GUILD_MESSAGE_REACTIONS
  );
  
  /**
   * A set of all flags which are explicitly disabled.
   */
  private static final Set<CacheFlag> disabledFlags = Set.of(
        CacheFlag.VOICE_STATE,
        CacheFlag.EMOTE,
        CacheFlag.ACTIVITY,
        CacheFlag.CLIENT_STATUS,
        CacheFlag.ONLINE_STATUS
  );
  
  /**
   * The minimum amount of time between connecting multiple JDA instances is 5 seconds.<br>
   * We use an additional second as buffer, bringing the time up to 6 seconds.
   */
  private final TimedSemaphore rateLimiter = new TimedSemaphore(6, TimeUnit.SECONDS, 1);
  private final Injector clientInjector;
  private final String token;
  private final long shardCount;
  private int index = 0;
  
  @Inject
  public ShardSupplier(Injector injector, @Named(DISCORD_TOKEN) String token, @Named(SHARD_COUNT) long shardCount) {
    this.clientInjector = injector;
    this.token = token;
    this.shardCount = shardCount;
  }
  
  @Override
  @Contract(pure = true)
  public boolean hasNext() {
    return index < shardCount;
  }
  
  @Override
  @Contract(mutates = "this")
  public JDA next() {
    try {
      rateLimiter.acquire();
      
      JDA jda = JDABuilder.create(intents)
            .setToken(token)
            .useSharding(index++, (int) shardCount)
            .disableCache(disabledFlags)
            .build();
      
      jda.awaitReady();
  
      Injector shardInjector = clientInjector.createChildInjector(new ShardModule());
      
      jda.addEventListener(createGuildListener(shardInjector));
      jda.addEventListener(createBlacklistListener(shardInjector));
      jda.addEventListener(createSiteComponentListener(shardInjector));
      jda.addEventListener(createSlashCommandListener(shardInjector));
      
      return jda;
    } catch (Exception e)  {
      throw new RuntimeException(e);
    }
  }
  
  private Object[] createGuildListener(Injector shardInjector) {
    return new EventListener[] {
          shardInjector.getInstance(GuildTableListener.class),
          shardInjector.getInstance(WebhookTableListener.class),
          shardInjector.getInstance(TextChannelTableListener.class)
    };
  }
  
  private EventListener createBlacklistListener(Injector shardInjector) {
    return shardInjector.getInstance(BlacklistListener.class);
  }
  
  private EventListener createSiteComponentListener(Injector shardInjector) {
    return shardInjector.getInstance(SiteComponentListener.class);
  }
  
  private EventListener createSlashCommandListener(Injector shardInjector) {
    ScheduledExecutorService queue = shardInjector.getInstance(ScheduledExecutorService.class);
    return new SlashCommandListener(queue, shardInjector);
  }
}
