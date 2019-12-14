package vartas.discord.bot.mpi.serializable;

import java.io.Serializable;

public class MPISubredditFeedModification implements Serializable {
    private final String subredditName;
    private final long guildId;
    private final long channelId;

    public MPISubredditFeedModification(String subredditName, long guildId, long channelId){
        this.subredditName = subredditName;
        this.guildId = guildId;
        this.channelId = channelId;
    }


    public String getSubredditName(){
        return subredditName;
    }

    public long getGuildId(){
        return guildId;
    }

    public long getChannelId(){
        return channelId;
    }
}
