package vartas.discord.bot.internal;

import vartas.discord.bot.RedditFeed;
import vartas.discord.bot.entities.Configuration;
import vartas.discord.bot.listener.BlacklistListener;
import vartas.discord.bot.visitor.ClusterVisitor;

import javax.annotation.Nonnull;

public class LoadConfiguration implements ClusterVisitor {
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
        configuration.resolve(Configuration.LongType.SUBREDDIT).keySet().forEach(feed::add);
    }

    @Override
    public void visit(@Nonnull String subreddit, @Nonnull RedditFeed.SubredditFeed feed){
        configuration.resolve(Configuration.LongType.SUBREDDIT).get(subreddit).forEach(channelId -> feed.add(configuration.getGuildId(), channelId));
    }
}
