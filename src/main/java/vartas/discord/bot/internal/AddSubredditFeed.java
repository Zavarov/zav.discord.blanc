package vartas.discord.bot.internal;

import vartas.discord.bot.RedditFeed;
import vartas.discord.bot.entities.Cluster;
import vartas.discord.bot.entities.Configuration;

import javax.annotation.Nonnull;

public class AddSubredditFeed implements Cluster.ClusterVisitor, Cluster.ShardsVisitor {
    private final String subredditName;
    private final long guildId;
    private final long channelId;
    public AddSubredditFeed(String subredditName, long guildId, long channelId){
        this.subredditName = subredditName;
        this.guildId = guildId;
        this.channelId = channelId;
    }

    @Override
    public void visit(@Nonnull RedditFeed feed){
        feed.add(subredditName, channelId, guildId);
    }

    @Override
    public void visit(@Nonnull Configuration configuration){
        if(configuration.getGuildId() == guildId)
            configuration.add(Configuration.LongType.SUBREDDIT, subredditName, channelId);
    }

    @Override
    public void traverse(@Nonnull Cluster cluster){
        Cluster.ClusterVisitor.super.traverse(cluster);
        Cluster.ShardsVisitor.super.traverse(cluster);
    }
}
