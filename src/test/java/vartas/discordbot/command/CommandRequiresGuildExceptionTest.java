package vartas.discordbot.command;

/*
 * Copyright (C) 2017 u/Zavarov
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



import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;

/**
 * @author u/Zavarov
 */
public class CommandRequiresGuildExceptionTest {
    CommandRequiresGuildException exception;
    @Before
    public void setUp(){
        exception = new CommandRequiresGuildException();
    }
    @Test
    public void checkMessageTest(){
        assertEquals(exception.getMessage(),"This command can only be executed inside of a guild.");
    }
}
