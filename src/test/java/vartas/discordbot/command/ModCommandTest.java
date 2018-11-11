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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import vartas.discordbot.comm.OfflineCommunicator;
import vartas.discordbot.comm.OfflineEnvironment;
import vartas.xml.XMLPermission;
import vartas.xml.XMLServer;

/**
 *
 * @author u/Zavarov
 */
public class ModCommandTest {
    static OfflineCommunicator comm;
    static JDAImpl jda;
    XMLServer server;
    GuildImpl guild;
    TextChannelImpl channel1;
    SelfUserImpl self;
    UserImpl user;
    RoleImpl role0;
    MemberImpl memberself;
    MemberImpl member;
    Message message1;
    Message message2;
    @BeforeClass
    public static void startUp(){
        comm = (OfflineCommunicator)new OfflineEnvironment().comm(0);
        jda = (JDAImpl)comm.jda();
    }
    ModCommand command;
    
    @Before
    public void setUp(){
        guild = new GuildImpl(jda , 0);
        channel1 = new TextChannelImpl(1, guild);
        self = new SelfUserImpl(0L,jda);
        user = new UserImpl(100L,jda);
        memberself = new MemberImpl(guild, self);
        member = new MemberImpl(guild,user);
        role0 = new RoleImpl(0L,guild);
        server = comm.server(guild);
        
        jda.setSelfUser(self);
        jda.getUserMap().put(self.getIdLong(),self);
        jda.getUserMap().put(user.getIdLong(),user);
        guild.getTextChannelsMap().put(channel1.getIdLong(), channel1);
        guild.getMembersMap().put(self.getIdLong(),memberself);
        guild.getMembersMap().put(user.getIdLong(),member);
        guild.getRolesMap().put(role0.getIdLong(),role0);
        guild.setOwner(memberself);
        guild.setPublicRole(role0);
        role0.setRawPermissions(Permission.ALL_TEXT_PERMISSIONS);
        
        message1 = new ReceivedMessage(
                1L, channel1, MessageType.DEFAULT,
                false, false, null,null, false, false, 
                "content", "", self, OffsetDateTime.now()
                ,Arrays.asList(), Arrays.asList(), Arrays.asList());
        
        message2 = new ReceivedMessage(
                2L, channel1, MessageType.DEFAULT,
                false, false, null,null, false, false, 
                "content", "", user, OffsetDateTime.now()
                ,Arrays.asList(), Arrays.asList(), Arrays.asList());
        
        command = new ModCommand(Permission.ADMINISTRATOR){
            @Override
            public void execute(){
                comm.send(channel1, "success");
            }
        };
        command.setMessage(message1);
        command.setParameter(Collections.emptyList());
        command.setCommunicator(comm);
        
        comm.actions.clear();
        comm.discord.clear();
    }
    
    @Test
    public void runTest(){
        assertTrue(comm.discord.entries().isEmpty());
        
        role0.setRawPermissions(Permission.ALL_GUILD_PERMISSIONS);
        command.run();
        
        assertEquals(comm.discord.get(channel1).get(0).getContentRaw(),"success");
    }
    @Test
    public void runFailureTest(){
        command.setMessage(message2);
        command.run();
        assertTrue(comm.discord.get(channel1).get(0).getContentRaw().contains("Administrator"));
    }
    
    @Test
    public void checkPermissionRootTest(){
        comm.environment().permission().add(Rank.ROOT, self);
        command.checkRequirements();
        
        startUp();
    }
    @Test
    public void checkPermissionModTest(){
        command.setMessage(message2);
        role0.setRawPermissions(Permission.ALL_GUILD_PERMISSIONS);
        command.checkRequirements();
    }
    @Test(expected=MissingPermissionException.class)
    public void checkPermissionFailureTest(){
        command.setMessage(message2);
        command.checkRequirements();
    }
    @Test
    public void canInteractRootTest(){
        command.setMessage(message2);
        comm.environment().permission().add(Rank.ROOT, user);
        
        assertTrue(command.canInteract(() -> member.canInteract(role0)));
        
        startUp();
    }
    @Test
    public void canInteractRootOwnerTest(){
        comm.environment().permission().add(Rank.ROOT, self);
        assertTrue(command.canInteract(() -> memberself.canInteract(role0)));
        
        startUp();
    }
    @Test
    public void canInteractOwnerTest(){
        assertTrue(command.canInteract(() -> memberself.canInteract(role0)));
        
    }
    @Test
    public void canInteractFailureTest() {
        command.setMessage(message2);
        assertFalse(command.canInteract(() -> member.canInteract(role0)));
    }
}