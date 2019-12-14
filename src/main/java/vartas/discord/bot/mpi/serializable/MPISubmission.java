package vartas.discord.bot.mpi.serializable;

import vartas.reddit.Submission;
import vartas.reddit.compact.CompactSubmission;

import java.io.Serializable;

public class MPISubmission extends CompactSubmission implements Serializable, MPIGuildMessage {
    private final long guildId;
    private final long channelId;

    public MPISubmission(Submission submission, long guildId, long channelId){
        this.guildId = guildId;
        this.channelId = channelId;

        this.linkFlairText = submission.getLinkFlairText().orElse(null);
        this.isNsfw = submission.isNsfw();
        this.isSpoiler = submission.isSpoiler();
        this.title = submission.getTitle();
        this.selftext = submission.getSelfText().orElse(null);
        this.thumbnail = submission.getThumbnail().orElse(null);
        this.url = submission.getUrl();
        this.author = submission.getAuthor();
        this.id = submission.getId();
        this.score = submission.getScore();
        this.subreddit = submission.getSubreddit();
        this.created = submission.getCreated();
    }

    @Override
    public long getGuildId() {
        return guildId;
    }

    @Override
    public long getChannelId() {
        return channelId;
    }
}
