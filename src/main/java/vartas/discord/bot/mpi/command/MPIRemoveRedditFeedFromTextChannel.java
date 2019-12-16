package vartas.discord.bot.mpi.command;

import org.jetbrains.annotations.NotNull;
import vartas.discord.bot.RedditFeed;
import vartas.discord.bot.mpi.MPICoreCommands;
import vartas.discord.bot.mpi.serializable.MPISubredditFeedModification;

public class MPIRemoveRedditFeedFromTextChannel extends MPICommand<MPISubredditFeedModification> {

    @Override
    public void visit(@NotNull String subreddit, @NotNull RedditFeed.SubredditFeed subredditFeed){
        if(subreddit.equals(message.getSubredditName()))
            subredditFeed.remove(message.getGuildId(), message.getChannelId());
    }

    @Override
    protected int getCode() {
        return MPICoreCommands.MPI_REMOVE_REDDIT_FEED_FROM_TEXT_CHANNEL.getCode();
    }
}
