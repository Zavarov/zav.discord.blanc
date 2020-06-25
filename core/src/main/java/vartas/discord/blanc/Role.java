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

import vartas.discord.blanc.factory.FieldFactory;
import vartas.discord.blanc.factory.MessageEmbedFactory;

public abstract class Role extends RoleTOP implements Printable{
    @Override
    public MessageEmbed toMessageEmbed() {
        MessageEmbed messageEmbed = MessageEmbedFactory.create();

        messageEmbed.setTitle(getName());
        messageEmbed.addFields("ID", getId());
        getGroup().ifPresent(group -> messageEmbed.addFields("group", group));

        return messageEmbed;
    }
}
