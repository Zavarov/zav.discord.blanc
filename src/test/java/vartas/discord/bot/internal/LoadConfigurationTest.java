package vartas.discord.bot.internal;

import net.dv8tion.jda.internal.entities.GuildImpl;
import org.junit.Before;
import org.junit.Test;
import vartas.discord.bot.AbstractTest;
import vartas.discord.bot.entities.Cluster;
import vartas.discord.bot.entities.Configuration;
import vartas.discord.bot.entities.Shard;
import vartas.discord.bot.entities.offline.OfflineCluster;
import vartas.discord.bot.entities.offline.OfflineShard;

import javax.annotation.Nonnull;

import static org.assertj.core.api.Assertions.assertThat;

public class LoadConfigurationTest extends AbstractTest {
    OfflineCluster cluster;
    OfflineShard shard;
    Configuration configuration;
    LoadConfiguration command;
    String subredditName = "subreddit";
    boolean visitedSubredditFeed;
    @Before
    public void setUp(){
        cluster = OfflineCluster.create();
        shard = OfflineShard.create(cluster);
        configuration = new Configuration(guildId);

        shard.guilds.put(guildId, new GuildImpl(null, guildId));
        cluster.registerShard(shard);
        configuration.add(Configuration.LongType.SUBREDDIT, subredditName, channelId);

        command = new LoadConfiguration(shard, configuration);

        visitedSubredditFeed = false;
    }

    @Test
    public void visitorTest(){
        cluster.accept(command);
        cluster.accept(new Visitor());
        assertThat(visitedSubredditFeed).isTrue();
    }

    private class Visitor extends Cluster.VisitorDelegator{
        public Visitor(){
            setConfigurationVisitor(new Shard.ConfigurationVisitor());
            setShardVisitor(new Shard.ShardVisitor());
            setShardVisitor(new Cluster.ShardVisitor());
            setClusterVisitor(new Cluster.ClusterVisitor());
        }

        @Override
        public void visit(@Nonnull String subredditName, long guildId, long channelId){
            assertThat(LoadConfigurationTest.this.subredditName).isEqualTo(subredditName);
            assertThat(AbstractTest.guildId).isEqualTo(guildId);
            assertThat(AbstractTest.channelId).isEqualTo(channelId);
            visitedSubredditFeed = true;
        }
    }
}
