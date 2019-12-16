package vartas.discord.bot.mpi.command;

import net.dv8tion.jda.api.entities.Activity;
import org.jetbrains.annotations.NotNull;
import vartas.discord.bot.entities.Shard;
import vartas.discord.bot.mpi.MPICoreCommands;
import vartas.discord.bot.mpi.serializable.MPIStatusMessageModification;

public class MPIUpdateStatusMessage extends MPICommand<MPIStatusMessageModification> {
    @Override
    public void visit(@NotNull Shard shard) {
        shard.jda().getPresence().setActivity(Activity.playing(message.getStatusMessage()));
    }

    @Override
    protected int getCode() {
        return MPICoreCommands.MPI_UPDATE_STATUS_MESSAGE.getCode();
    }
}
