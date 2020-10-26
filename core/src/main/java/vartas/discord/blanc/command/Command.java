/*
 * Copyright (c) 2020 Zavarov
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package vartas.discord.blanc.command;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import vartas.discord.blanc.*;

import javax.annotation.Nonnull;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * The base class for all commands.
 * It implements methods for validating each command before execution.
 */
@Nonnull
public abstract class Command extends CommandTOP {
    /**
     * A {@link Rank} may inherit multiple other ranks. A prime example for this is
     * {@link Rank#ROOT}, which includes all other ranks.<br>
     * This multimap keeps track of all those relations.
     */
    private static final Multimap<Rank, Rank> RANKS_ALIAS = HashMultimap.create();

    static{
        RANKS_ALIAS.putAll(Rank.ROOT, EnumSet.allOf(Rank.class));
        RANKS_ALIAS.put(Rank.DEVELOPER, Rank.REDDIT);
    }

    /**
     * Checks if the specified {@link User} has the given {@link Rank}.<br>
     * This check is transitive, meaning if the {@link User} has a specific {@link Rank}, they
     * automatically also have all ranks below it.
     * @param user The {@link User} associated with the given {@link Rank}.
     * @param rank The {@link Rank} associated with the {@link User}.
     * @see Rank
     * @see Errors#INSUFFICIENT_RANK
     * @throws PermissionException if the user doesn't have the given rank.
     */
    protected void checkRank(@Nonnull User user, @Nonnull Rank rank) throws PermissionException{
        if(!getEffectiveRanks(user).contains(rank))
            throw PermissionException.of(Errors.INSUFFICIENT_RANK);
    }

    /**
     * Checks if the specified {@link Message} contains at least one attachment.<br>
     * @param message The {@link Message} associated with the command.
     * @throws NoSuchElementException if the message has no attachments.
     */
    protected void checkAttachment(@Nonnull Message message) throws NoSuchElementException{
        if(message.isEmptyAttachments())
            throw new NoSuchElementException(Errors.INSUFFICIENT_ATTACHMENTS.toString());
    }

    /**
     * Computes all ranks the specified {@link User} owns. The generated {@link Set} will contain
     * all explicitly granted ranks, as well as all inherited ranks specified in {@link #RANKS_ALIAS}.
     * @param user The {@link User} associated with the calculated {@link Rank}.
     * @return A set containing all ranks associated with the {@link User}.
     */
    @Nonnull
    protected Set<Rank> getEffectiveRanks(@Nonnull User user){
        Set<Rank> effectiveRanks = new HashSet<>();

        //Every user has the USER rank by default
        effectiveRanks.add(Rank.USER);
        effectiveRanks.addAll(user.getRanks());
        for(Rank rank : user.getRanks())
            effectiveRanks.addAll(RANKS_ALIAS.get(rank));

        return effectiveRanks;
    }

    @Override
    public Command getRealThis(){
        return this;
    }
}
