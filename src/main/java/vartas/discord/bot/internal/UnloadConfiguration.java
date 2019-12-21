package vartas.discord.bot.internal;

import vartas.discord.bot.EntityAdapter;
import vartas.discord.bot.RedditFeed;
import vartas.discord.bot.entities.Cluster;
import vartas.discord.bot.entities.Configuration;
import vartas.discord.bot.entities.Shard;
import vartas.discord.bot.listener.BlacklistListener;

import javax.annotation.Nonnull;

public class UnloadConfiguration implements Cluster.ClusterVisitor, Shard.Visitor {
    private final Configuration configuration;

    public UnloadConfiguration(@Nonnull Configuration configuration){
        this.configuration = configuration;
    }

    @Override
    public void visit(@Nonnull EntityAdapter adapter){
        adapter.delete(configuration);
    }

    @Override
    public void visit(@Nonnull BlacklistListener listener){
        listener.remove(configuration.getGuildId());
    }

    @Override
    public void visit(@Nonnull RedditFeed feed){
        configuration.resolve(Configuration.LongType.SUBREDDIT).forEach((subredditName, channelId) -> feed.remove(subredditName, configuration.getGuildId()));
    }
}
