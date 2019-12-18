package vartas.discord.bot.mpi.command;

import com.google.common.base.Preconditions;
import vartas.discord.bot.RedditFeed;
import vartas.discord.bot.mpi.MPICoreCommands;
import vartas.discord.bot.mpi.serializable.MPISubredditFeedModification;

import javax.annotation.Nonnull;

public class MPIRemoveRedditFeedFromTextChannel extends MPIPointToPointCommand<MPISubredditFeedModification> {
    @Override
    public void visit(@Nonnull String subreddit, @Nonnull RedditFeed.SubredditFeed subredditFeed) throws NullPointerException{
        Preconditions.checkNotNull(message);
        if(subreddit.equals(message.getSubredditName()))
            subredditFeed.remove(message.getGuildId(), message.getChannelId());
    }

    @Override
    protected int getCode() {
        return MPICoreCommands.MPI_REMOVE_REDDIT_FEED_FROM_TEXT_CHANNEL.getCode();
    }

    public static MPIReceiveCommand createReceiveCommand(){
        return new MPIRemoveRedditFeedFromTextChannel().new MPIReceiveCommand();
    }

    public static MPISendCommand createSendCommand(){
        return new MPIRemoveRedditFeedFromTextChannel().new MPIPointToPointSendCommand();
    }
}
