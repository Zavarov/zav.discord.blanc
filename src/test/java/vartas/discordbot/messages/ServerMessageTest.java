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
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.MessageEmbed.Field;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.impl.GuildImpl;
import net.dv8tion.jda.core.entities.impl.MemberImpl;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import vartas.OfflineInstance;

/**
 *
 * @author u/Zavarov
 */
public class ServerMessageTest {
    OfflineInstance instance;
    
    @Before
    public void setUp(){
        instance = new OfflineInstance();
    }
    
    @Test
    public void createTest(){
        instance.guild.getMembersMap().put(instance.user.getIdLong(),new MemberImpl(instance.guild,instance.user){
            @Override
            public List<Role> getRoles(){
                return Arrays.asList(instance.role3);
            }
        });
        MessageEmbed message = ServerMessage.create(instance.guild);
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
        instance.self.setBot(true);
        instance.guild.getMembersMap().put(instance.user.getIdLong(),new MemberImpl(instance.guild,instance.user){
            @Override
            public List<Role> getRoles(){
                return Arrays.asList(instance.role3);
            }
        });
        MessageEmbed message = ServerMessage.create(instance.guild);
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
        instance.guild.getMembersMap().put(instance.user.getIdLong(), new MemberImpl(instance.guild,instance.user){
            @Override
            public List<Role> getRoles(){
                return Arrays.asList(instance.role1);
            }
        });
        instance.role1.setRawPermissions(Permission.BAN_MEMBERS.getRawValue());
        MessageEmbed message = ServerMessage.create(instance.guild);
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
        instance.guild = new GuildImpl(instance.jda, instance.guild.getIdLong()){
            @Override
            public String getIconUrl(){
                return "http://image.jpg";
            }
        };
        instance.member = new MemberImpl(instance.guild, instance.user);
        instance.guild.setName("guild");
        instance.guild.setAvailable(true);
        instance.guild.setOwner(instance.member);
        instance.guild.getMembersMap().put(instance.user.getIdLong(), instance.member);
        instance.jda.getGuildMap().put(instance.guild.getIdLong(), instance.guild);
        MessageEmbed message = ServerMessage.create(instance.guild);
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
        instance.public_role.setRawPermissions(Permission.BAN_MEMBERS.getRawValue());
        MessageEmbed message = ServerMessage.create(instance.guild);
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
        
        assertTrue(fields.get(5).getValue().contains(instance.member.getAsMention()));
        assertFalse(fields.get(5).getValue().contains(instance.self_member.getAsMention()));
    }
    @Test
    public void createMultipleAdminsTest(){
        instance.public_role.setRawPermissions(Permission.ADMINISTRATOR.getRawValue());
        MessageEmbed message = ServerMessage.create(instance.guild);
        List<Field> fields = message.getFields();
        assertEquals(fields.get(0).getName(),"Owner");
        assertEquals(fields.get(1).getName(),"Region");
        assertEquals(fields.get(2).getName(),"#TextChannels");
        assertEquals(fields.get(3).getName(),"#VoiceChannels");
        assertEquals(fields.get(4).getName(),"Admins");
        assertEquals(fields.get(5).getName(),"#Members");
        assertEquals(fields.get(6).getName(),"#Roles");
        assertEquals(fields.get(7).getName(),"Created");
        
        assertTrue(fields.get(4).getValue().contains(instance.member.getAsMention()));
        assertTrue(fields.get(4).getValue().contains(instance.self_member.getAsMention()));
    }
    @Test
    public void constructorIsPrivateTest() throws NoSuchMethodException, IllegalAccessException, InstantiationException, IllegalArgumentException, InvocationTargetException{
        Constructor<ServerMessage> constructor = ServerMessage.class.getDeclaredConstructor();
        assertTrue(Modifier.isPrivate(constructor.getModifiers()));
        constructor.setAccessible(true);
        constructor.newInstance();
    }
}
