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
import java.util.List;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.impl.GuildImpl;
import net.dv8tion.jda.core.entities.impl.JDAImpl;
import net.dv8tion.jda.core.entities.impl.MemberImpl;
import net.dv8tion.jda.core.entities.impl.RoleImpl;
import net.dv8tion.jda.core.entities.impl.SelfUserImpl;
import net.dv8tion.jda.core.entities.impl.TextChannelImpl;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
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
public class MemberMessageTest {
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
    public void createWithoutNicknameTest(){
        memberself.setNickname(null);
        InteractiveMessage message = MemberMessage.create(self, memberself, channel1, comm);
        assertFalse(message.pages.get(0).getFields().get(1).getValue().contains("Nickname"));
    }
    @Test
    public void createWithNicknameTest(){
        InteractiveMessage message = MemberMessage.create(self, memberself, channel1, comm);
        assertTrue(message.pages.get(0).getFields().get(1).getValue().contains("Nickname"));
    }
    @Test
    public void createWithColorTest(){
        memberself = new MemberImpl(guild,self){
            @Override
            public Color getColor(){
                return Color.RED;
            }
        };
        InteractiveMessage message = MemberMessage.create(self, memberself, channel1, comm);
        assertTrue(message.pages.get(0).getFields().get(1).getValue().contains("Color"));
        assertTrue(message.pages.get(0).getFields().get(1).getValue().contains("0xFF0000"));
    }
    @Test
    public void createWithoutColorTest(){
        InteractiveMessage message = MemberMessage.create(self, memberself, channel1, comm);
        assertFalse(message.pages.get(0).getFields().get(1).getValue().contains("Color"));
    }
    @Test
    public void createWithRoleTest(){
        memberself = new MemberImpl(guild,self){
            @Override
            public List<Role> getRoles(){
                return Arrays.asList(role0);
            }
        };
        InteractiveMessage message = MemberMessage.create(self, memberself, channel1, comm);
        assertTrue(message.pages.get(0).getFields().get(1).getValue().contains("#Roles"));
    }
    @Test
    public void createWithoutRoleTest(){
        InteractiveMessage message = MemberMessage.create(self, memberself, channel1, comm);
        assertTrue(message.pages.get(0).getFields().get(1).getValue().contains("#Roles     : 0"));
    }
    @Test
    public void createNoGameTest(){
        memberself.setGame(null);
        InteractiveMessage message = MemberMessage.create(self, memberself, channel1, comm);
        assertFalse(message.pages.get(0).getFields().get(1).getValue().contains("Playing"));
        assertFalse(message.pages.get(0).getFields().get(1).getValue().contains("Streaming"));
        assertFalse(message.pages.get(0).getFields().get(1).getValue().contains("Watching"));
        assertFalse(message.pages.get(0).getFields().get(1).getValue().contains("Listening"));
    }
    @Test
    public void createCustomGameTest(){
        memberself.setGame(Game.streaming("game", "https://www.twitch.tv/user"));
        InteractiveMessage message = MemberMessage.create(self, memberself, channel1, comm);
        assertTrue(message.pages.get(0).getFields().get(1).getValue().contains("Streaming"));
    }
    @Test
    public void createDefaultGameTest(){
        memberself.setGame(Game.playing("game"));
        InteractiveMessage message = MemberMessage.create(self, memberself, channel1, comm);
        assertTrue(message.pages.get(0).getFields().get(1).getValue().contains("Playing"));
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
        InteractiveMessage message = MemberMessage.create(self, memberself, channel1, comm);
        assertEquals(message.pages.get(0).getThumbnail().getUrl(),"http://image.png");
    }
    @Test
    public void createNoThumbnailTest(){
        InteractiveMessage message = MemberMessage.create(self, memberself, channel1, comm);
        assertNull(message.pages.get(0).getThumbnail());
    }
    @Test
    public void constructorIsPrivateTest() throws NoSuchMethodException, IllegalAccessException, InstantiationException, IllegalArgumentException, InvocationTargetException{
        Constructor<MemberMessage> constructor = MemberMessage.class.getDeclaredConstructor();
        assertTrue(Modifier.isPrivate(constructor.getModifiers()));
        constructor.setAccessible(true);
        constructor.newInstance();
    }
}