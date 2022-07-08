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

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
import zav.discord.blanc.api.listener.BlacklistListener;
import zav.discord.blanc.api.listener.SiteComponentListener;
import zav.discord.blanc.api.listener.SlashCommandListener;
import zav.discord.blanc.api.listener.TextChannelListener;
import zav.discord.blanc.databind.Credentials;
import zav.discord.blanc.databind.Rank;
import zav.discord.blanc.databind.UserEntity;
import zav.discord.blanc.runtime.internal.BlancModule;
import zav.discord.blanc.runtime.internal.CommandResolver;
import zav.discord.blanc.runtime.internal.JsonUtils;
import zav.discord.blanc.runtime.job.CleanupJob;
import zav.discord.blanc.runtime.job.PresenceJob;
import zav.discord.blanc.runtime.job.RedditJob;
import zav.jrc.client.Duration;
import zav.jrc.client.FailedRequestException;
import zav.jrc.client.UserlessClient;
import zav.jrc.client.guice.UserlessClientModule;

/**
 * Entry point for the application.
 */
public class Main {
  
  static {
    System.setProperty("org.jboss.logging.provider", "slf4j");
  }
  
  private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
  private final List<CommandData> commands = CommandResolver.getCommands();
  private final List<Object> listeners = new ArrayList<>();
  private final Credentials credentials;
  private final Injector guice;
  private final Client client;

  @SuppressFBWarnings(value = "BAD_PRACTICE")
  private Main(Credentials credentials) throws Exception {
    this.credentials = credentials;
    this.guice = loadGuice();
    this.client = loadDiscordClient();
    this.loadRedditClient();
    this.loadDatabase();
    LOGGER.info("All Done~");
  }
  
  private Injector loadGuice() {
    LOGGER.info("Loading Guice");
    List<Module> modules = new ArrayList<>();
    modules.add(new BlancModule(credentials));
    modules.add(new UserlessClientModule());
    return Guice.createInjector(modules);
  }
  
  private UserlessClient loadRedditClient() throws FailedRequestException {
    LOGGER.info("Loading Reddit Client");
    UserlessClient reddit = guice.getInstance(UserlessClient.class);
    reddit.login(Duration.TEMPORARY);
    return reddit;
  }
  
  private Client loadDiscordClient() throws IOException {
    LOGGER.info("Loading Discord Client");
    ScheduledExecutorService pool = guice.getInstance(ScheduledExecutorService.class);
    CommandParser parser = guice.getInstance(CommandParser.class);
    Client client = guice.getInstance(Client.class);
    
    EntityManagerFactory factory = client.getEntityManagerFactory();
    listeners.add(new SlashCommandListener(pool, parser));
    listeners.add(new TextChannelListener(factory));
    listeners.add(new BlacklistListener(client.getPatternCache()));
    listeners.add(new SiteComponentListener(client.getSiteCache()));

    LOGGER.info("Starting jobs for client");
    Runnable job = new RedditJob(client.getSubredditObservable()); 
    pool.scheduleAtFixedRate(job, 1, 1, TimeUnit.MINUTES);
    
    Runnable cleanupJob = new CleanupJob(client);    
    pool.scheduleAtFixedRate(cleanupJob, 1, 1, TimeUnit.HOURS);
    
    Runnable presenceJob = new PresenceJob(client);
    pool.scheduleAtFixedRate(presenceJob, 0, 1, TimeUnit.HOURS);
    
    LOGGER.info("Loading shards");
    for (JDA shard : client.getShards()) {
      loadShard(shard);
    }
    
    return client;
  }
  
  private void loadShard(JDA shard) throws IOException {
    LOGGER.info("Adding event listeners for shard {}", shard.getShardInfo());
    shard.addEventListener(listeners.toArray());
    
    LOGGER.info("Clear existing guild commands for shard {}", shard.getShardInfo());
    for (Guild guild : shard.getGuilds()) {
      loadGuild(guild);
    }
    
    LOGGER.info("Updating commands for shard {}", shard.getShardInfo());
    shard.updateCommands().addCommands(commands).complete();
    shard.retrieveCommands().complete();
  }
  
  private void loadGuild(Guild guild) {
    LOGGER.info("Clear existing guild commands for guild {}", guild.getName());
    guild.updateCommands().addCommands().complete();
  }
  
  @SuppressFBWarnings(value = "RCN_REDUNDANT_NULLCHECK_WOULD_HAVE_BEEN_A_NPE")
  private void loadDatabase() {
    LOGGER.info("Loading Database");
    EntityManagerFactory factory = client.getEntityManagerFactory();
    User owner = client.getShards().get(0).retrieveUserById(credentials.getOwner()).complete();
    
    if (owner == null) {
      LOGGER.error("User with id {} doesn't exist.", credentials.getOwner());
    } else {
      try (EntityManager entityManager = factory.createEntityManager()) {
        UserEntity entity = UserEntity.getOrCreate(entityManager, owner);
        entity.setRanks(List.of(Rank.ROOT));
        entityManager.getTransaction().begin();
        entityManager.persist(entity);
        entityManager.getTransaction().commit();
      }
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
    Credentials credentials = JsonUtils.read("Credentials.json", Credentials.class);
    new Main(credentials);
  }
}
