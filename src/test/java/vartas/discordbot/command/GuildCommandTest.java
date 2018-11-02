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

package vartas.discordbot.command;

import java.io.File;
import java.util.Arrays;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import vartas.OfflineInstance;
import vartas.discordbot.DiscordBot;
import vartas.xml.XMLPermission;

/**
 * @author u/Zavarov
 */
public class GuildCommandTest {
    GuildCommand command;
    OfflineInstance instance;
    
    @Before
    public void setUp(){
        instance = new OfflineInstance();
        command = new GuildCommand(){
            @Override
            protected void execute(){
                DiscordBot.sendMessage(this.message.getChannel(), "success");
            }
        };
        command.setBot(instance.bot);
        command.setMessage(instance.guild_message);
        command.setParameter(Arrays.asList());
        command.setPermission(XMLPermission.create(new File("src/test/resources/permission.xml")));
        
    }
    @Test(expected=CommandRequiresGuildException.class)
    public void outsideGuildTest(){
        command.setMessage(instance.private_message);
        command.run();
    }
    @Test
    public void insideGuildTest(){
        command.run();
        assertEquals(instance.messages.get(0).getContentRaw(),"success");
    }
}
