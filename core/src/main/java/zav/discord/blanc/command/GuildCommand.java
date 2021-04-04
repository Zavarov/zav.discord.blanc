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

package zav.discord.blanc.command;

import zav.discord.blanc.*;
import zav.discord.blanc.Errors;
import zav.discord.blanc.Guild;
import zav.discord.blanc.PermissionException;

import javax.annotation.Nonnull;

/**
 * A guild command is a subclass of the normal command that can only be executed within a {@link Guild}. In addition to
 * a normal {@link Command}, it allows an additional condition, according to which a {@link User} is only able to
 * execute the {@link Command}, if and only if they have a specified set of permissions.
 * @see Permission
 */
@Nonnull
public abstract class GuildCommand extends GuildCommandTOP{
    /**
     * Checks if the specified {@link Member} has the given Discord {@link Permission}. Permissions can be granted
     * either by roles or via {@link TextChannel} overrides. As such, the number of permissions may differ between
     * multiple channels.
     * @param member The {@link Member} associated with the given {@link Permission}.
     * @param textChannel The {@link TextChannel} associated with the given {@link Permission}.
     * @param permission The {@link Permission} associated with the given {@link Member} and {@link TextChannel}.
     * @see Permission
     * @see Errors#INSUFFICIENT_PERMISSION
     * @throws PermissionException If the user doesn't have at least one of the required permissions.
     */
    protected void checkPermission(@Nonnull Member member, @Nonnull TextChannel textChannel, @Nonnull Permission permission){
        if(!member.getPermissions(textChannel).contains(permission))
            throw PermissionException.of(Errors.INSUFFICIENT_PERMISSION, permission);
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
