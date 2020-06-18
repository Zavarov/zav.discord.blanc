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

import vartas.discord.blanc.*;

import javax.annotation.Nonnull;

@Nonnull
public abstract class Command extends CommandTOP {
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
        //Higher number -> Higher rank
        if(user.getRank().ordinal() < rank.ordinal())
            throw PermissionException.of(Errors.INSUFFICIENT_RANK);
    }
}
