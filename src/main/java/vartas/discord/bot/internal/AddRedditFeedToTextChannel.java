package vartas.discord.bot.internal;

import vartas.discord.bot.RedditFeed;
import vartas.discord.bot.visitor.ClusterVisitor;

import javax.annotation.Nonnull;

public class AddRedditFeedToTextChannel implements ClusterVisitor {
    private final String subredditName;
    private final long guildId;
    private final long channelId;

    public AddRedditFeedToTextChannel(String subredditName, long guildId, long channelId){
        this.subredditName = subredditName;
        this.guildId = guildId;
        this.channelId = channelId;
    }

    @Override
    public void visit(@Nonnull String subreddit, @Nonnull RedditFeed.SubredditFeed subredditFeed) throws NullPointerException{
        if(this.subredditName.equals(subreddit)) {
            subredditFeed.add(guildId, channelId);
        }
    }
}
