package vartas.discord.bot.internal;

import com.google.common.base.Preconditions;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.internal.utils.JDALogger;
import org.slf4j.Logger;
import vartas.discord.bot.entities.Cluster;
import vartas.discord.bot.entities.Shard;
import vartas.discord.bot.entities.Status;

import javax.annotation.Nonnull;
import java.util.Optional;

public class UpdateStatusMessage extends Cluster.VisitorDelegator{
    private final Logger log = JDALogger.getLog(this.getClass());
    private String statusMessage;

    public UpdateStatusMessage(){
        setClusterVisitor(new Cluster.ClusterVisitor());
        setShardVisitor(new Cluster.ShardVisitor());
        setShardVisitor(new Shard.ShardVisitor());
    }

    @Override
    public void visit(@Nonnull Status status){
        Optional<String> messageOpt = status.get();
        messageOpt.ifPresent(message -> statusMessage = message);
    }

    @Override
    public void visit(@Nonnull JDA jda){
        Preconditions.checkNotNull(statusMessage);
        jda.getPresence().setActivity(Activity.playing(statusMessage));
        log.info(String.format("Status message updated to '%s'", statusMessage));
    }
}
