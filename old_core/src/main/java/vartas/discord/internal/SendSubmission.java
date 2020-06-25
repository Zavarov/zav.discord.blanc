package vartas.discord.internal;

import com.google.common.base.Preconditions;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.requests.ErrorResponse;
import vartas.discord.entities.Shard;
import vartas.discord.message.SubmissionMessage;
import vartas.reddit.Submission;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.NoSuchElementException;
import java.util.Optional;

@Nonnull
public class SendSubmission implements Shard.Visitor{
    private final long guildId;
    private final long channelId;
    @Nullable
    private JDA jda;
    @Nonnull
    private final Submission submission;

    public SendSubmission(@Nonnull Submission submission, long guildId, long channelId){
        this.guildId = guildId;
        this.channelId = channelId;
        this.submission = submission;
    }

    @Override
    public void visit(@Nonnull JDA jda){
        this.jda = jda;
    }

    @Override
    public void endVisit(@Nonnull Shard shard){
        Preconditions.checkNotNull(jda);
        //Handle Discord exceptions
        try {
            Guild guild = Optional.ofNullable(jda.getGuildById(guildId)).orElseThrow();
            TextChannel channel = Optional.ofNullable(guild.getTextChannelById(channelId)).orElseThrow();
            Message submissionMessage = SubmissionMessage.create(submission).build();
            shard.queue(channel.sendMessage(submissionMessage));
        //Impossible to send in this channel
        }catch(InsufficientPermissionException | NoSuchElementException e){
            shard.getCluster().accept(new RemoveSubredditFeedFromConfiguration(submission.getSubreddit(), guildId, channelId));
        }catch(ErrorResponseException e){
            ErrorResponse response = e.getErrorResponse();
            if(response == ErrorResponse.UNKNOWN_GUILD || response == ErrorResponse.UNKNOWN_CHANNEL)
                shard.getCluster().accept(new RemoveSubredditFeedFromConfiguration(submission.getSubreddit(), guildId, channelId));
        }
    }
}
