package vartas.discord.bot.mpi.serializable;

import com.google.common.base.Preconditions;
import com.google.common.collect.HashMultimap;
import vartas.discord.bot.entities.Rank;

import javax.annotation.Nonnull;
import java.io.Serializable;

public class MPIRank extends Rank implements Serializable {
    @Nonnull
    private final HashMultimap<Long, Ranks> multimap;

    public MPIRank(@Nonnull Rank rank) throws NullPointerException{
        Preconditions.checkNotNull(rank);
        multimap = HashMultimap.create(rank.get());
    }

    @Nonnull
    public HashMultimap<Long, Ranks> get(){
        return multimap;
    }
}
