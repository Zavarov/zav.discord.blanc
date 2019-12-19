package vartas.discord.bot.internal;

import com.google.common.base.Preconditions;
import vartas.discord.bot.entities.Shard;
import vartas.discord.bot.visitor.ClusterVisitor;

import javax.annotation.Nonnull;

public class Shutdown implements ClusterVisitor {
    @Override
    public void visit(int shardId, @Nonnull Shard shard) throws NullPointerException{
        Preconditions.checkNotNull(shard);
        Thread thread = new Thread(shard.shutdown());
        thread.start();
    }
}
