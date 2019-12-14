package vartas.discord.bot.mpi;

import mpi.MPI;
import mpi.MPIException;
import mpi.Status;
import vartas.discord.bot.entities.Shard;
import vartas.discord.bot.mpi.command.*;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import static vartas.discord.bot.mpi.MPICoreCommands.*;

public class MPIObserver implements Runnable{
    private static final Map<Integer, MPICommand<?>> commands = new HashMap<>();

    static{
        //Rank
        registerCommand(MPI_ADD_RANK, MPIAddRank::new);
        registerCommand(MPI_REMOVE_RANK, MPIRemoveRank::new);
        registerCommand(MPI_STORE_RANK, MPIStoreRank::new);
        //Status
        registerCommand(MPI_UPDATE_STATUS_MESSAGE, MPIUpdateStatusMessage::new);
        //Reddit
        registerCommand(MPI_SEND_SUBMISSION, MPISendSubmission::new);
        registerCommand(MPI_ADD_REDDIT_FEED_TO_TEXT_CHANNEL, MPIAddRedditFeedToTextChannel::new);
        registerCommand(MPI_REMOVE_REDDIT_FEED_FROM_TEXT_CHANNEL, MPIRemoveRedditFeedFromTextChannel::new);
        //Internal
        registerCommand(MPI_SHUTDOWN, MPIShutdown::new);
    }

    public static void registerCommand(MPIStatusCode statusCode, Supplier<MPICommand<?>> commandSupplier){
        commands.put(statusCode.getCode(), commandSupplier.get());
    }

    protected final Shard shard;

    public MPIObserver(Shard shard){
        this.shard = shard;
    }

    @Override
    public void run() {
        try {
            Status status = MPI.COMM_WORLD.iProbe(MPI.ANY_SOURCE, MPI.ANY_TAG);
            //Incoming message detected
            if (status != null) {
                commands.get(status.getTag()).accept(shard);
            }
        }catch(MPIException e){
            //TODO Notify the root
            e.printStackTrace();
        }
    }
}
