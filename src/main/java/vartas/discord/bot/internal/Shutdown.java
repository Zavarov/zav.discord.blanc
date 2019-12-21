package vartas.discord.bot.internal;

import com.google.common.base.Preconditions;
import vartas.discord.bot.entities.Cluster;
import vartas.discord.bot.entities.Shard;

import javax.annotation.Nonnull;

public class Shutdown implements Cluster.ShardsVisitor {
    @Override
    public void visit(int shardId, @Nonnull Shard shard) throws NullPointerException{
        Preconditions.checkNotNull(shard);
        Thread thread = new Thread(shard.shutdown());
        thread.start();
    }

    @Override
    public void endVisit(@Nonnull Cluster cluster){
        cluster.shutdown();
    }
}
