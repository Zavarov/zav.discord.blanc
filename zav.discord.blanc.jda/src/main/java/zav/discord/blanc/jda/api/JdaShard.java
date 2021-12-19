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

package zav.discord.blanc.jda.api;

import static zav.discord.blanc.jda.internal.GuiceUtils.injectGuild;
import static zav.discord.blanc.jda.internal.GuiceUtils.injectSelfUser;
import static zav.discord.blanc.jda.internal.GuiceUtils.injectUser;
import static zav.discord.blanc.jda.internal.ResolverUtils.resolveGuild;
import static zav.discord.blanc.jda.internal.ResolverUtils.resolveUser;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;
import javax.inject.Inject;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.entities.User;
import zav.discord.blanc.api.Argument;
import zav.discord.blanc.api.Shard;

/**
 * Implementation of a shard view, backed by JDA.
 */
public class JdaShard implements Shard {
  // Only one instance per guild
  private final Map<Long, JdaGuild> guildCache = new ConcurrentHashMap<>();
  
  @Inject
  private JDA jda;
  
  @Inject
  private ExecutorService jobQueue;
  
  @Override
  public JdaSelfUser getSelfUser() {
    SelfUser jdaSelfUser = jda.getSelfUser();
    
    return injectSelfUser(jdaSelfUser);
  }
  
  @Override
  public JdaGuild getGuild(Argument argument) {
    Guild jdaGuild = resolveGuild(jda, argument);
    
    return getGuild(jdaGuild);
  }
  
  private JdaGuild getGuild(Guild jdaGuild) {
    return guildCache.computeIfAbsent(jdaGuild.getIdLong(), id -> injectGuild(jdaGuild));
  }
  
  @Override
  public JdaUser getUser(Argument argument) {
    User jdaUser = resolveUser(jda, argument);
    
    return injectUser(jdaUser);
  }
  
  @Override
  public Collection<JdaGuild> getGuilds() {
    return jda.getGuilds()
          .stream()
          .map(this::getGuild)
          .collect(Collectors.toUnmodifiableList());
  }
  
  @Override
  public void shutdown() {
    jda.shutdown();
    jobQueue.shutdown();
  }
  
  @Override
  public <T extends Runnable> void submit(T job) {
    jobQueue.submit(job);
  }
}
