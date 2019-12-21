package vartas.discord.bot.internal;

import vartas.discord.bot.RedditFeed;
import vartas.discord.bot.entities.Cluster;
import vartas.discord.bot.entities.Configuration;
import vartas.discord.bot.entities.Shard;
import vartas.discord.bot.listener.BlacklistListener;

import javax.annotation.Nonnull;

public class LoadConfiguration implements Cluster.ClusterVisitor, Shard.Visitor {
    private final Configuration configuration;

    public LoadConfiguration(@Nonnull Configuration configuration){
        this.configuration = configuration;
    }

    @Override
    public void visit(@Nonnull BlacklistListener listener){
        listener.set(configuration);
    }

    @Override
    public void visit(@Nonnull RedditFeed feed){
        configuration.resolve(Configuration.LongType.SUBREDDIT).forEach((subredditName, channelId) -> feed.add(subredditName, configuration.getGuildId(), channelId));
    }
}
