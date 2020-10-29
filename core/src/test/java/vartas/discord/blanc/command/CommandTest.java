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
import vartas.discord.blanc.mock.*;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CommandTest extends AbstractTest {
    User author;
    PrivateChannel messageChannel;
    Command command;
    Message message;

    @BeforeEach
    public void setUp(){
        author = new UserMock(0, "User");
        messageChannel = new PrivateChannelMock(0, "PrivateChannel");
        command = new MessageCommandMock(author, messageChannel);
        message = new MessageMock();
    }

    @Test
    public void testCheckRootRank(){
        author.addRanks(Rank.ROOT);
        assertDoesNotThrow(() -> command.checkRank(author, Rank.USER));
        assertDoesNotThrow(() -> command.checkRank(author, Rank.DEVELOPER));
        assertDoesNotThrow(() -> command.checkRank(author, Rank.ROOT));
    }

    @Test
    public void testCheckDeveloperRank(){
        author.addRanks(Rank.DEVELOPER);
        assertDoesNotThrow(() -> command.checkRank(author, Rank.USER));
        assertDoesNotThrow(() -> command.checkRank(author, Rank.DEVELOPER));
        assertThrows(PermissionException.class, () -> command.checkRank(author, Rank.ROOT));
    }

    @Test
    public void testCheckUserRank(){
        author.addRanks(Rank.USER);
        assertDoesNotThrow(() -> command.checkRank(author, Rank.USER));
        assertThrows(PermissionException.class, () -> command.checkRank(author, Rank.DEVELOPER));
        assertThrows(PermissionException.class, () -> command.checkRank(author, Rank.ROOT));
    }

    @Test
    public void testCheckMissingAttachment(){
        assertThrows(NoSuchElementException.class, () -> command.checkAttachment(message));
    }

    @Test
    public void testCheckAttachment(){
        message.addAttachments(new AttachmentMock());

        command.checkAttachment(message);
    }
}
