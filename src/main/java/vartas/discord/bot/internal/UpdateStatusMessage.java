package vartas.discord.bot.internal;

import com.google.common.base.Preconditions;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.internal.utils.JDALogger;
import org.slf4j.Logger;
import vartas.discord.bot.entities.Cluster;
import vartas.discord.bot.entities.Shard;
import vartas.discord.bot.entities.Status;

import javax.annotation.Nonnull;
import java.util.Optional;

public class UpdateStatusMessage implements Cluster.ShardsVisitor, Cluster.ClusterVisitor {
    private final Logger log = JDALogger.getLog(this.getClass());
    private String statusMessage;

    @Override
    public void visit(@Nonnull Status status){
        Optional<String> messageOpt = status.get();
        messageOpt.ifPresent(message -> statusMessage = message);
    }

    //Don't traverse all subnodes to minimize the overhead
    @Override
    public void traverse(int shardId, @Nonnull Shard shard){
        Preconditions.checkNotNull(statusMessage);
        shard.jda().getPresence().setActivity(Activity.playing(statusMessage));
        log.info(String.format("Status message updated to '%s'", statusMessage));
    }
    @Override
    public void traverse(@Nonnull Cluster cluster){
        Cluster.ClusterVisitor.super.traverse(cluster);
        Cluster.ShardsVisitor.super.traverse(cluster);
    }
}
