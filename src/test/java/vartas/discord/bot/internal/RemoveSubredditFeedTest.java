package vartas.discord.bot.internal;

import net.dv8tion.jda.internal.entities.GuildImpl;
import org.junit.Before;
import org.junit.Test;
import vartas.discord.bot.AbstractTest;
import vartas.discord.bot.entities.Cluster;
import vartas.discord.bot.entities.Configuration;
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
        command = new RemoveSubredditFeed(subredditName);
    }

    @Test
    public void visitorTest(){
        cluster.accept(new Adder());
        cluster.accept(command);
        cluster.accept(new Visitor());
    }

    private class Adder implements Cluster.Visitor{
        @Override
        public void visit(@Nonnull Configuration configuration){
            configuration.add(Configuration.LongType.SUBREDDIT, subredditName, channelId);
        }
    }

    private static class Visitor implements Cluster.Visitor{
        @Override
        public void visit(@Nonnull Configuration.LongType row, @Nonnull String column, @Nonnull Collection<Long> values){
            throw new IllegalStateException("You're not supposed to be here");
        }
    }
}
