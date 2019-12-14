package vartas.discord.bot.mpi.command;

import com.google.common.base.Preconditions;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.requests.ErrorResponse;
import vartas.discord.bot.entities.Shard;
import vartas.discord.bot.message.SubmissionMessage;
import vartas.discord.bot.mpi.MPIAdapter;
import vartas.discord.bot.mpi.MPICoreCommands;
import vartas.discord.bot.mpi.serializable.MPISubmission;
import vartas.discord.bot.mpi.serializable.MPISubredditFeedModification;

import javax.annotation.Nonnull;
import java.util.NoSuchElementException;

public class MPISendSubmission extends MPICommand<MPISubmission> {
    @Override
    public void visit(@Nonnull Shard shard) throws NullPointerException, NoSuchElementException {
        Preconditions.checkNotNull(shard);
        //Handle Discord exceptions
        try {
            TextChannel channel = message.getMessageChannel(shard.jda()).orElseThrow();
            Message submissionMessage = SubmissionMessage.create(message).build();
            channel.sendMessage(submissionMessage).queue();
            //Impossible to send in this channel
        }catch(InsufficientPermissionException e){
            remove(shard);
        //TODO Ignore Discord being unavailable
        }catch(ErrorResponseException e){
            ErrorResponse response = e.getErrorResponse();
            if(response == ErrorResponse.UNKNOWN_GUILD || response == ErrorResponse.UNKNOWN_CHANNEL) {
                remove(shard);
            }
        }
    }

    private void remove(@Nonnull Shard shard){
        Preconditions.checkNotNull(shard);
        String subreddit = message.getSubreddit();
        int shardId = MPIAdapter.MPI_MASTER_NODE;
        long guildId = message.getGuildId();
        long channelId = message.getChannelId();

        MPIRemoveRedditFeedFromTextChannel command = new MPIRemoveRedditFeedFromTextChannel();
        MPISubredditFeedModification object = new MPISubredditFeedModification(subreddit, guildId, channelId);
        shard.send(shardId, command, object);
    }

    @Override
    protected int getCode() {
        return MPICoreCommands.MPI_ADD_RANK.getCode();
    }
}
