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

import vartas.discord.blanc.factory.MessageEmbedFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.InputStream;
import java.util.List;

/**
 * The internal representation of a {@link Guild} member.
 */
@Nonnull
public abstract class Member extends MemberTOP implements Printable{
    /**
     * Retrieves all effective permissions this {@link Member} has in the given {@link TextChannel}.
     * To avoid redundancy, this task is delegated to the underlying Discord library.
     * @param textChannel The text channel associated with the permissions.
     * @return A list of all permissions this member has in the given text channel.
     */
    @Nonnull
    public abstract List<Permission> getPermissions(@Nonnull TextChannel textChannel);

    @Override
    public MessageEmbed toMessageEmbed() {
        MessageEmbed messageEmbed = MessageEmbedFactory.create();

        messageEmbed.setTitle(getName());
        messageEmbed.addFields("ID", getId());
        messageEmbed.addFields("Rank", getRanks());

        return messageEmbed;
    }
}
