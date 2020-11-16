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

package vartas.discord.blanc.mock;

import vartas.discord.blanc.$factory.MessageEmbedFactory;
import vartas.discord.blanc.$factory.TitleFactory;
import vartas.discord.blanc.MessageEmbed;
import vartas.discord.blanc.Role;

import java.util.Collections;
import java.util.Optional;

public class RoleMock extends Role {
    @Override
    public String getAsMention() {
        throw new UnsupportedOperationException();
    }

    @Override
    public MessageEmbed toMessageEmbed(){
        return MessageEmbedFactory.create(
                Optional.empty(),
                Optional.empty(),
                Optional.of(TitleFactory.create("Role")),
                Optional.of(Long.toUnsignedString(getId())),
                Optional.empty(),
                Optional.empty(),
                Collections.emptyList()
        );
    }
}
