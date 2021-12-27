/*
 * Copyright (c) 2020 Zavarov
 *
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
import com.google.inject.Key;
import com.google.inject.name.Names;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import zav.discord.blanc.activity.ActivityJob;
import zav.discord.blanc.api.Shard;
import zav.discord.blanc.command.Rank;
import zav.discord.blanc.databind.UserValueObject;
import zav.discord.blanc.db.GuildDatabase;
import zav.discord.blanc.db.RoleDatabase;
import zav.discord.blanc.db.TextChannelDatabase;
import zav.discord.blanc.db.UserDatabase;
import zav.discord.blanc.db.WebHookDatabase;
import zav.discord.blanc.jda.api.JdaClient;
import zav.discord.blanc.reddit.RedditJob;
import zav.discord.blanc.reddit.SubredditObservable;
import zav.discord.blanc.runtime.internal.CommandResolver;
import zav.discord.blanc.runtime.internal.guice.BlancModule;
import zav.discord.blanc.runtime.job.PresenceJob;
import zav.jrc.api.guice.JrcModule;
import zav.jrc.client.Client;
import zav.jrc.client.Duration;
import zav.jrc.client.guice.UserlessClientModule;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Main {
  private static final Logger LOGGER = LogManager.getLogger(Main.class);
  private static Injector injector;
  
  public static void main(String[] args) throws Exception {
    setUp();
    
    initDb();
    
    initJda();
  }
  
  private static void setUp() throws SQLException {
    LOGGER.info("Set up databases.");
    GuildDatabase.create();
    RoleDatabase.create();
    TextChannelDatabase.create();
    WebHookDatabase.create();
    UserDatabase.create();
  
    LOGGER.info("Set up commands.");
    CommandResolver.init();
  
    LOGGER.info("Set up injector.");
    injector = Guice.createInjector(new BlancModule(), new UserlessClientModule(), new JrcModule());
  }
  
  private static void initDb() throws SQLException {
    LOGGER.info("Initialize databases");
    long ownerId = injector.getInstance(Key.get(Long.class, Names.named("owner")));
    
    if (!UserDatabase.contains(ownerId)) {
      LOGGER.info("Owner with id {} not contained in database. Create new root user...", ownerId);
      UserValueObject owner = new UserValueObject()
            .withId(ownerId)
            .withDiscriminator(-1)
            .withName(StringUtils.EMPTY)
            .withRanks(List.of(Rank.ROOT.name()));
      
      UserDatabase.put(owner);
    }
  }
  
  private static void initJda() throws  Exception {
    LOGGER.info("Initialize JDA shards");
    JdaClient client = injector.getInstance(JdaClient.class);
  
    for (Shard shard : client.getShards()) {
      initJobs(shard);
    }

    initJobs(client);
  }
  
  private static void initJobs(JdaClient client) throws Exception {
    SubredditObservable.init(injector);
    Client reddit = injector.getInstance(Client.class);
    reddit.login(Duration.TEMPORARY);
  
    // Revoke the (permanent) access token
    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      try {
        reddit.logout();
      } catch (Exception e) {
        LOGGER.error(e.getMessage(), e);
      }
    }));
  
    Runnable job = new RedditJob(client);
    client.getShards().get(0).schedule(job, 1, TimeUnit.MINUTES);
  }
  
  private static void initJobs(Shard shard) throws Exception {
    Runnable job = new PresenceJob(shard.getPresence());
    shard.schedule(job, 1, TimeUnit.HOURS);
    
    job = new ActivityJob(shard);
    shard.schedule(job, 15, TimeUnit.MINUTES);
  }
}
