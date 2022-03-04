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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import javax.inject.Named;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.apache.commons.lang3.concurrent.TimedSemaphore;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.jetbrains.annotations.Contract;

/**
 * Utility class for initializing Discord shards.
 */
@NonNullByDefault
public class JdaShardSupplier implements Iterator<JDA> {
  /**
   * The minimum amount of time between connecting multiple JDA instances is 5 seconds.<br>
   * We use an additional second as buffer, bringing the time up to 6 seconds.
   */
  private static final TimedSemaphore rateLimiter = new TimedSemaphore(6, TimeUnit.SECONDS, 1);
  
  /**
   * A set of all intents that are required for this bot to work.
   */
  private static final Set<GatewayIntent> intents = Set.of(
        GatewayIntent.DIRECT_MESSAGES,
        GatewayIntent.DIRECT_MESSAGE_REACTIONS,
        GatewayIntent.GUILD_MEMBERS,
        GatewayIntent.GUILD_PRESENCES,
        GatewayIntent.GUILD_MESSAGES,
        GatewayIntent.GUILD_MESSAGE_REACTIONS
  );
  
  @Inject
  @Named(DISCORD_TOKEN)
  private String token;
  
  @Inject
  @Named(SHARD_COUNT)
  private long shardCount;
  
  @Inject
  private Injector injector;
  
  private int index = 0;
  
  /*package*/ JdaShardSupplier() {
    // Create instance with Guice
  }
  
  @Override
  @Contract(pure = true)
  public boolean hasNext() {
    return index < shardCount;
  }
  
  @Override
  public JDA next() {
    try {
      rateLimiter.acquire();
      
      JDA jda = JDABuilder.create(intents)
            .setToken(token)
            .useSharding(index++, (int) shardCount)
            .disableCache(CacheFlag.VOICE_STATE, CacheFlag.EMOTE)
            .setMemberCachePolicy(MemberCachePolicy.ALL)
            .build();
      
      jda.awaitReady();
  
      Injector shardInjector = injector.createChildInjector(new ShardModule());
  
      jda.addEventListener(shardInjector.getInstance(BlacklistListener.class));
      jda.addEventListener(shardInjector.getInstance(GuildCommandListener.class));
      jda.addEventListener(shardInjector.getInstance(PrivateCommandListener.class));
      jda.addEventListener(shardInjector.getInstance(SiteComponentListener.class));
      
      return jda;
    } catch (Exception e)  {
      throw new RuntimeException(e);
    }
  }
}
