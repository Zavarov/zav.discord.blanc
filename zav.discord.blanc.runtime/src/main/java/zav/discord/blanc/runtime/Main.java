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

import static zav.discord.blanc.runtime.internal.JsonUtils.read;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.name.Names;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zav.discord.blanc.api.Client;
import zav.discord.blanc.api.Rank;
import zav.discord.blanc.api.guice.ShardModule;
import zav.discord.blanc.api.listener.BlacklistListener;
import zav.discord.blanc.api.listener.GuildListener;
import zav.discord.blanc.api.listener.SiteComponentListener;
import zav.discord.blanc.api.listener.SlashCommandListener;
import zav.discord.blanc.api.listener.TextChannelListener;
import zav.discord.blanc.databind.UserEntity;
import zav.discord.blanc.databind.io.CredentialsEntity;
import zav.discord.blanc.db.UserTable;
import zav.discord.blanc.reddit.RedditJob;
import zav.discord.blanc.runtime.internal.BlancModule;
import zav.discord.blanc.runtime.internal.CommandResolver;
import zav.discord.blanc.runtime.internal.RedditUtils;
import zav.discord.blanc.runtime.job.PresenceJob;
import zav.jrc.client.FailedRequestException;
import zav.jrc.client.guice.UserlessClientModule;

/**
 * Entry point for the application.
 */
public class Main {
  private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
  private static Injector injector;
  
  /**
   * Main class of the application.
   *
   * @param args Command line arguments.
   * @throws Exception If the application couldn't be started.
   */
  public static void main(String[] args) throws Exception {
    setUp();
    
    initReddit();
    
    initJda();
  
    initDb();
  }
  
  private static void setUp() throws IOException {
    LOGGER.info("Initialize Commands");
    CommandResolver.init();
    
    LOGGER.info("Read Credentials.");
    CredentialsEntity credentials = read("BlancCredentials.json", CredentialsEntity.class);
  
    LOGGER.info("Set up injector.");
    injector = Guice.createInjector(new BlancModule(credentials), new UserlessClientModule());
  }
  
  private static void initDb() throws SQLException {
    LOGGER.info("Initialize databases");
    long ownerId = injector.getInstance(Key.get(Long.class, Names.named("owner")));
    UserTable db = injector.getInstance(UserTable.class);
    Client client = injector.getInstance(Client.class);
    
    User owner = client.getShards().get(0).retrieveUserById(ownerId).complete();
    
    if (owner == null) {
      LOGGER.error("User with id {} doesn't exist.", ownerId);
    }
    
    if (!db.contains(owner)) {
      LOGGER.info("Owner with id {} not contained in database. Create new root user...", ownerId);
      UserEntity entity = new UserEntity()
            .withId(ownerId)
            .withDiscriminator(StringUtils.EMPTY)
            .withName(StringUtils.EMPTY)
            .withRanks(List.of(Rank.ROOT.name()));
      
      db.put(entity);
    }
  }
  
  private static void initReddit() throws FailedRequestException {
    RedditUtils.init(injector);
  }
  
  private static void initJda() throws Exception {
    LOGGER.info("Initialize JDA shards");
    Client client = injector.getInstance(Client.class);
    List<CommandData> commands = CommandResolver.getCommands();
    
    for (JDA shard : client.getShards()) {
      initJobs(shard);
      initCommands(shard, commands);
      initListeners(shard);
    }

    initJobs();
  }
  
  private static void initCommands(JDA shard, List<CommandData> commands) {
    for (CommandData cmd : commands) {
      LOGGER.info("Registering {} for shard {}", cmd.getName(), shard);
    }
    
    shard.updateCommands().addCommands(commands).complete();
  }
  
  private static void initJobs() {
    ScheduledExecutorService executor = injector.getInstance(ScheduledExecutorService.class);
    Runnable job = injector.getInstance(RedditJob.class);
    executor.scheduleAtFixedRate(job, 1, 1, TimeUnit.MINUTES);
  }
  
  private static void initJobs(JDA shard) throws Exception {
    ScheduledExecutorService executor = injector.getInstance(ScheduledExecutorService.class);
    Runnable job = new PresenceJob(shard.getPresence());
    executor.scheduleAtFixedRate(job, 0, 1, TimeUnit.HOURS);
  }
  
  private static void initListeners(JDA jda) {
    Injector shardInjector = injector.createChildInjector(new ShardModule());
    ScheduledExecutorService queue = shardInjector.getInstance(ScheduledExecutorService.class);
  
    // Constructor has to be called explicitly. Otherwise, Guice picks the wrong injector
    jda.addEventListener(new SlashCommandListener(queue, shardInjector));
    jda.addEventListener(shardInjector.getInstance(GuildListener.class));
    jda.addEventListener(shardInjector.getInstance(TextChannelListener.class));
    jda.addEventListener(shardInjector.getInstance(BlacklistListener.class));
    jda.addEventListener(shardInjector.getInstance(SiteComponentListener.class));
  }
}
