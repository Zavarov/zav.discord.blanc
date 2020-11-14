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
import vartas.discord.blanc.*;
import vartas.discord.blanc.mock.AttachmentMock;
import vartas.discord.blanc.mock.MessageCommandMock;
import vartas.discord.blanc.mock.MessageMock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CommandTest extends AbstractTest {
    Command command;
    Message message;

    @BeforeEach
    public void setUp(){
        command = new MessageCommandMock(user, textChannel);
        message = new MessageMock();
    }

    @Test
    public void testCheckRootRank(){
        user.addRanks(Rank.ROOT);
        assertDoesNotThrow(() -> command.checkRank(user, Rank.USER));
        assertDoesNotThrow(() -> command.checkRank(user, Rank.DEVELOPER));
        assertDoesNotThrow(() -> command.checkRank(user, Rank.ROOT));
    }

    @Test
    public void testCheckDeveloperRank(){
        user.addRanks(Rank.DEVELOPER);
        assertDoesNotThrow(() -> command.checkRank(user, Rank.USER));
        assertDoesNotThrow(() -> command.checkRank(user, Rank.DEVELOPER));
        assertThrows(PermissionException.class, () -> command.checkRank(user, Rank.ROOT));
    }

    @Test
    public void testCheckUserRank(){
        user.addRanks(Rank.USER);
        assertDoesNotThrow(() -> command.checkRank(user, Rank.USER));
        assertThrows(PermissionException.class, () -> command.checkRank(user, Rank.DEVELOPER));
        assertThrows(PermissionException.class, () -> command.checkRank(user, Rank.ROOT));
    }

    @Test
    public void testCheckMissingAttachment(){
        assertThrows(CommandException.class, () -> command.checkAttachment(message));
    }

    @Test
    public void testCheckAttachment(){
        message.addAttachments(new AttachmentMock());

        command.checkAttachment(message);
    }

    @Test
    public void testGetRealThis(){
        assertThat(command.getRealThis()).isInstanceOf(Command.class);
    }
}
