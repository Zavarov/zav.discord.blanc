package vartas.discord.internal;

import vartas.discord.entities.Cluster;
import vartas.discord.entities.Shard;

import javax.annotation.Nonnull;

@Nonnull
public class Shutdown implements Cluster.Visitor {
    @Override
    public void visit(@Nonnull Shard shard){
        Thread thread = new Thread(shard.shutdown());
        thread.start();
    }

    @Override
    public void endVisit(@Nonnull Cluster cluster){
        cluster.shutdown();
    }
}
