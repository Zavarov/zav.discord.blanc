package vartas.discord.bot.internal;

import com.google.common.base.Preconditions;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import vartas.discord.bot.SubredditFeed;
import vartas.discord.bot.entities.Cluster;
import vartas.discord.bot.entities.Configuration;
import vartas.discord.bot.entities.Shard;

import javax.annotation.Nonnull;

public class RemoveSubredditFeed extends Cluster.VisitorDelegator{
    private final String subredditName;
    private final long guildId;
    private final long channelId;
    private Shard shard;
    private Guild guild;
    private int shardId = -1;
    public RemoveSubredditFeed(String subredditName, long guildId, long channelId){
        //Go through the subreddit feed
        setClusterVisitor(new Cluster.ClusterVisitor());
        //Go through the configuration file that needs updating
        setShardVisitor(new Cluster.ShardVisitor());
        //Go through the JDA of the shard to get the shard id
        setShardVisitor(new Shard.ShardVisitor());
        this.subredditName = subredditName;
        this.guildId = guildId;
        this.channelId = channelId;
    }

    @Override
    public void visit(@Nonnull SubredditFeed feed){
        feed.remove(subredditName, channelId);
    }

    @Override
    public void visit(@Nonnull Cluster cluster){
        shardId = cluster.getShardId(guildId);
    }

    @Override
    public void visit(@Nonnull JDA jda){
        guild = jda.getGuildById(guildId);
    }

    @Override
    public void visit(@Nonnull Shard shard) throws IllegalArgumentException{
        Preconditions.checkArgument(shardId >= 0);
        if(shardId == shard.jda().getShardInfo().getShardId())
            this.shard = shard;
    }

    @Override
    public void endVisit(@Nonnull Cluster cluster) throws NullPointerException{
        Preconditions.checkNotNull(shard);
        Preconditions.checkNotNull(guild);
        Configuration configuration = shard.guild(guild);
        configuration.remove(Configuration.LongType.SUBREDDIT, subredditName, channelId);
    }
}
