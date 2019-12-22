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
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

public class AddSubredditFeedTest extends AbstractTest {
    OfflineCluster cluster;
    OfflineShard shard;
    AddSubredditFeed command;
    String subredditName = "subreddit";
    boolean visitedConfiguration;
    boolean visitedSubredditFeed;
    @Before
    public void setUp(){
        cluster = OfflineCluster.create();
        shard = OfflineShard.create(cluster);
        shard.guilds.put(guildId, new GuildImpl(null, guildId));
        cluster.registerShard(shard);
        command = new AddSubredditFeed(subredditName,guildId, channelId);

        visitedConfiguration = false;
        visitedSubredditFeed = false;
    }

    @Test
    public void visitorTest(){
        cluster.accept(command);
        cluster.accept(new Visitor());
        assertThat(visitedConfiguration).isTrue();
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
        public void visit(@Nonnull Configuration.LongType row, @Nonnull String column, @Nonnull Collection<Long> values){
            assertThat(row).isEqualTo(Configuration.LongType.SUBREDDIT);
            assertThat(column).isEqualTo(subredditName);
            assertThat(values).containsExactly(channelId);
            visitedConfiguration = true;
        }

        @Override
        public void visit(@Nonnull String subredditName, long guildId, long channelId){
            assertThat(AddSubredditFeedTest.this.subredditName).isEqualTo(subredditName);
            assertThat(AbstractTest.guildId).isEqualTo(guildId);
            assertThat(AbstractTest.channelId).isEqualTo(channelId);
            visitedSubredditFeed = true;
        }
    }
}
