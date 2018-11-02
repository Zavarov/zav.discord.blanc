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

import java.io.File;
import java.io.IOException;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import vartas.OfflineInstance;
import vartas.TestCommand;
import vartas.parser.cfg.ContextFreeGrammar;
import vartas.xml.XMLCommand;
import vartas.xml.XMLConfig;

/**
 * @author u/Zavarov
 */
public class DiscordParserTest {
    DiscordParser parser;
    ContextFreeGrammar grammar;
    XMLCommand command;
    XMLConfig config;
    DiscordMessageListener listener;
    OfflineInstance instance;
    
    @Before
    public void setUp() throws IOException{
        grammar = new ContextFreeGrammar.Builder()
                .addTerminal("a")
                .addTerminal("b")
                .addNonterminal("Command")
                .setStartSymbol("Command")
                .addProduction("Command", "a","b").build();
        command = new XMLCommand();
        
        command.addCommand("ab", "vartas.TestCommand");
        
        config = XMLConfig.create(new File("src/test/resources/config.xml"));
        parser = new DiscordParser.Builder(grammar, command, config).build();
    }
    
    @Test
    public void parseCommandTest(){
        assertTrue(parser.parseCommand(null, null, "ab") instanceof TestCommand);
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void parseCommandInvalidTest(){
        command.addCommand("ab", "junk");
        parser.parseCommand(null, null, "ab");
    }
}
