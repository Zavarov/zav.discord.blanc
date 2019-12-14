package vartas.discord.bot.mpi.command;

import com.google.common.base.Preconditions;
import vartas.discord.bot.entities.Rank;
import vartas.discord.bot.mpi.MPICoreCommands;
import vartas.discord.bot.mpi.serializable.MPIRankModification;

public class MPIRemoveRank extends MPICommand<MPIRankModification> {
    @Override
    public void visit(Rank rank){
        Preconditions.checkNotNull(rank);
        rank.remove(message.getUserId(), message.getRank());
    }

    @Override
    protected int getCode() {
        return MPICoreCommands.MPI_REMOVE_RANK.getCode();
    }
}
