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

/**
 * A guild command is a subclass of the normal command.
 * This command can only be executed in a guild and allows an additional condition,
 * with which only users with certain permissions may execute the command.
 */
@Nonnull
public abstract class GuildCommand extends GuildCommandTOP{
    /**
     * Checks if the specified {@link Member} has the given Discord {@link Permission}.<br>
     * The permissions can either come via {@link Role Roles} or via {@link TextChannel TextChannels}.
     * In the latter case, those permissions are only valid within the scope of the given {@link TextChannel}.
     * @param member The {@link Member} associated with the given {@link Permission}.
     * @param textChannel The {@link TextChannel} associated with the given {@link Permission}.
     * @param permission The {@link Permission} associated with the given {@link Member} and {@link TextChannel}.
     * @see Permission
     * @see Errors#INSUFFICIENT_PERMISSION
     * @throws PermissionException if the user doesn't have the given rank.
     */
    protected void checkPermission(@Nonnull Member member, @Nonnull TextChannel textChannel, @Nonnull Permission permission){
        if(!member.getPermissions(textChannel).contains(permission))
            throw PermissionException.of(Errors.INSUFFICIENT_PERMISSION);
    }

    /**
     * Part of the visitor pattern to grant access to the explicit implementation of the individual types.
     * @return The current instance.
     */
    @Override
    public GuildCommand getRealThis(){
        return this;
    }
}
