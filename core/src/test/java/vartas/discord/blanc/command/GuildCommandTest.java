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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import vartas.discord.blanc.AbstractTest;
import vartas.discord.blanc.Permission;
import vartas.discord.blanc.PermissionException;
import vartas.discord.blanc.mock.CommandBuilderMock;
import vartas.discord.blanc.mock.GuildCommandMock;
import vartas.discord.blanc.mock.ParserMock;

import static org.assertj.core.api.Assertions.assertThat;

public class GuildCommandTest extends AbstractTest {
    ParserMock parser;
    GuildCommandMock guildCommand;
    CommandBuilderMock commandBuilder;
    @BeforeEach
    public void setUp(){
        parser = new ParserMock();
        commandBuilder = new CommandBuilderMock(parser, "!!");

        guildCommand = new GuildCommandMock(member, textChannel, guild);
    }

    @Test
    public void testCheckPermission(){
        member.permissions.put(textChannel, Permission.CHANGE_NICKNAME);
        guildCommand.checkPermission(member, textChannel, Permission.CHANGE_NICKNAME);
    }

    @Test
    public void testCheckMissingPermission(){
        Assertions.assertThrows(PermissionException.class, () -> guildCommand.checkPermission(member, textChannel, Permission.CHANGE_NICKNAME));
    }

    @Test
    public void testGetRealThis(){
        assertThat(guildCommand.getRealThis()).isInstanceOf(GuildCommand.class);
    }
}
