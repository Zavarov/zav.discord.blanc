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
import vartas.discord.blanc.*;
import vartas.discord.blanc.mock.*;

public class GuildCommandTest extends AbstractTest {
    MemberMock author;
    Guild guild;
    ParserMock parser;
    TextChannel textChannel;
    GuildCommandMock guildCommand;
    CommandBuilderMock commandBuilder;
    @BeforeEach
    public void setUp(){
        parser = new ParserMock();
        commandBuilder = new CommandBuilderMock(parser, "!!");

        author = new MemberMock(0, "User");
        guild = new GuildMock(0, "Guild");
        textChannel = new TextChannelMock(0, "TextChannel");
        guildCommand = new GuildCommandMock(author, textChannel, guild);
    }

    @Test
    public void testCheckPermission(){
        author.permissions.add(Permission.CHANGE_NICKNAME);
        guildCommand.checkPermission(author, textChannel, Permission.CHANGE_NICKNAME);
    }

    @Test
    public void testCheckMissingPermission(){
        Assertions.assertThrows(PermissionException.class, () -> guildCommand.checkPermission(author, textChannel, Permission.CHANGE_NICKNAME));
    }
}
