package vartas.discord.bot.mpi.command;

import com.google.common.base.Preconditions;
import net.dv8tion.jda.api.entities.Activity;
import vartas.discord.bot.entities.Shard;
import vartas.discord.bot.mpi.MPICoreCommands;
import vartas.discord.bot.mpi.serializable.MPIStatusMessageModification;

import javax.annotation.Nonnull;

public class MPIUpdateStatusMessage extends MPICollectiveCommand<MPIStatusMessageModification> {
    @Override
    public void visit(@Nonnull Shard shard) {
        Preconditions.checkNotNull(shard);
        Preconditions.checkNotNull(message);
        shard.jda().getPresence().setActivity(Activity.playing(message.getStatusMessage()));
    }

    @Override
    protected short getCode() {
        return MPICoreCommands.MPI_UPDATE_STATUS_MESSAGE.getCode();
    }

    public static MPIReceiveCommand createReceiveCommand(){
        return new MPIUpdateStatusMessage().new MPIReceiveCommand();
    }

    public static MPISendCommand createSendCommand(){
        return new MPIUpdateStatusMessage().new MPICollectiveSendCommand();
    }
}
