package vartas.discord.bot.internal;

import vartas.discord.bot.entities.Cluster;
import vartas.discord.bot.entities.Configuration;

import javax.annotation.Nonnull;

public class RemoveSubredditFeedFromConfiguration implements Cluster.Visitor{
    private final String subredditName;
    private final long guildId;
    private final long channelId;
    public RemoveSubredditFeedFromConfiguration(String subredditName, long guildId, long channelId){
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
