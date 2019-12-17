package vartas.discord.bot.mpi.command;

import com.google.common.base.Preconditions;
import mpi.MPIException;
import vartas.discord.bot.entities.Cluster;
import vartas.discord.bot.entities.Shard;
import vartas.discord.bot.mpi.MPICoreCommands;
import vartas.discord.bot.mpi.serializable.MPIVoid;

import javax.annotation.Nonnull;

public class MPIShutdown extends MPICommand<MPIVoid> {
    @Override
    public void visit(@Nonnull Shard shard) throws NullPointerException{
        Preconditions.checkNotNull(shard);
        try {
            shard.getCluster().ifPresent(Cluster::shutdown);
            shard.shutdown();
        }catch(MPIException e){
            //TODO
            e.printStackTrace();
        }
    }

    @Override
    protected int getCode() {
        return MPICoreCommands.MPI_SHUTDOWN.getCode();
    }
}
