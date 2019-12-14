package vartas.discord.bot.mpi.command;

import com.google.common.base.Preconditions;
import vartas.discord.bot.entities.Shard;
import vartas.discord.bot.mpi.MPICoreCommands;
import vartas.discord.bot.mpi.serializable.MPIRankModification;

import javax.annotation.Nonnull;

public class MPIShutdown extends MPICommand<MPIRankModification> {
    @Override
    public void visit(@Nonnull Shard shard) throws NullPointerException{
        Preconditions.checkNotNull(shard);
        //TODO
        //shard.shutdown();
    }

    @Override
    protected int getCode() {
        return MPICoreCommands.MPI_SHUTDOWN.getCode();
    }
}
