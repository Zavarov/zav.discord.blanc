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

import java.awt.Color;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.impl.GuildImpl;
import net.dv8tion.jda.core.entities.impl.JDAImpl;
import net.dv8tion.jda.core.entities.impl.MemberImpl;
import net.dv8tion.jda.core.entities.impl.RoleImpl;
import net.dv8tion.jda.core.entities.impl.SelfUserImpl;
import net.dv8tion.jda.core.entities.impl.TextChannelImpl;
import net.dv8tion.jda.core.entities.impl.UserImpl;
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
public class RoleMessageTest {
    static OfflineCommunicator comm;
    static JDAImpl jda;
    XMLServer server;
    GuildImpl guild;
    TextChannelImpl channel1;
    UserImpl user;
    SelfUserImpl self;
    RoleImpl role0;
    MemberImpl memberself;
    MemberImpl member;
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
        user = new UserImpl(1L,jda);
        memberself = new MemberImpl(guild, self);
        member = new MemberImpl(guild, user);
        role0 = new RoleImpl(0L,guild);
        
        jda.setSelfUser(self);
        jda.getUserMap().put(self.getIdLong(),self);
        jda.getUserMap().put(user.getIdLong(),user);
        guild.getMembersMap().put(self.getIdLong(),memberself);
        guild.getMembersMap().put(user.getIdLong(),member);
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
    public void createWithRolesTest(){
        guild = new GuildImpl(jda,0L){
            @Override
            public List<Member> getMembersWithRoles(Role... role){
                return Arrays.asList(member,memberself);
            }
            @Override
            public List<Member> getMembersWithRoles(Collection<Role> role){
                return Arrays.asList(member,memberself);
            }
        };
        role0 = new RoleImpl(role0.getIdLong(),guild);
        guild.getRolesMap().put(role0.getIdLong(), role0);
        
        InteractiveMessage message = RoleMessage.create(user, role0, channel1, comm);
        assertTrue(message.pages.get(0).getFields().get(1).getValue().contains("`#Members   :` 2"));
    }
    @Test
    public void createWithoutRolesTest(){
        role0.setRawPosition(0);
        InteractiveMessage message = RoleMessage.create(user, role0, channel1, comm);
        assertTrue(message.pages.get(0).getFields().get(1).getValue().contains("`#Members   :` 0"));
    }
    
    @Test
    public void createWithoutColorTest(){
        role0.setColor(Role.DEFAULT_COLOR_RAW);
        InteractiveMessage message = RoleMessage.create(user, role0, channel1, comm);
        assertFalse(message.pages.get(0).getFields().get(1).getValue().contains("Color"));
    }
    
    @Test
    public void createWithColorTest(){
        role0.setColor(Color.RED.getRGB());
        InteractiveMessage message = RoleMessage.create(user, role0, channel1, comm);
        assertTrue(message.pages.get(0).getFields().get(1).getValue().contains("Color"));
        assertTrue(message.pages.get(0).getFields().get(1).getValue().contains("0xFF0000"));
    }
    
    @Test
    public void createWithPermissionTest(){
        role0.setRawPermissions(Permission.ALL_PERMISSIONS);
        InteractiveMessage message = RoleMessage.create(user, role0, channel1, comm);
        assertTrue(message.pages.get(1).getFields().get(1).getValue().contains("Administrator"));
    }
    
    @Test
    public void createWithoutPermissionTest(){
        role0.setRawPermissions(0);
        InteractiveMessage message = RoleMessage.create(user, role0, channel1, comm);
        
        assertFalse(
                Permission.getPermissions(Permission.ALL_PERMISSIONS)
                        .stream()
                        .map(Permission::getName)
                        .anyMatch(message.pages.get(1).getFields().get(1).getValue()::contains));
    }
    @Test
    public void constructorIsPrivateTest() throws NoSuchMethodException, IllegalAccessException, InstantiationException, IllegalArgumentException, InvocationTargetException{
        Constructor<RoleMessage> constructor = RoleMessage.class.getDeclaredConstructor();
        assertTrue(Modifier.isPrivate(constructor.getModifiers()));
        constructor.setAccessible(true);
        constructor.newInstance();
    }
}