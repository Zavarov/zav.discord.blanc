/*
 * Copyright (C) 2018 u/Zavarov
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
package vartas.xml;

import com.google.common.collect.Sets;
import java.io.File;
import java.util.Arrays;
import java.util.Set;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author u/Zavarov
 */
public class XMLConfigTest {
    XMLConfig config;
    
    @Before
    public void setUp(){
        config = XMLConfig.create(new File("src/test/resources/config.xml"));
    }
    @Test
    public void getDiscordShardsTest(){
        assertEquals(config.getDiscordShards(),2);
    }
    @Test
    public void getStatusIntervalTest(){
        assertEquals(config.getStatusInterval(),5);
    }
    @Test
    public void getActivityIntervalTest(){
        assertEquals(config.getActivityInterval(),15);
    }
    @Test
    public void getSupportInviteTest(){
        assertEquals(config.getSupportInvite(),"support_invite");
    }
    @Test
    public void getBotNameTest(){
        assertEquals(config.getBotName(),"name");
    }
    @Test
    public void getBotVersionTest(){
        assertEquals(config.getBotVersion(),"1.0");
    }
    @Test
    public void getBotInviteTest(){
        assertEquals(config.getBotInvite(),"bot_invite");
    }
    @Test
    public void getCommandIdentifierTest(){
        assertEquals(config.getCommandIdentifier(),Arrays.asList("Command"));
    }
    @Test
    public void getDataIdentifierTest(){
        Set<String> identifier = Sets.newHashSet("Identifier1","Identifier2");
        assertTrue(config.getDataIdentifier().containsAll(identifier));
        assertTrue(identifier.containsAll(config.getDataIdentifier()));
    }
    @Test
    public void getDataFolder(){
        assertEquals(config.getDataFolder(),"src/test/resources");
    }
    @Test
    public void getPrefixTest(){
        assertEquals(config.getPrefix(),"\\");
    }
    @Test
    public void getInteractiveMessageAgeTest(){
        assertEquals(config.getInteractiveMessageAge(),15);
    }
    @Test
    public void getHelpTest(){
        assertEquals(config.getHelp(),"help");
    }
    @Test
    public void getImageHeightTest(){
        assertEquals(config.getImageHeight(),5);
    }
    @Test
    public void getImageWidthTest(){
        assertEquals(config.getImageWidth(),10);
    }
}
