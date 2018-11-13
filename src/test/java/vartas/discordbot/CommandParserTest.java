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

package vartas.discordbot;

import java.io.IOException;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import vartas.discordbot.comm.Communicator;
import vartas.discordbot.comm.OfflineEnvironment;
import vartas.discordbot.command.ErrorCommand;
import vartas.discordbot.command.TestCommand;
import vartas.parser.cfg.ContextFreeGrammar;

/**
 * @author u/Zavarov
 */
public class CommandParserTest {
    static Communicator comm;
    CommandParser parser;
    
    @BeforeClass
    public static void startUp(){
        comm = new OfflineEnvironment().comm(0);
    }
    
    @Before
    public void setUp() throws IOException{
        ContextFreeGrammar grammar = new ContextFreeGrammar.Builder()
                .addTerminal("a")
                .addTerminal("b")
                .addNonterminal("Command")
                .setStartSymbol("Command")
                .addProduction("Command", "a","b").build();
        comm.environment().grammar().putAll(grammar);
        
        comm.environment().command().put("ab", "vartas.discordbot.command.TestCommand");
        
        parser = new CommandParser.Builder(comm).build();
    }
    
    @Test
    public void parseCommandTest(){
        assertTrue(parser.parseCommand(null, "ab") instanceof TestCommand);
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void parseCommandInvalidTest(){
        comm.environment().command().addCommand("ab", "junk");
        parser.parseCommand(null, "ab");
    }
    @Test
    public void parseUnexpectedTokenTest(){
        assertTrue(parser.parseCommand(null, "aba") instanceof ErrorCommand);
    }
}