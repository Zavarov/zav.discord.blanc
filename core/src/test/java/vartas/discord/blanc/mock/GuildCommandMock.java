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

import vartas.discord.blanc.Guild;
import vartas.discord.blanc.Member;
import vartas.discord.blanc.TextChannel;
import vartas.discord.blanc.command.GuildCommand;

public class GuildCommandMock extends GuildCommand {
    public GuildCommandMock(Member author, TextChannel textChannel, Guild guild) {
        set$Author(author);
        set$TextChannel(textChannel);
        set$Guild(guild);
    }

    @Override
    public void run() {

    }

    @Override
    public void validate() {}

    @Override
    public GuildCommand getRealThis() {
        return this;
    }
}
