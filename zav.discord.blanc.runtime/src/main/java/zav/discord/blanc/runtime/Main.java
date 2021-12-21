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
import zav.discord.blanc.api.Shard;
import zav.discord.blanc.command.Rank;
import zav.discord.blanc.databind.UserValueObject;
import zav.discord.blanc.db.GuildTable;
import zav.discord.blanc.db.RoleTable;
import zav.discord.blanc.db.TextChannelTable;
import zav.discord.blanc.db.UserTable;
import zav.discord.blanc.db.WebHookTable;
import zav.discord.blanc.jda.JdaShardSupplier;
import zav.discord.blanc.runtime.internal.CommandResolver;
import zav.discord.blanc.runtime.internal.guice.BlancModule;
import zav.discord.blanc.runtime.job.PresenceJob;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Main {
  private static final Logger LOGGER = LogManager.getLogger(Main.class);
  private static Injector injector;
  
  public static void main(String[] args) throws SQLException {
    setUp();
    
    initDb();
    
    initJda();
  }
  
  private static void setUp() throws SQLException {
    LOGGER.info("Set up databases.");
    GuildTable.create();
    RoleTable.create();
    TextChannelTable.create();
    WebHookTable.create();
    UserTable.create();
  
    LOGGER.info("Set up commands.");
    CommandResolver.init();
  
    LOGGER.info("Set up injector.");
    injector = Guice.createInjector(new BlancModule());
  }
  
  private static void initDb() throws SQLException {
    LOGGER.info("Initialize databases");
    long ownerId = injector.getInstance(Key.get(Long.class, Names.named("owner")));
    
    if (!UserTable.contains(ownerId)) {
      LOGGER.info("Owner with id {} not contained in database. Create new root user...", ownerId);
      UserValueObject owner = new UserValueObject()
            .withId(ownerId)
            .withDiscriminator(-1)
            .withName(StringUtils.EMPTY)
            .withRanks(List.of(Rank.ROOT.name()));
      
      UserTable.put(owner);
    }
  }
  
  private static void initJda() {
    LOGGER.info("Initialize JDA shards");
    JdaShardSupplier supplier = injector.getInstance(JdaShardSupplier.class);
  
    while (supplier.hasNext()) {
      Shard shard = supplier.next();
  
      initJobs(shard);
    }
  }
  
  private static void initJobs(Shard shard) {
    try {
      PresenceJob job = new PresenceJob(shard.getPresence());
      shard.schedule(job, 1, TimeUnit.HOURS);
      
    } catch(Exception e) {
      throw new RuntimeException(e);
    }
  }
  
    /*
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
    public static zav.jra.Client REDDIT_CLIENT;
    @Nonnull
    public static final RedditObservable REDDIT_OBSERVABLE = new RedditObservable();
    @Nonnull
    public static final Client CLIENT = new Client();
    @Nonnull
    private static final Parser PARSER = new MontiCoreCommandParser();

    static{
        //The application would terminate on an invalid command, for example
        Log.enableFailQuick(false);
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        initJsonFiles();
        initRedditClient();
        initDiscordShards();

        LOGGER.info("Ready to serve, my lord!");
    }

    //#################################################################################################################//
    //                                                                                                                 //
    //    Initialize JSON Files                                                                                        //
    //                                                                                                                 //
    //#################################################################################################################//

    private static void initJsonFiles() throws IOException {
        LOGGER.info("Load credentials.json.");
        JSONCredentials.fromJson(JSONCredentials.CREDENTIALS, Paths.get("credentials.json"));
        LOGGER.info("Load ranks.json.");
        JSONRanks.fromJson(JSONRanks.RANKS, Paths.get("ranks.json"));
        LOGGER.info("Load status.json.");
        JSONStatusMessages.fromJson(JSONStatusMessages.STATUS_MESSAGES, Paths.get("status.json"));
    }

    //#################################################################################################################//
    //                                                                                                                 //
    //    Initialize Reddit Client                                                                                     //
    //                                                                                                                 //
    //#################################################################################################################//

    private static void initRedditClient() throws IOException, InterruptedException {
        LOGGER.info("Initialize Reddit client.");
        REDDIT_CLIENT = createRedditClient();
        REDDIT_CLIENT.login();
    }

    private static UserlessClient createRedditClient(){
        final UserAgent userAgent = createUserAgent();
        final String id = JSONCredentials.CREDENTIALS.getRedditId();
        final String secret = JSONCredentials.CREDENTIALS.getRedditSecret();

        return new UserlessClient(userAgent, id, secret);
    }

    private static UserAgent createUserAgent(){
        final String platform = "linux";
        final String name = JSONCredentials.CREDENTIALS.getBotName();
        final String version = JSONCredentials.CREDENTIALS.getVersion();
        final String account = JSONCredentials.CREDENTIALS.getRedditAccount();

        return UserAgentFactory.create(platform, name, version, account);
    }

    //#################################################################################################################//
    //                                                                                                                 //
    //    Initialize Discord Shards                                                                                    //
    //                                                                                                                 //
    //#################################################################################################################//

    private static void initDiscordShards(){
        LOGGER.info("Initialize Discord shards.");
        final ShardLoader shardLoader = createShardLoader();

        for(int id = 0 ; id < JSONCredentials.CREDENTIALS.getShardCount() ; ++id)
            CLIENT.addShards(loadShard(shardLoader, id));
    }

    private static ShardLoader createShardLoader(){
        return new JDAShardLoader(JSONCredentials.CREDENTIALS, Main::createCommandBuilder);
    }

    private static CommandBuilder createCommandBuilder(Shard shard, JDA jda){
        return new MontiCoreCommandBuilder(
                (guild, textChannel) -> new JDATypeResolver(shard, jda, guild, textChannel),
                shard,
                PARSER,
                JSONCredentials.CREDENTIALS.getGlobalPrefix()
        );
    }

    private static Shard loadShard(ShardLoader shardLoader, int shardId){
        LOGGER.info("Initialize shard {}.", shardId);
        Shard shard = shardLoader.load(shardId);

        setUpActivityFeed(shard);
        setUpRedditFeed(shard);
        setUpStatusMessages(shard);

        return shard;
    }

    private static void setUpActivityFeed(Shard shard){
        ActivityRunnable runnable = new ActivityRunnable(shard);

        shard.submit(runnable, 15, 15, TimeUnit.MINUTES);
    }

    private static void setUpRedditFeed(Shard shard){
        RedditVisitor visitor = new RedditVisitor(REDDIT_OBSERVABLE, REDDIT_CLIENT);
        shard.accept(visitor);

        //The Reddit client is shared among all shards, so the runnable has to be submitted only once
        if (shard.getId() == 0) {
            RedditRunnable runnable = new RedditRunnable(REDDIT_OBSERVABLE);
            shard.submit(runnable, 5, 5, TimeUnit.MINUTES);
        }
    }

    private static void setUpStatusMessages(Shard shard) {
        StatusMessageRunnable runnable = new StatusMessageRunnable(shard.retrieveSelfUser());

        shard.submit(runnable, 0, 5, TimeUnit.MINUTES);
    }
     */
}
