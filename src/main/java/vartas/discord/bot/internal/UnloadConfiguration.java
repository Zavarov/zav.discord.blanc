package vartas.discord.bot.internal;

import vartas.discord.bot.EntityAdapter;
import vartas.discord.bot.SubredditFeed;
import vartas.discord.bot.entities.Cluster;
import vartas.discord.bot.entities.Configuration;
import vartas.discord.bot.entities.Shard;

import javax.annotation.Nonnull;

public class UnloadConfiguration extends Cluster.VisitorDelegator {
    private final Shard shard;
    private final Configuration configuration;

    public UnloadConfiguration(@Nonnull Shard shard, @Nonnull Configuration configuration){
        setClusterVisitor(new Cluster.ClusterVisitor());
        setShardVisitor(new Cluster.ShardVisitor());
        setShardVisitor(new Shard.ShardVisitor());
        this.shard = shard;
        this.configuration = configuration;
    }

    @Override
    public void handle(@Nonnull Shard shard){
        if(this.shard == shard)
            super.handle(shard);
    }

    @Override
    public void visit(@Nonnull EntityAdapter adapter){
        adapter.delete(configuration);
    }

    @Override
    public void visit(@Nonnull SubredditFeed feed){
        configuration.resolve(Configuration.LongType.SUBREDDIT).forEach(feed::remove);
    }
}
