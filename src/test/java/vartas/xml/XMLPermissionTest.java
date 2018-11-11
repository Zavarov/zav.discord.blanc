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
import net.dv8tion.jda.core.entities.impl.JDAImpl;
import net.dv8tion.jda.core.entities.impl.UserImpl;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import vartas.discordbot.comm.OfflineEnvironment;
import vartas.discordbot.command.Rank;
import static vartas.discordbot.command.Rank.DEVELOPER;
import static vartas.discordbot.command.Rank.REDDIT;
import static vartas.discordbot.command.Rank.ROOT;
import static vartas.discordbot.command.Rank.USER;

/**
 *
 * @author u/Zavarov
 */
public class XMLPermissionTest {
    XMLPermission permission;
    UserImpl user1,user2,user3;
    JDAImpl jda;
    
    @Before
    public void setUp(){
        permission = XMLPermission.create(new File("src/test/resources/permission.xml"));
        jda = OfflineEnvironment.create();
        user1 = new UserImpl(1,jda);
        user2 = new UserImpl(2,jda);
        user3 = new UserImpl(3,jda);
        jda.getUserMap().put(user1.getIdLong(), user1);
        jda.getUserMap().put(user2.getIdLong(), user2);
        jda.getUserMap().put(user3.getIdLong(), user3);
    }
    
    @Test
    public void addTest(){
        assertFalse(permission.get("ROOT").contains(user3.getId()));
        permission.add(Rank.ROOT, user3);
        assertTrue(permission.get("ROOT").contains(user3.getId()));
    }
    
    @Test
    public void removeTest(){
        assertTrue(permission.get("ROOT").contains(user1.getId()));
        permission.remove(Rank.ROOT, user1);
        assertFalse(permission.get("ROOT").contains(user1.getId()));
    }
    
    @Test
    public void getRanksTest(){
        assertEquals(permission.getRanks(user1),Sets.newHashSet(ROOT,REDDIT,USER));
        assertEquals(permission.getRanks(user2),Sets.newHashSet(DEVELOPER,USER));
        assertEquals(permission.getRanks(user3),Sets.newHashSet(REDDIT,USER));
    }
}
