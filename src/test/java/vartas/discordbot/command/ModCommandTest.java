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
import java.util.Arrays;
import net.dv8tion.jda.core.Permission;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import vartas.OfflineInstance;
import vartas.xml.XMLPermission;

/**
 *
 * @author u/Zavarov
 */
public class ModCommandTest {
    XMLPermission permission;
    OfflineInstance instance;
    ModCommand command;
    
    @Before
    public void setUp(){
        instance = new OfflineInstance();
        
        permission = XMLPermission.create(new File("src/test/resources/permission.xml"));
        command = new ModCommand(Permission.ADMINISTRATOR){
            @Override
            public void execute(){
                throw new RuntimeException();
            }
        };
        command.setMessage(instance.guild_message);
        command.setBot(instance.bot);
        command.setConfig(instance.config);
        command.setParameter(Arrays.asList());
        command.setPermission(permission);
    }
    
    @Test
    public void runTest(){
        assertTrue(instance.messages.isEmpty());
        
        instance.public_role.setRawPermissions(Permission.ALL_GUILD_PERMISSIONS);
        command.run();
        
        assertFalse(instance.messages.isEmpty());
    }
    @Test(expected=MissingPermissionException.class)
    public void runFailureTest(){
        instance.guild_message.setAuthor(instance.user);
        command.run();
    }
    
    @Test
    public void checkPermissionRootTest(){
        permission.add(Rank.ROOT, instance.root);
        instance.guild_message.setAuthor(instance.root);
        command.checkPermission();
    }
    @Test
    public void checkPermissionModTest(){
        instance.guild_message.setAuthor(instance.user);
        instance.public_role.setRawPermissions(Permission.ALL_GUILD_PERMISSIONS);
        command.checkPermission();
    }
    @Test(expected=MissingPermissionException.class)
    public void checkPermissionFailureTest(){
        instance.guild_message.setAuthor(instance.user);
        command.checkPermission();
    }
    @Test
    public void canInteractRootTest(){
        permission.add(Rank.ROOT, instance.root);
        instance.guild_message.setAuthor(instance.root);
        assertTrue(command.canInteract(() -> instance.root_member.canInteract(instance.role1)));
    }
    @Test
    public void canInteractRootOwnerTest(){
        permission.add(Rank.ROOT, instance.self);
        instance.public_role.setRawPermissions(Permission.ALL_GUILD_PERMISSIONS);
        assertTrue(command.canInteract(() -> instance.self_member.canInteract(instance.role1)));
    }
    @Test
    public void canInteractOwnerTest(){
        assertTrue(command.canInteract(() -> instance.self_member.canInteract(instance.role1)));
        
    }
    @Test
    public void canInteractFailureTest() {
        assertFalse(command.canInteract(() -> instance.member.canInteract(instance.role1)));
    }
}