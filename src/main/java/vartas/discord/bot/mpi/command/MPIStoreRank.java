package vartas.discord.bot.mpi.command;

import com.google.common.base.Preconditions;
import vartas.discord.bot.entities.Rank;
import vartas.discord.bot.entities.Shard;
import vartas.discord.bot.mpi.MPICoreCommands;
import vartas.discord.bot.mpi.serializable.MPIVoid;

import javax.annotation.Nonnull;

public class MPIStoreRank extends MPICommand<MPIVoid> {
    private Rank rank;
    @Override
    public void visit(@Nonnull Rank rank) throws NullPointerException{
        Preconditions.checkNotNull(rank);
        this.rank = rank;
    }

    @Override
    public void endVisit(@Nonnull Shard shard){
        Preconditions.checkNotNull(shard);
        Preconditions.checkNotNull(rank);
        shard.store(rank);
    }

    @Override
    protected int getCode() {
        return MPICoreCommands.MPI_STORE_RANK.getCode();
    }
}
