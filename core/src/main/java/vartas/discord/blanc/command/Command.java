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
 * The base class for all commands. A command is a subroutine of the application than can be executed via a
 * {@link MessageChannel}.
 */
@Nonnull
public abstract class Command extends CommandTOP {
    /**
     * Specifies the hierarchy between the different ranks. It is not necessary for a {@link User} to have a specific
     * {@link Rank}, as long as they have a {@link Rank} that inherits it. A prime example for this is
     * {@link Rank#ROOT}, which includes all other ranks.
     */
    @Nonnull
    private static final Multimap<Rank, Rank> RANK_ALIASES = HashMultimap.create();

    static{
        RANK_ALIASES.putAll(Rank.ROOT, EnumSet.allOf(Rank.class));
        RANK_ALIASES.put(Rank.DEVELOPER, Rank.REDDIT);
    }

    /**
     * Checks if the specified {@link User} has the given {@link Rank}. Upon failure, a {@link PermissionException} is
     * thrown. The check will succeed if the {@link User} either has the {link Rank} explicitly or implicitly via
     * aliases.
     * @param user The {@link User} associated with the given {@link Rank}.
     * @param rank The {@link Rank} associated with the {@link User}.
     * @see Rank
     * @see Errors#INSUFFICIENT_RANK
     * @throws PermissionException If the user doesn't have the given rank.
     */
    protected void checkRank(@Nonnull User user, @Nonnull Rank rank) throws PermissionException{
        if(!getEffectiveRanks(user).contains(rank))
            throw PermissionException.of(Errors.INSUFFICIENT_RANK);
    }

    /**
     * Checks if the specified {@link Message} contains at least one attachment. Upon failure, a
     * {@link NoSuchElementException} is thrown.
     * @param message The {@link Message} associated with the {@link Command}.
     * @throws NoSuchElementException If the message has no attachments.
     */
    protected void checkAttachment(@Nonnull Message message) throws NoSuchElementException{
        if(message.isEmptyAttachments())
            throw new NoSuchElementException(Errors.INSUFFICIENT_ATTACHMENTS.toString());
    }

    /**
     * Computes all ranks owned the specified {@link User}. The returned {@link Set} contains all explicitly owned
     * ranks, as well as all aliases specified in {@link #RANK_ALIASES}. Note that an user always has {@link Rank#USER}.
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
            effectiveRanks.addAll(RANK_ALIASES.get(rank));

        return effectiveRanks;
    }

    /**
     * Part of the visitor pattern to grant access to the explicit implementation of the individual types.
     * @return The current instance.
     */
    @Override
    public Command getRealThis(){
        return this;
    }
}
