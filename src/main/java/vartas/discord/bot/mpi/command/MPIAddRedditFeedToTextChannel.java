package vartas.discord.bot.mpi.command;

import com.google.common.base.Preconditions;
import vartas.discord.bot.RedditFeed;
import vartas.discord.bot.mpi.MPICoreCommands;
import vartas.discord.bot.mpi.serializable.MPISubredditFeedModification;

import javax.annotation.Nonnull;

public class MPIAddRedditFeedToTextChannel extends MPIPointToPointCommand<MPISubredditFeedModification> {
    @Override
    public void visit(@Nonnull String subreddit, @Nonnull RedditFeed.SubredditFeed subredditFeed) throws NullPointerException{
        Preconditions.checkNotNull(message);
        if(subreddit.equals(message.getSubredditName())) {
            subredditFeed.add(message.getGuildId(), message.getChannelId());
        }
    }

    @Override
    protected int getCode() {
        return MPICoreCommands.MPI_ADD_REDDIT_FEED_TO_TEXT_CHANNEL.getCode();
    }

    public static MPIReceiveCommand createReceiveCommand(){
        return new MPIAddRedditFeedToTextChannel().new MPIReceiveCommand();
    }

    public static MPISendCommand createSendCommand(){
        return new MPIAddRedditFeedToTextChannel().new MPIPointToPointSendCommand();
    }
}
