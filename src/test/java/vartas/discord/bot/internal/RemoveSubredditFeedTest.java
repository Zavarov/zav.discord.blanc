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
import java.util.Collection;

public class RemoveSubredditFeedTest extends AbstractTest{
    OfflineCluster cluster;
    OfflineShard shard;
    RemoveSubredditFeed command;
    String subredditName = "subreddit";
    @Before
    public void setUp(){
        cluster = OfflineCluster.create();
        shard = OfflineShard.create(cluster);
        shard.guilds.put(guildId, new GuildImpl(null, guildId));
        cluster.registerShard(shard);
        command = new RemoveSubredditFeed(subredditName,guildId, channelId);
    }

    @Test
    public void visitorTest(){
        cluster.accept(new Adder());
        cluster.accept(command);
        cluster.accept(new Visitor());
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

        @Override
        public void visit(@Nonnull Configuration configuration){
            configuration.add(Configuration.LongType.SUBREDDIT, subredditName, channelId);
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
        public void visit(@Nonnull Configuration.LongType row, @Nonnull String column, @Nonnull Collection<Long> values){
            throw new IllegalStateException("You're not supposed to be here");
        }

        @Override
        public void visit(@Nonnull String subredditName, long guildId, long channelId){
            throw new IllegalStateException("You're not supposed to be here");
        }
    }
}
