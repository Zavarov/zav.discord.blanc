package vartas.discord.bot.mpi.serializable;

import com.google.common.base.Preconditions;
import vartas.discord.bot.entities.Rank;

import java.io.Serializable;

public class MPIRankModification implements Serializable {
    private final long userId;
    private final int rankOrdinal;

    public MPIRankModification(long userId, int rankOrdinal) throws IllegalArgumentException{
        Preconditions.checkArgument(rankOrdinal < Rank.Ranks.values().length);
        this.userId = userId;
        this.rankOrdinal = rankOrdinal;
    }

    public long getUserId(){
        return userId;
    }

    public Rank.Ranks getRank(){
        return Rank.Ranks.values()[rankOrdinal];
    }
}
