package vartas.discord.bot.mpi;

import com.google.common.base.Preconditions;
import mpi.MPI;
import mpi.MPIException;
import vartas.discord.bot.mpi.command.MPICommand;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public abstract class MPIAdapter {
    /**
     * The executor for all MPI commands. One {@link Runnable} is frequently checking for incoming messages without
     * blocking, while any other {@link Runnable} may be used to send messages.<br>
     * It is important that at each time, only a single thread makes MPI calls.
     */
    @Nonnull
    protected ScheduledExecutorService mpi = Executors.newSingleThreadScheduledExecutor();
    public static final int MPI_MASTER_NODE = 0;

    protected int myRank;
    protected int numProcs;

    public MPIAdapter(@Nonnull String[] args) throws MPIException, NullPointerException {
        Preconditions.checkNotNull(args);
        int level = MPI.InitThread(args, MPI.THREAD_SERIALIZED);
        Preconditions.checkArgument(level == MPI.THREAD_SERIALIZED);

        this.myRank = MPI.COMM_WORLD.getRank();
        this.numProcs = MPI.COMM_WORLD.getSize();
    }

    public int getShardId(long guildId){
        return (int)((guildId >> 22) % numProcs);
    }

    public abstract <T extends Serializable> void send(MPICommand<T>.MPISendCommand command, T object);
}
