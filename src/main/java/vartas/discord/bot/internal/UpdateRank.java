package vartas.discord.bot.internal;

import com.google.common.base.Preconditions;
import vartas.discord.bot.entities.Rank;
import vartas.discord.bot.entities.Shard;
import vartas.discord.bot.visitor.ClusterVisitor;

import javax.annotation.Nonnull;

public class UpdateRank implements ClusterVisitor {
    private Rank rank;

    @Override
    public void visit(@Nonnull Rank rank){
        this.rank = rank;
    }

    @Override
    public void visit(int shardId, @Nonnull Shard shard) throws NullPointerException{
        Preconditions.checkNotNull(shard);
        Preconditions.checkNotNull(rank);
        if(shardId == 0)
            shard.store(rank);
    }
}
