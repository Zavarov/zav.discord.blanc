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

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Collections;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageType;
import net.dv8tion.jda.core.entities.impl.GuildImpl;
import net.dv8tion.jda.core.entities.impl.JDAImpl;
import net.dv8tion.jda.core.entities.impl.MemberImpl;
import net.dv8tion.jda.core.entities.impl.PrivateChannelImpl;
import net.dv8tion.jda.core.entities.impl.ReceivedMessage;
import net.dv8tion.jda.core.entities.impl.RoleImpl;
import net.dv8tion.jda.core.entities.impl.SelfUserImpl;
import net.dv8tion.jda.core.entities.impl.TextChannelImpl;
import net.dv8tion.jda.core.entities.impl.UserImpl;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import vartas.discordbot.comm.OfflineCommunicator;
import vartas.discordbot.comm.OfflineEnvironment;
import vartas.xml.XMLServer;

/**
 * @author u/Zavarov
 */
public class GuildCommandTest {
    static OfflineCommunicator comm;
    static XMLServer server;
    static GuildImpl guild;
    static TextChannelImpl channel1;
    static PrivateChannelImpl channel2;
    static JDAImpl jda;
    static SelfUserImpl self;
    static UserImpl user;
    static RoleImpl role0;
    static MemberImpl memberself;
    static MemberImpl member;
    static Message message1;
    static Message message2;
    @BeforeClass
    public static void startUp(){
        comm = (OfflineCommunicator)new OfflineEnvironment().comm(0);
        
        jda = (JDAImpl)comm.jda();
        guild = new GuildImpl(jda , 0);
        channel1 = new TextChannelImpl(1, guild);
        guild.getTextChannelsMap().put(channel1.getIdLong(), channel1);
        self = new SelfUserImpl(0L,jda);
        user = new UserImpl(1L,jda);
        memberself = new MemberImpl(guild, self);
        member = new MemberImpl(guild,user);
        role0 = new RoleImpl(0L,guild);
        channel2 = new PrivateChannelImpl(1, user);
        
        jda.setSelfUser(self);
        jda.getUserMap().put(self.getIdLong(),self);
        jda.getUserMap().put(user.getIdLong(),user);
        guild.getMembersMap().put(self.getIdLong(),memberself);
        guild.getMembersMap().put(user.getIdLong(),member);
        guild.getRolesMap().put(role0.getIdLong(),role0);
        guild.setOwner(memberself);
        guild.setPublicRole(role0);
        user.setPrivateChannel(channel2);
        role0.setRawPermissions(Permission.ALL_TEXT_PERMISSIONS);
        
        message1 = new ReceivedMessage(
                1L, channel1, MessageType.DEFAULT,
                false, false, null,null, false, false, 
                "content", "", self, OffsetDateTime.now()
                ,Arrays.asList(), Arrays.asList(), Arrays.asList());
        
        message2 = new ReceivedMessage(
                2L, channel2, MessageType.DEFAULT,
                false, false, null,null, false, false, 
                "content", "", user, OffsetDateTime.now()
                ,Arrays.asList(), Arrays.asList(), Arrays.asList());
        
        server = comm.server(guild);
    }
    GuildCommand command;
    
    @Before
    public void setUp(){
        command = new GuildCommand(){
            @Override
            protected void execute(){
                comm.send(this.message.getChannel(), "success");
            }
        };
        command.setMessage(message1);
        command.setParameter(Collections.emptyList());
        command.setCommunicator(comm);
        
        comm.actions.clear();
        comm.discord.clear();
        
    }
    @Test
    public void outsideGuildTest(){
        command.setMessage(message2);
        command.run();
        assertEquals(comm.discord.get(channel2).get(0).getContentRaw(),"This command can only be executed inside of a guild.");
    }
    @Test
    public void insideGuildTest(){
        command.run();
        assertEquals(comm.discord.get(channel1).get(0).getContentRaw(),"success");
    }
}
