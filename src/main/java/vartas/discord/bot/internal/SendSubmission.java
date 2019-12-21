package vartas.discord.bot.internal;

import com.google.common.base.Preconditions;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.requests.ErrorResponse;
import vartas.discord.bot.entities.Cluster;
import vartas.discord.bot.entities.Shard;
import vartas.discord.bot.message.SubmissionMessage;
import vartas.reddit.Submission;

import javax.annotation.Nonnull;
import java.util.NoSuchElementException;
import java.util.Optional;

public class SendSubmission implements Cluster.ShardsVisitor {
    private final int shardId;
    private final long guildId;
    private final long channelId;
    private final Submission submission;

    public SendSubmission(int shardId, long guildId, long channelId, Submission submission){
        this.shardId = shardId;
        this.guildId = guildId;
        this.channelId = channelId;
        this.submission = submission;
    }

    @Override
    public void visit(int shardId, @Nonnull Shard shard) throws NullPointerException {
        Preconditions.checkNotNull(shard);
        //Abort if we're in the wrong shard
        if(this.shardId != shardId)
            return;

        //Handle Discord exceptions
        try {
            Guild guild = Optional.ofNullable(shard.jda().getGuildById(guildId)).orElseThrow();
            TextChannel channel = Optional.ofNullable(guild.getTextChannelById(channelId)).orElseThrow();
            Message submissionMessage = SubmissionMessage.create(submission).build();
            shard.queue(channel.sendMessage(submissionMessage));
        //Impossible to send in this channel
        }catch(InsufficientPermissionException | NoSuchElementException e){
            shard.getCluster().accept(new RemoveSubredditFeed(submission.getSubreddit(), guildId, channelId));
            //TODO Ignore Discord being unavailable
        }catch(ErrorResponseException e){
            ErrorResponse response = e.getErrorResponse();
            if(response == ErrorResponse.UNKNOWN_GUILD || response == ErrorResponse.UNKNOWN_CHANNEL)
                shard.getCluster().accept(new RemoveSubredditFeed(submission.getSubreddit(), guildId, channelId));
        }
    }
}
