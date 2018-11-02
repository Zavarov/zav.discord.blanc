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
package vartas.discordbot.command;

import java.io.File;
import java.util.Collections;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import vartas.OfflineInstance;
import vartas.xml.XMLPermission;

/**
 *
 * @author u/Zavarov
 */
public class ErrorCommandTest {
    ErrorCommand command;
    OfflineInstance instance;
    @Before
    public void setUp(){
        instance = new OfflineInstance();
        command = new ErrorCommand(new RuntimeException());
        command.setMessage(instance.guild_message);
        command.setParameter(Collections.emptyList());
        command.setBot(instance.bot);
        command.setConfig(instance.config);
        command.setPermission(XMLPermission.create(new File("src/test/resources/permission.xml")));
    }
    @Test
    public void runTest(){
        command.run();
        assertTrue(instance.messages.get(0).getEmbeds().get(0).getFields().get(0).getValue().contains("RuntimeException"));
    }
}
