package vartas.discord.bot.mpi;

import mpi.MPI;
import mpi.MPIException;
import mpi.Status;
import vartas.discord.bot.entities.Shard;
import vartas.discord.bot.mpi.command.*;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import static vartas.discord.bot.mpi.MPICoreCommands.*;

public class MPIObserver implements Runnable{
    /**
     * This map associates each code with a command.
     * Every time a message is received, its status code is retrieved and the matching command is executed.
     */
    private static final Map<Integer, Function<Integer, MPICommand<?>.MPIReceiveCommand>> commands = new HashMap<>();

    static{
        //P2P Commands
        registerCommand(MPI_SEND_SUBMISSION, MPISendSubmission::createReceiveCommand);
        registerCommand(MPI_ADD_REDDIT_FEED_TO_TEXT_CHANNEL, MPIAddRedditFeedToTextChannel::createReceiveCommand);
        registerCommand(MPI_REMOVE_REDDIT_FEED_FROM_TEXT_CHANNEL, MPIRemoveRedditFeedFromTextChannel::createReceiveCommand);
        //Collective Commands
        registerCommand(MPU_UPDATE_RANK, MPIUpdateRank::createReceiveCommand);
        registerCommand(MPI_UPDATE_STATUS_MESSAGE, MPIUpdateStatusMessage::createReceiveCommand);
        registerCommand(MPI_SHUTDOWN, MPIShutdown::createReceiveCommand);
    }

    public static void registerCommand(MPIStatusCode statusCode, Supplier<MPICommand<?>.MPIReceiveCommand> commandSupplier){
        commands.put(statusCode.getCode(), (i) -> commandSupplier.get());
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
                int targetNode = status.getSource();
                int tag = status.getTag();
                int code = tag & 0xFFFF;
                int id = (tag >> 16) & 0xFFFF;
                commands.get(code).apply(id).accept(shard);
            }
        }catch(MPIException e){
            //TODO Notify the root
            e.printStackTrace();
        }
    }
}
