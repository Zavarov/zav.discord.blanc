package vartas.discord.bot.internal;

import vartas.discord.bot.entities.Cluster;
import vartas.discord.bot.entities.Configuration;

import javax.annotation.Nonnull;

public class RemoveSubredditFeed implements Cluster.Visitor{
    private final String subredditName;
    public RemoveSubredditFeed(String subredditName){
        this.subredditName = subredditName;
    }

    @Override
    public void visit(@Nonnull Configuration configuration){
        configuration.remove(Configuration.LongType.SUBREDDIT, subredditName);
    }
}
