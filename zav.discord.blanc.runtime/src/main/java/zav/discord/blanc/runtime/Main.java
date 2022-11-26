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

package zav.discord.blanc.runtime;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zav.discord.blanc.api.Client;
import zav.discord.blanc.api.CommandParser;
import zav.discord.blanc.api.CommandProvider;
import zav.discord.blanc.api.Shard;
import zav.discord.blanc.api.cache.AutoResponseCache;
import zav.discord.blanc.api.cache.SiteCache;
import zav.discord.blanc.api.listener.AutoResponseListener;
import zav.discord.blanc.api.listener.SiteComponentListener;
import zav.discord.blanc.api.listener.SlashCommandListener;
import zav.discord.blanc.api.listener.TextChannelListener;
import zav.discord.blanc.api.util.ShardSupplier;
import zav.discord.blanc.databind.Credentials;
import zav.discord.blanc.databind.Rank;
import zav.discord.blanc.databind.UserEntity;
import zav.discord.blanc.reddit.SubredditObservable;
import zav.discord.blanc.runtime.internal.JsonUtils;
import zav.discord.blanc.runtime.internal.SimpleCommandParser;
import zav.discord.blanc.runtime.internal.SimpleCommandProvider;
import zav.discord.blanc.runtime.job.CleanupJob;
import zav.discord.blanc.runtime.job.PresenceJob;
import zav.discord.blanc.runtime.job.RedditJob;
import zav.jrc.client.Duration;
import zav.jrc.client.FailedRequestException;
import zav.jrc.client.UserlessClient;
import zav.jrc.databind.io.CredentialsEntity;
import zav.jrc.databind.io.UserAgentEntity;

/**
 * Entry point for the application.
 */
public class Main {
  
  private static final File DISCORD_CREDENTIALS = new File("DiscordUser.json");
  private static final File REDDIT_CREDENTIALS = new File("RedditUser.json");
  private static final File USER_AGENT = new File("UserAgent.json");
  
  private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
  private final List<CommandData> commands = JsonUtils.getCommands();
  private Credentials credentials;
  private UserlessClient reddit;
  private Client client;

  @SuppressFBWarnings(value = "BAD_PRACTICE")
  private Main() throws Exception {
    credentials = Credentials.read(DISCORD_CREDENTIALS);
    reddit = loadRedditClient();
    client = loadDiscordClient();
    loadDatabase();
    LOGGER.info("All Done~");
  }
  
  private UserlessClient loadRedditClient() throws FailedRequestException, IOException {
    LOGGER.info("Loading Reddit Client");
    UserAgentEntity userAgent = UserAgentEntity.read(USER_AGENT);
    CredentialsEntity credentials = CredentialsEntity.read(REDDIT_CREDENTIALS);
    UserlessClient reddit = new UserlessClient(userAgent, credentials);
    reddit.login(Duration.TEMPORARY);
    return reddit;
  }
  
  private Client loadDiscordClient() throws IOException {
    LOGGER.info("Loading Discord Client");
    ScheduledExecutorService pool = Executors.newScheduledThreadPool(2);
    Client client = new Client();
    client.bind(Credentials.class, credentials);
    client.bind(SubredditObservable.class, new SubredditObservable(reddit, pool));
    client.postConstruct(new ShardSupplier(client, credentials));

    LOGGER.info("Starting jobs for client");
    Runnable job = new RedditJob(client); 
    pool.scheduleAtFixedRate(job, 1, 1, TimeUnit.MINUTES);
    
    Runnable cleanupJob = new CleanupJob(client);    
    pool.scheduleAtFixedRate(cleanupJob, 1, 1, TimeUnit.HOURS);
    
    Runnable presenceJob = new PresenceJob(client);
    pool.scheduleAtFixedRate(presenceJob, 0, 1, TimeUnit.HOURS);
    
    LOGGER.info("Loading shards");
    for (Shard shard : client.getShards()) {
      loadShard(shard);
    }
    
    return client;
  }
  
  private void loadShard(Shard shard) throws IOException {
    final ScheduledExecutorService pool = Executors.newScheduledThreadPool(4);
    final JDA jda = shard.getJda();
    
    LOGGER.info("Initializing Application Context");
    shard.bind(AutoResponseCache.class, new AutoResponseCache());
    shard.bind(SiteCache.class, new SiteCache());
    shard.bind(ScheduledExecutorService.class, pool);
    
    LOGGER.info("Adding event listeners for shard {}", jda.getShardInfo());
    CommandProvider provider = new SimpleCommandProvider();
    CommandParser parser = new SimpleCommandParser(shard, provider);
    List<Object> listeners = new ArrayList<>();
    listeners.add(new SlashCommandListener(pool, parser));
    listeners.add(new TextChannelListener());
    listeners.add(new AutoResponseListener(shard.get(AutoResponseCache.class)));
    listeners.add(new SiteComponentListener(shard.get(SiteCache.class)));
    jda.addEventListener(listeners.toArray());
    
    LOGGER.info("Clear existing guild commands for shard {}", jda.getShardInfo());
    for (Guild guild : jda.getGuilds()) {
      loadGuild(guild);
    }
    
    LOGGER.info("Updating commands for shard {}", jda.getShardInfo());
    jda.updateCommands().addCommands(commands).complete();
  }
  
  private void loadGuild(Guild guild) {
    LOGGER.info("Clear existing guild commands for guild {}", guild.getName());
    guild.updateCommands().addCommands().complete();
  }
  
  @SuppressFBWarnings(value = "RCN_REDUNDANT_NULLCHECK_WOULD_HAVE_BEEN_A_NPE")
  private void loadDatabase() {
    LOGGER.info("Loading Database");
    User owner = client.getShard(0).getJda().retrieveUserById(credentials.getOwner()).complete();
    
    if (owner == null) {
      LOGGER.error("User with id {} doesn't exist.", credentials.getOwner());
    } else {
      UserEntity entity = UserEntity.find(owner);
      entity.setRanks(List.of(Rank.DEVELOPER));
      entity.merge();
    }
  }
  
  /**
   * Main class of the application.
   *
   * @param args Command line arguments.
   * @throws Exception If the application couldn't be started.
   */
  @SuppressFBWarnings(value = "BAD_PRACTICE")
  public static void main(String[] args) throws Exception {    
    new Main();
  }
}
