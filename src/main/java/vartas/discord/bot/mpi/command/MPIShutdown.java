package vartas.discord.bot.mpi.command;

import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;
import vartas.discord.bot.entities.Shard;
import vartas.discord.bot.mpi.MPICoreCommands;
import vartas.discord.bot.mpi.serializable.MPIVoid;

import javax.annotation.Nonnull;

public class MPIShutdown extends MPICommand<MPIVoid> {
    @Override
    public void visit(@NotNull @Nonnull Shard shard) throws NullPointerException{
        Preconditions.checkNotNull(shard);
        //TODO
        //shard.shutdown();
    }

    @Override
    protected int getCode() {
        return MPICoreCommands.MPI_SHUTDOWN.getCode();
    }
}
