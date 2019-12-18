package vartas.discord.bot.mpi.command;

import com.google.common.base.Preconditions;
import vartas.discord.bot.entities.Rank;
import vartas.discord.bot.entities.Shard;
import vartas.discord.bot.mpi.MPIAdapter;
import vartas.discord.bot.mpi.MPICoreCommands;
import vartas.discord.bot.mpi.serializable.MPIRank;

import javax.annotation.Nonnull;

public class MPIUpdateRank extends MPICollectiveCommand<MPIRank> {
    private Rank rank;

    @Override
    public void visit(@Nonnull Rank rank) throws NullPointerException{
        Preconditions.checkNotNull(rank);
        Preconditions.checkNotNull(message);
        this.rank = rank;
        this.rank.replace(message.get());
    }

    @Override
    public void endVisit(@Nonnull Shard shard){
        if(myRank == MPIAdapter.MPI_MASTER_NODE) {
            Preconditions.checkNotNull(shard);
            Preconditions.checkNotNull(rank);
            shard.store(rank);
        }
    }

    @Override
    protected int getCode() {
        return MPICoreCommands.MPU_UPDATE_RANK.getCode();
    }

    public static MPIReceiveCommand createReceiveCommand(){
        return new MPIUpdateRank().new MPIReceiveCommand();
    }

    public static MPISendCommand createSendCommand(){
        return new MPIUpdateRank().new MPICollectiveSendCommand();
    }
}
