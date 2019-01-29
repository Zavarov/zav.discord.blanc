/*
 * Copyright (C) 2019 u/Zavarov
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
package vartas.discordbot.messages;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.impl.GuildImpl;
import net.dv8tion.jda.core.entities.impl.JDAImpl;
import net.dv8tion.jda.core.entities.impl.MemberImpl;
import net.dv8tion.jda.core.entities.impl.RoleImpl;
import net.dv8tion.jda.core.entities.impl.SelfUserImpl;
import net.dv8tion.jda.core.entities.impl.TextChannelImpl;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import vartas.discordbot.comm.OfflineCommunicator;
import vartas.discordbot.comm.OfflineEnvironment;
import vartas.xml.XMLServer;

/**
 *
 * @author u/Zavarov
 */
public class UserMessageTest {
    static OfflineCommunicator comm;
    static JDAImpl jda;
    XMLServer server;
    GuildImpl guild;
    TextChannelImpl channel1;
    SelfUserImpl self;
    RoleImpl role0;
    MemberImpl memberself;
    @BeforeClass
    public static void startUp(){
        comm = (OfflineCommunicator)new OfflineEnvironment().comm(0);
        jda = (JDAImpl)comm.jda();
    }
    @Before
    public void setUp(){
        guild = new GuildImpl(jda , 0);
        channel1 = new TextChannelImpl(1, guild);
        guild.getTextChannelsMap().put(channel1.getIdLong(), channel1);
        self = new SelfUserImpl(0L,jda);
        memberself = new MemberImpl(guild, self);
        role0 = new RoleImpl(0L,guild);
        
        jda.setSelfUser(self);
        jda.getUserMap().put(self.getIdLong(),self);
        guild.getMembersMap().put(self.getIdLong(),memberself);
        guild.getRolesMap().put(role0.getIdLong(),role0);
        guild.setOwner(memberself);
        guild.setPublicRole(role0);
        guild.setName("guild0");
        role0.setRawPermissions(Permission.ALL_TEXT_PERMISSIONS);
        role0.setName("role0");
        memberself.setNickname("memberself");
        
        server = comm.server(guild);
    }
    @Test
    public void createThumbnailTest(){
        self = new SelfUserImpl(self.getIdLong(), jda){
            @Override
            public String getAvatarUrl(){
                return "http://image.png";
            }
        };
        memberself = new MemberImpl(guild, self);
        InteractiveMessage message = UserMessage.create(self, self, channel1, comm);
        assertEquals(message.pages.get(0).getThumbnail().getUrl(),"http://image.png");
    }
    @Test
    public void createNoThumbnailTest(){
        InteractiveMessage message = UserMessage.create(self, self, channel1, comm);
        assertNull(message.pages.get(0).getThumbnail());
    }
}
