/*
 * Copyright (c) 2021 Zavarov.
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

package zav.discord.blanc.jda;

import static zav.discord.blanc.jda.internal.GuiceUtils.injectShard;

import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import javax.inject.Named;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.apache.commons.lang3.concurrent.TimedSemaphore;
import zav.discord.blanc.command.parser.Parser;
import zav.discord.blanc.jda.api.JdaShard;
import zav.discord.blanc.jda.internal.listener.BlacklistListener;
import zav.discord.blanc.jda.internal.listener.GuildActivityListener;
import zav.discord.blanc.jda.internal.listener.GuildCommandListener;
import zav.discord.blanc.jda.internal.listener.PrivateCommandListener;

/**
 * Utility class for initializing Discord shards.
 */
public class JdaShardSupplier implements Iterator<JdaShard> {
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
  @Named("discordToken")
  private String token;
  
  @Inject
  @Named("shardCount")
  private long shardCount;
  
  @Inject
  private Parser parser;
  
  private int index = 0;
  
  @Override
  public boolean hasNext() {
    return index < shardCount;
  }
  
  @Override
  public JdaShard next() {
    try {
      rateLimiter.acquire();
    
      JDA jda = JDABuilder.create(intents)
            .setToken(token)
            .useSharding(index++, (int) shardCount)
            .build();
      
      JdaShard shard = injectShard(jda);
      
      jda.addEventListener(new BlacklistListener(shard));
      jda.addEventListener(new GuildActivityListener());
      jda.addEventListener(new GuildCommandListener(parser, shard));
      jda.addEventListener(new PrivateCommandListener(parser, shard));
      
      return shard;
    } catch (Exception e)  {
      throw new RuntimeException(e);
    }
  }
}
