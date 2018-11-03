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
import net.dv8tion.jda.core.entities.impl.RoleImpl;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import vartas.OfflineInstance;

/**
 *
 * @author u/Zavarov
 */
public class RoleMessageTest {
    OfflineInstance instance;
    
    @Before
    public void setUp(){
        instance = new OfflineInstance();
    }
    @Test
    public void createWithRolesTest(){
        GuildImpl guild = new GuildImpl(instance.jda,0L){
            @Override
            public List<Member> getMembersWithRoles(Role... role){
                return Arrays.asList(instance.member,instance.self_member);
            }
            @Override
            public List<Member> getMembersWithRoles(Collection<Role> role){
                return Arrays.asList(instance.member,instance.self_member);
            }
        };
        instance.role1 = new RoleImpl(instance.role1.getIdLong(),guild);
        guild.getRolesMap().put(instance.role1.getIdLong(), instance.role1);
        
        InteractiveMessage message = RoleMessage.create(instance.user, instance.role1, instance.channel1);
        assertTrue(message.pages.get(0).getFields().get(1).getValue().contains("`#Members   :` 2"));
    }
    @Test
    public void createWithoutRolesTest(){
        instance.role1.setRawPosition(0);
        InteractiveMessage message = RoleMessage.create(instance.user, instance.role1, instance.channel1);
        assertTrue(message.pages.get(0).getFields().get(1).getValue().contains("`#Members   :` 0"));
    }
    
    @Test
    public void createWithoutColorTest(){
        instance.role1.setColor(0);
        InteractiveMessage message = RoleMessage.create(instance.user, instance.role1, instance.channel1);
        assertFalse(message.pages.get(0).getFields().get(1).getValue().contains("Color"));
    }
    
    @Test
    public void createWithColorTest(){
        instance.role1.setColor(Color.RED.getRGB());
        InteractiveMessage message = RoleMessage.create(instance.user, instance.role1, instance.channel1);
        assertTrue(message.pages.get(0).getFields().get(1).getValue().contains("Color"));
        assertTrue(message.pages.get(0).getFields().get(1).getValue().contains("0xFF0000"));
    }
    
    @Test
    public void createWithPermissionTest(){
        instance.role1.setRawPermissions(Permission.ALL_PERMISSIONS);
        InteractiveMessage message = RoleMessage.create(instance.user, instance.role1, instance.channel1);
        assertTrue(message.pages.get(1).getFields().get(1).getValue().contains("Administrator"));
    }
    
    @Test
    public void createWithoutPermissionTest(){
        instance.role1.setRawPermissions(0);
        InteractiveMessage message = RoleMessage.create(instance.user, instance.role1, instance.channel1);
        
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