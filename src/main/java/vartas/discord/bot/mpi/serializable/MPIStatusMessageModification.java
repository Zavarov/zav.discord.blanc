package vartas.discord.bot.mpi.serializable;

import com.google.common.base.Preconditions;

import javax.annotation.Nonnull;
import java.io.Serializable;

public class MPIStatusMessageModification implements Serializable {
    private final String statusMessage;
    public MPIStatusMessageModification(@Nonnull String statusMessage) throws IllegalArgumentException{
        Preconditions.checkNotNull(statusMessage);
        this.statusMessage = statusMessage;
    }

    public String getStatusMessage(){
        return statusMessage;
    }
}
