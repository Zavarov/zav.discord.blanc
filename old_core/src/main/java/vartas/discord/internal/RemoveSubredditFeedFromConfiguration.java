package vartas.discord.internal;

import vartas.discord.entities.Cluster;
import vartas.discord.entities.Configuration;

import javax.annotation.Nonnull;

@Nonnull
public class RemoveSubredditFeedFromConfiguration implements Cluster.Visitor{
    @Nonnull
    private final String subredditName;
    private final long guildId;
    private final long channelId;
    public RemoveSubredditFeedFromConfiguration(@Nonnull String subredditName, long guildId, long channelId){
        this.subredditName = subredditName;
        this.guildId = guildId;
        this.channelId = channelId;
    }

    @Override
    public void visit(@Nonnull Configuration configuration){
        if(configuration.getGuildId() == guildId)
            configuration.remove(Configuration.LongType.SUBREDDIT, subredditName, channelId);
    }
}
