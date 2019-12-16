package vartas.discord.bot.mpi.command;

import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;
import vartas.discord.bot.entities.Rank;
import vartas.discord.bot.mpi.MPICoreCommands;
import vartas.discord.bot.mpi.serializable.MPIRankModification;

import javax.annotation.Nonnull;

public class MPIAddRank extends MPICommand<MPIRankModification> {
    @Override
    public void visit(@NotNull @Nonnull Rank rank) throws NullPointerException{
        Preconditions.checkNotNull(rank);
        rank.add(message.getUserId(), message.getRank());
    }

    @Override
    protected int getCode() {
        return MPICoreCommands.MPI_ADD_RANK.getCode();
    }
}
