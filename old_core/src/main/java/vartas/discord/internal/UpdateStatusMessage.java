package vartas.discord.internal;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.internal.utils.JDALogger;
import org.slf4j.Logger;
import vartas.discord.entities.Cluster;
import vartas.discord.entities.Status;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Nonnull
public class UpdateStatusMessage implements Cluster.Visitor{
    @Nonnull
    private final Logger log = JDALogger.getLog(this.getClass());
    @Nullable
    private String statusMessage;

    @Override
    public void visit(@Nonnull Status status){
        status.get().ifPresent(message -> {
            statusMessage = message;
            log.debug(String.format("Status message updated to '%s'", message));
        });
    }

    @Override
    public void visit(@Nonnull JDA jda) {
        //Do nothing if there is no status message
        if(statusMessage != null)
            jda.getPresence().setActivity(Activity.playing(statusMessage));
    }
}
