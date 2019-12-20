package vartas.discord.bot.internal;

import vartas.discord.bot.EntityAdapter;
import vartas.discord.bot.RedditFeed;
import vartas.discord.bot.entities.Configuration;
import vartas.discord.bot.listener.BlacklistListener;
import vartas.discord.bot.visitor.ClusterVisitor;

import javax.annotation.Nonnull;

public class UnloadConfiguration implements ClusterVisitor {
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
    public void visit(@Nonnull String subreddit, @Nonnull RedditFeed.SubredditFeed feed){
        configuration.resolve(Configuration.LongType.SUBREDDIT).get(subreddit).forEach(channelId -> feed.remove(configuration.getGuildId(), channelId));
    }
}
