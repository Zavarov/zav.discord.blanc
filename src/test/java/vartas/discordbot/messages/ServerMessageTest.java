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
package vartas.discordbot.messages;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.MessageEmbed.Field;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.impl.GuildImpl;
import net.dv8tion.jda.core.entities.impl.JDAImpl;
import net.dv8tion.jda.core.entities.impl.MemberImpl;
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
import vartas.xml.XMLServer;

/**
 *
 * @author u/Zavarov
 */
public class ServerMessageTest {
    static OfflineCommunicator comm;
    static JDAImpl jda;
    XMLServer server;
    GuildImpl guild;
    TextChannelImpl channel1;
    TextChannelImpl channel2;
    TextChannelImpl channel3;
    UserImpl user1;
    UserImpl user2;
    UserImpl user3;
    SelfUserImpl self;
    RoleImpl role0;
    RoleImpl role1;
    RoleImpl role2;
    MemberImpl memberself;
    MemberImpl member1;
    MemberImpl member2;
    MemberImpl member3;
    @BeforeClass
    public static void startUp(){
        comm = (OfflineCommunicator)new OfflineEnvironment().comm(0);
        jda = (JDAImpl)comm.jda();
    }
    @Before
    public void setUp(){
        guild = new GuildImpl(jda , 0);
        channel1 = new TextChannelImpl(1, guild);
        channel2 = new TextChannelImpl(2, guild);
        channel3 = new TextChannelImpl(3, guild);
        guild.getTextChannelsMap().put(channel1.getIdLong(), channel1);
        guild.getTextChannelsMap().put(channel2.getIdLong(), channel2);
        guild.getTextChannelsMap().put(channel3.getIdLong(), channel3);
        self = new SelfUserImpl(0L,jda);
        user1 = new UserImpl(1L,jda);
        user2 = new UserImpl(2L,jda);
        user3 = new UserImpl(3L,jda);
        memberself = new MemberImpl(guild, self);
        member1 = new MemberImpl(guild, user1);
        member2 = new MemberImpl(guild, user2);
        member3 = new MemberImpl(guild, user3);
        role0 = new RoleImpl(0L,guild);
        role1 = new RoleImpl(1L,guild);
        role2 = new RoleImpl(2L,guild);
        
        jda.setSelfUser(self);
        jda.getUserMap().put(self.getIdLong(),self);
        jda.getUserMap().put(user1.getIdLong(),user1);
        jda.getUserMap().put(user2.getIdLong(),user2);
        jda.getUserMap().put(user3.getIdLong(),user3);
        guild.getMembersMap().put(self.getIdLong(),memberself);
        guild.getMembersMap().put(user1.getIdLong(),member1);
        guild.getMembersMap().put(user2.getIdLong(),member2);
        guild.getMembersMap().put(user3.getIdLong(),member3);
        guild.getRolesMap().put(role0.getIdLong(),role0);
        guild.getRolesMap().put(role1.getIdLong(),role1);
        guild.getRolesMap().put(role2.getIdLong(),role2);
        guild.setOwner(memberself);
        guild.setPublicRole(role0);
        role0.setRawPermissions(Permission.ALL_TEXT_PERMISSIONS);
        
        member2.setOnlineStatus(OnlineStatus.ONLINE);
        member3.setOnlineStatus(OnlineStatus.ONLINE);
        
        server = comm.server(guild);
    }
    
    @Test
    public void createTest(){
        guild.getMembersMap().put(user1.getIdLong(),new MemberImpl(guild,user1){
            @Override
            public List<Role> getRoles(){
                return Arrays.asList(role2);
            }
        });
        MessageEmbed message = ServerMessage.create(guild);
        List<Field> fields = message.getFields();
        assertEquals(fields.get(0).getName(),"Owner");
        assertEquals(fields.get(1).getName(),"Region");
        assertEquals(fields.get(2).getName(),"#TextChannels");
        assertEquals(fields.get(3).getName(),"#VoiceChannels");
        assertEquals(fields.get(4).getName(),"Admin");
        assertEquals(fields.get(5).getName(),"#Members");
        assertEquals(fields.get(6).getName(),"#Roles");
        assertEquals(fields.get(7).getName(),"Created");
    }
    
    @Test
    public void createBotTest(){
        user1.setBot(true);
        guild.getMembersMap().put(user1.getIdLong(),new MemberImpl(guild,user1){
            @Override
            public List<Role> getRoles(){
                return Arrays.asList(role2);
            }
        });
        MessageEmbed message = ServerMessage.create(guild);
        List<Field> fields = message.getFields();
        assertEquals(fields.get(0).getName(),"Owner");
        assertEquals(fields.get(1).getName(),"Region");
        assertEquals(fields.get(2).getName(),"#TextChannels");
        assertEquals(fields.get(3).getName(),"#VoiceChannels");
        assertEquals(fields.get(4).getName(),"Admin");
        assertEquals(fields.get(5).getName(),"#Members");
        assertEquals(fields.get(6).getName(),"#Roles");
        assertEquals(fields.get(7).getName(),"Created");
    }
    @Test
    public void createModeratorTest(){
        guild.getMembersMap().put(user1.getIdLong(), new MemberImpl(guild,user1){
            @Override
            public List<Role> getRoles(){
                return Arrays.asList(role1);
            }
        });
        role1.setRawPermissions(Permission.BAN_MEMBERS.getRawValue());
        MessageEmbed message = ServerMessage.create(guild);
        List<Field> fields = message.getFields();
        assertEquals(fields.get(0).getName(),"Owner");
        assertEquals(fields.get(1).getName(),"Region");
        assertEquals(fields.get(2).getName(),"#TextChannels");
        assertEquals(fields.get(3).getName(),"#VoiceChannels");
        assertEquals(fields.get(4).getName(),"Admin");
        assertEquals(fields.get(5).getName(),"Moderator");
        assertEquals(fields.get(6).getName(),"#Members");
        assertEquals(fields.get(7).getName(),"#Roles");
        assertEquals(fields.get(8).getName(),"Created");
    }
    @Test
    public void createThumbnailTest(){
        guild = new GuildImpl(jda, guild.getIdLong()){
            @Override
            public String getIconUrl(){
                return "http://image.jpg";
            }
        };
        member1 = new MemberImpl(guild, user1);
        guild.setName("guild");
        guild.setAvailable(true);
        guild.setOwner(member1);
        guild.getMembersMap().put(user1.getIdLong(), member1);
        jda.getGuildMap().put(guild.getIdLong(), guild);
        MessageEmbed message = ServerMessage.create(guild);
        List<Field> fields = message.getFields();
        assertEquals(fields.get(0).getName(),"Owner");
        assertEquals(fields.get(1).getName(),"Region");
        assertEquals(fields.get(2).getName(),"#TextChannels");
        assertEquals(fields.get(3).getName(),"#VoiceChannels");
        assertEquals(fields.get(4).getName(),"Admin");
        assertEquals(fields.get(5).getName(),"#Member");
        assertEquals(fields.get(6).getName(),"#Roles");
        assertEquals(fields.get(7).getName(),"Created");
        assertEquals(message.getThumbnail().getUrl(),"http://image.jpg");
        
    }
    @Test
    public void createMultipleModeratorsTest(){
        role0.setRawPermissions(Permission.BAN_MEMBERS.getRawValue());
        MessageEmbed message = ServerMessage.create(guild);
        List<Field> fields = message.getFields();
        assertEquals(fields.get(0).getName(),"Owner");
        assertEquals(fields.get(1).getName(),"Region");
        assertEquals(fields.get(2).getName(),"#TextChannels");
        assertEquals(fields.get(3).getName(),"#VoiceChannels");
        assertEquals(fields.get(4).getName(),"Admin");
        assertEquals(fields.get(5).getName(),"Moderators");
        assertEquals(fields.get(6).getName(),"#Members");
        assertEquals(fields.get(7).getName(),"#Roles");
        assertEquals(fields.get(8).getName(),"Created");
        
        assertTrue(fields.get(5).getValue().contains(member1.getAsMention()));
        assertFalse(fields.get(5).getValue().contains(memberself.getAsMention()));
    }
    @Test
    public void createMultipleAdminsTest(){
        role0.setRawPermissions(Permission.ADMINISTRATOR.getRawValue());
        MessageEmbed message = ServerMessage.create(guild);
        List<Field> fields = message.getFields();
        assertEquals(fields.get(0).getName(),"Owner");
        assertEquals(fields.get(1).getName(),"Region");
        assertEquals(fields.get(2).getName(),"#TextChannels");
        assertEquals(fields.get(3).getName(),"#VoiceChannels");
        assertEquals(fields.get(4).getName(),"Admins");
        assertEquals(fields.get(5).getName(),"#Members");
        assertEquals(fields.get(6).getName(),"#Roles");
        assertEquals(fields.get(7).getName(),"Created");
        
        assertTrue(fields.get(4).getValue().contains(member1.getAsMention()));
        assertTrue(fields.get(4).getValue().contains(memberself.getAsMention()));
    }
    @Test
    public void constructorIsPrivateTest() throws NoSuchMethodException, IllegalAccessException, InstantiationException, IllegalArgumentException, InvocationTargetException{
        Constructor<ServerMessage> constructor = ServerMessage.class.getDeclaredConstructor();
        assertTrue(Modifier.isPrivate(constructor.getModifiers()));
        constructor.setAccessible(true);
        constructor.newInstance();
    }
}
