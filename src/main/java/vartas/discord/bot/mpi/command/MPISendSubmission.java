package vartas.discord.bot.mpi.command;

import com.google.common.base.Preconditions;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.requests.ErrorResponse;
import vartas.discord.bot.entities.Shard;
import vartas.discord.bot.message.SubmissionMessage;
import vartas.discord.bot.mpi.MPICoreCommands;
import vartas.discord.bot.mpi.serializable.MPISubmission;
import vartas.discord.bot.mpi.serializable.MPISubredditFeedModification;

import javax.annotation.Nonnull;
import java.util.NoSuchElementException;

public class MPISendSubmission extends MPIPointToPointCommand<MPISubmission> {
    @Override
    public void visit(@Nonnull Shard shard) throws NullPointerException, NoSuchElementException {
        Preconditions.checkNotNull(shard);
        Preconditions.checkNotNull(message);
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
        Preconditions.checkNotNull(message);
        String subreddit = message.getSubreddit();
        long guildId = message.getGuildId();
        long channelId = message.getChannelId();

        MPIRemoveRedditFeedFromTextChannel.MPISendCommand command = MPIRemoveRedditFeedFromTextChannel.createSendCommand();
        MPISubredditFeedModification object = new MPISubredditFeedModification(subreddit, guildId, channelId);
        shard.send(command, object);
    }

    @Override
    protected int getCode() {
        return MPICoreCommands.MPI_SEND_SUBMISSION.getCode();
    }

    public static MPIReceiveCommand createReceiveCommand(){
        return new MPISendSubmission().new MPIReceiveCommand();
    }

    public static MPISendCommand createSendCommand(int targetNode){
        return new MPISendSubmission().new MPIPointToPointSendCommand(targetNode);
    }
}
