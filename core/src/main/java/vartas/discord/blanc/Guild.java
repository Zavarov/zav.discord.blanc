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

package vartas.discord.blanc;

import javax.annotation.Nonnull;

/**
 * The internal representation of a Discord guild.
 */
@Nonnull
public class Guild extends GuildTOP {
    /**
     * Considering that the program needs to be in the {@link Guild} in order to register
     * a command, this value can always assumed to be defined.
     * @return the {@link Member} instance of this program in this {@link Guild}.
     */
    @Nonnull
    public Member getSelfMember(){
        throw new UnsupportedOperationException();
    }

    /**
     * Checks whether the specified {@link Member} can interact with the specified {@link Role}.<br>
     * Interacting usually means assigning this role to others, which is only possible when the
     * member has is the {@link Guild} owner or has a {@link Role} higher than the one that is modified.<br>
     * Another content would be the modification of the {@link Permission Permissions} associating
     * with the {@link Role}, which is only possible under similar conditions.
     * @param member the {@link Member} associated with the {@link Role}.
     * @param role the {@link Role} associated with the {@link Member}.
     * @return true if the {@link Member} can interact with the {@link Role}.
     */
    public boolean canInteract(@Nonnull Member member, @Nonnull Role role) {
        throw new UnsupportedOperationException();
    }

    /**
     * Checks whether the specified {@link Member} can interact with the specified {@link TextChannel}.<br>
     * In this context, interacting means being able to see the {@link TextChannel} and to be able
     * to send messages in it. This is only possible, when the {@link Member} both has {@link Permission#SEND_MESSAGES}
     * and {@link Permission#VIEW_CHANNEL}.
     * @see Permission#VIEW_CHANNEL
     * @see Permission#SEND_MESSAGES
     * @param member the {@link Member} associated with the {@link TextChannel}.
     * @param textChannel the {@link TextChannel} associated with the {@link Member}.
     * @return true if the {@link Member} can interact with the {@link TextChannel}.
     */
    public boolean canInteract(@Nonnull Member member, @Nonnull TextChannel textChannel) {
        throw new UnsupportedOperationException();
    }
}
