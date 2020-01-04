package vartas.discord.bot.internal;

import vartas.discord.bot.entities.Cluster;
import vartas.discord.bot.entities.Shard;

import javax.annotation.Nonnull;

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
