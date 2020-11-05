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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import vartas.discord.blanc.AbstractTest;
import vartas.discord.blanc.mock.*;

import java.time.Instant;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

public class CommandBuilderTest extends AbstractTest {
    ParserMock parser;
    IntermediateCommandMock privateIntermediateCommand;
    IntermediateCommandMock guildIntermediateCommand;
    MessageCommandMock privateCommand;
    GuildCommandMock guildCommand;
    CommandBuilderMock commandBuilder;

    MessageMock guildMessage;
    MessageMock privateMessage;
    @BeforeEach
    public void setUp(){
        parser = new ParserMock();
        commandBuilder = new CommandBuilderMock(parser, "!!");

        guildMessage = new MessageMock(0, Instant.now(), member);
        privateMessage = new MessageMock(1, Instant.now(), user);

        privateCommand = new MessageCommandMock(user, privateChannel);
        guildCommand = new GuildCommandMock(member, textChannel, guild);

        privateIntermediateCommand = new IntermediateCommandMock("!!", "private", Collections.emptyList());
        guildIntermediateCommand = new IntermediateCommandMock("!!", "guild", Collections.emptyList());

        commandBuilder.commandTable.put("private", Collections.emptyList(), privateCommand);
        commandBuilder.commandTable.put("guild", Collections.emptyList(), guildCommand);
        parser.commandMap.put(privateMessage, privateIntermediateCommand);
        parser.commandMap.put(guildMessage, guildIntermediateCommand);
    }

    @Test
    public void testBuildInvalidGuildCommand(){
        parser.commandMap.remove(guildMessage);
        assertThat(commandBuilder.build(guildMessage, guild, textChannel)).isEmpty();

    }

    @Test
    public void testBuildGuildCommand(){
        assertThat(commandBuilder.build(guildMessage, guild, textChannel)).contains(guildCommand);
    }

    @Test
    public void testBuildGuildCommandWithGuildPrefix(){
        guild.setPrefix("*");
        guildIntermediateCommand.setPrefix("*");
        assertThat(commandBuilder.build(guildMessage, guild, textChannel)).contains(guildCommand);
    }

    @Test
    public void testBuildGuildCommandWithoutPrefix(){
        guildIntermediateCommand.setPrefix(null);
        assertThat(commandBuilder.build(guildMessage, guild, textChannel)).isEmpty();
    }

    @Test
    public void testBuildInvalidPrivateCommand(){
        parser.commandMap.remove(privateMessage);
        assertThat(commandBuilder.build(privateMessage, privateChannel)).isEmpty();
    }

    @Test
    public void testBuildPrivateCommand(){
        assertThat(commandBuilder.build(privateMessage, privateChannel)).contains(privateCommand);
    }

    @Test
    public void testBuildPrivateCommandWithoutPrefix(){
        privateIntermediateCommand.setPrefix(null);
        assertThat(commandBuilder.build(privateMessage, privateChannel)).isEmpty();
    }
}
