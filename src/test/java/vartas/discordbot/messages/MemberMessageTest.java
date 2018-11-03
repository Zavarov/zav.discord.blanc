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
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.impl.MemberImpl;
import net.dv8tion.jda.core.entities.impl.UserImpl;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import vartas.OfflineInstance;

/**
 *
 * @author u/Zavarov
 */
public class MemberMessageTest {
    OfflineInstance instance;
    
    @Before
    public void setUp(){
        instance = new OfflineInstance();
    }
    
    @Test
    public void createWithoutNicknameTest(){
        instance.member.setNickname(null);
        InteractiveMessage message = MemberMessage.create(instance.user, instance.member, instance.channel1);
        assertFalse(message.pages.get(0).getFields().get(1).getValue().contains("Nickname"));
    }
    @Test
    public void createWithNicknameTest(){
        InteractiveMessage message = MemberMessage.create(instance.user, instance.member, instance.channel1);
        assertTrue(message.pages.get(0).getFields().get(1).getValue().contains("Nickname"));
    }
    @Test
    public void createWithColorTest(){
        instance.member = new MemberImpl(instance.guild,instance.self){
            @Override
            public Color getColor(){
                return Color.RED;
            }
        };
        InteractiveMessage message = MemberMessage.create(instance.user, instance.member, instance.channel1);
        assertTrue(message.pages.get(0).getFields().get(1).getValue().contains("Color"));
        assertTrue(message.pages.get(0).getFields().get(1).getValue().contains("0xFF0000"));
    }
    @Test
    public void createWithoutColorTest(){
        InteractiveMessage message = MemberMessage.create(instance.user, instance.member, instance.channel1);
        assertFalse(message.pages.get(0).getFields().get(1).getValue().contains("Color"));
    }
    @Test
    public void createWithRoleTest(){
        instance.member = new MemberImpl(instance.guild,instance.self){
            @Override
            public List<Role> getRoles(){
                return Arrays.asList(instance.role1);
            }
        };
        InteractiveMessage message = MemberMessage.create(instance.user, instance.member, instance.channel1);
        assertTrue(message.pages.get(0).getFields().get(1).getValue().contains("#Roles"));
    }
    @Test
    public void createWithoutRoleTest(){
        InteractiveMessage message = MemberMessage.create(instance.user, instance.member, instance.channel1);
        assertTrue(message.pages.get(0).getFields().get(1).getValue().contains("`#Roles     :` 0"));
    }
    @Test
    public void createNoGameTest(){
        instance.member.setGame(null);
        InteractiveMessage message = MemberMessage.create(instance.user, instance.member, instance.channel1);
        assertFalse(message.pages.get(0).getFields().get(1).getValue().contains("Playing"));
        assertFalse(message.pages.get(0).getFields().get(1).getValue().contains("Streaming"));
        assertFalse(message.pages.get(0).getFields().get(1).getValue().contains("Watching"));
        assertFalse(message.pages.get(0).getFields().get(1).getValue().contains("Listening"));
    }
    @Test
    public void createCustomGameTest(){
        instance.member.setGame(Game.streaming("game", "https://www.twitch.tv/user"));
        InteractiveMessage message = MemberMessage.create(instance.user, instance.member, instance.channel1);
        assertTrue(message.pages.get(0).getFields().get(1).getValue().contains("Streaming"));
    }
    @Test
    public void createDefaultGameTest(){
        instance.member.setGame(Game.playing("game"));
        InteractiveMessage message = MemberMessage.create(instance.user, instance.member, instance.channel1);
        assertTrue(message.pages.get(0).getFields().get(1).getValue().contains("Playing"));
    }
    @Test
    public void createThumbnailTest(){
        instance.user = new UserImpl(instance.user.getIdLong(), instance.jda){
            @Override
            public String getAvatarUrl(){
                return "http://image.png";
            }
        };
        instance.member = new MemberImpl(instance.guild, instance.user);
        InteractiveMessage message = MemberMessage.create(instance.user, instance.member, instance.channel1);
        assertEquals(message.pages.get(0).getThumbnail().getUrl(),"http://image.png");
    }
    @Test
    public void createNoThumbnailTest(){
        InteractiveMessage message = MemberMessage.create(instance.user, instance.member, instance.channel1);
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