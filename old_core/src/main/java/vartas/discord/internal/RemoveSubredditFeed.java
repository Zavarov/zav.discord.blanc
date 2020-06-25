package vartas.discord.internal;

import vartas.discord.entities.Cluster;
import vartas.discord.entities.Configuration;

import javax.annotation.Nonnull;

@Nonnull
public class RemoveSubredditFeed implements Cluster.Visitor{
    @Nonnull
    private final String subredditName;
    public RemoveSubredditFeed(@Nonnull String subredditName){
        this.subredditName = subredditName;
    }

    @Override
    public void visit(@Nonnull Configuration configuration){
        configuration.remove(Configuration.LongType.SUBREDDIT, subredditName);
    }
}
