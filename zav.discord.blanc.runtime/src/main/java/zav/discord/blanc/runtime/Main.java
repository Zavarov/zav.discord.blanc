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
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import net.dv8tion.jda.api.JDA;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import zav.discord.blanc.api.Client;
import zav.discord.blanc.api.Rank;
import zav.discord.blanc.databind.UserEntity;
import zav.discord.blanc.db.UserTable;
import zav.discord.blanc.reddit.RedditJob;
import zav.discord.blanc.runtime.internal.BlancModule;
import zav.discord.blanc.runtime.internal.CommandResolver;
import zav.discord.blanc.runtime.internal.RedditUtils;
import zav.discord.blanc.runtime.job.PresenceJob;
import zav.jrc.client.FailedRequestException;
import zav.jrc.client.guice.UserlessClientModule;

public class Main {
  private static final Logger LOGGER = LogManager.getLogger(Main.class);
  private static Injector injector;
  
  public static void main(String[] args) throws Exception {
    setUp();
    
    initDb();
    
    initReddit();
    
    initJda();
  }
  
  private static void setUp() {
    LOGGER.info("Set up commands.");
    CommandResolver.init();
  
    LOGGER.info("Set up injector.");
    injector = Guice.createInjector(new BlancModule(null), new UserlessClientModule());
  }
  
  private static void initDb() throws SQLException {
    LOGGER.info("Initialize databases");
    long ownerId = injector.getInstance(Key.get(Long.class, Names.named("owner")));
    UserTable db = injector.getInstance(UserTable.class);
    
    if (!db.contains(ownerId)) {
      LOGGER.info("Owner with id {} not contained in database. Create new root user...", ownerId);
      UserEntity owner = new UserEntity()
            .withId(ownerId)
            .withDiscriminator(StringUtils.EMPTY)
            .withName(StringUtils.EMPTY)
            .withRanks(List.of(Rank.ROOT.name()));
      
      db.put(owner);
    }
  }
  
  private static void initReddit() throws FailedRequestException {
    RedditUtils.init(injector);
  }
  
  private static void initJda() throws Exception {
    LOGGER.info("Initialize JDA shards");
    Client client = injector.getInstance(Client.class);
  
    for (JDA shard : client.getShards()) {
      initJobs(shard);
    }

    initJobs();
  }
  
  private static void initJobs() {
    ScheduledExecutorService executor = injector.getInstance(ScheduledExecutorService.class);
    Runnable job = injector.getInstance(RedditJob.class);
    executor.schedule(job, 1, TimeUnit.MINUTES);
  }
  
  private static void initJobs(JDA shard) throws Exception {
    ScheduledExecutorService executor = injector.getInstance(ScheduledExecutorService.class);
    Runnable job = new PresenceJob(shard.getPresence());
    executor.schedule(job, 1, TimeUnit.HOURS);
  }
}
