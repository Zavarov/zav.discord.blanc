package vartas.discord.bot.internal;

import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.internal.utils.JDALogger;
import org.slf4j.Logger;
import vartas.discord.bot.entities.Shard;
import vartas.discord.bot.visitor.ClusterVisitor;

import javax.annotation.Nonnull;

public class UpdateStatusMessage implements ClusterVisitor {
    private final Logger log = JDALogger.getLog(this.getClass());
    private final String statusMessage;

    public UpdateStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
        log.info(String.format("Status message changed to '%s'",statusMessage));
    }

    @Override
    public void visit(int shardId, @Nonnull Shard shard){
        shard.jda().getPresence().setActivity(Activity.playing(statusMessage));
    }
}
