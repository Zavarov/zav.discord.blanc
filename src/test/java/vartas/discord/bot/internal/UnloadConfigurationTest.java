package vartas.discord.bot.internal;

import net.dv8tion.jda.internal.entities.GuildImpl;
import org.junit.Before;
import org.junit.Test;
import vartas.discord.bot.AbstractTest;
import vartas.discord.bot.SubredditFeed;
import vartas.discord.bot.entities.Cluster;
import vartas.discord.bot.entities.Configuration;
import vartas.discord.bot.entities.Shard;
import vartas.discord.bot.entities.offline.OfflineCluster;
import vartas.discord.bot.entities.offline.OfflineShard;

import javax.annotation.Nonnull;

import static org.assertj.core.api.Assertions.assertThat;

public class UnloadConfigurationTest extends AbstractTest {
    OfflineCluster cluster;
    OfflineShard shard;
    Configuration configuration;
    UnloadConfiguration command;
    String subredditName = "subreddit";
    @Before
    public void setUp(){
        cluster = OfflineCluster.create();
        shard = OfflineShard.create(cluster);
        configuration = new Configuration(guildId);

        shard.guilds.put(guildId, new GuildImpl(null, guildId));
        cluster.registerShard(shard);
        configuration.add(Configuration.LongType.SUBREDDIT, subredditName, channelId);

        command = new UnloadConfiguration(shard, configuration);
    }

    @Test
    public void visitorTest(){
        cluster.accept(new Adder());
        cluster.accept(command);
        cluster.accept(new Visitor());
        assertThat(shard.removed.contains(guildId));
    }

    private class Adder extends Cluster.VisitorDelegator{
        public Adder(){
            setConfigurationVisitor(new Shard.ConfigurationVisitor());
            setShardVisitor(new Shard.ShardVisitor());
            setShardVisitor(new Cluster.ShardVisitor());
            setClusterVisitor(new Cluster.ClusterVisitor());
        }

        @Override
        public void visit(@Nonnull SubredditFeed feed){
            feed.add(subredditName, guildId, channelId);
        }
    }

    private static class Visitor extends Cluster.VisitorDelegator{
        public Visitor(){
            setConfigurationVisitor(new Shard.ConfigurationVisitor());
            setShardVisitor(new Shard.ShardVisitor());
            setShardVisitor(new Cluster.ShardVisitor());
            setClusterVisitor(new Cluster.ClusterVisitor());
        }

        @Override
        public void visit(@Nonnull String subredditName, long guildId, long channelId){
            throw new IllegalStateException("You're not supposed to be here");
        }
    }
}
