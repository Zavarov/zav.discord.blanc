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

import com.google.common.collect.Lists;
import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import net.dv8tion.jda.core.entities.Message.Attachment;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.entities.impl.GuildImpl;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import vartas.OfflineInstance;
import vartas.parser.ast.AbstractSyntaxTree;
import vartas.parser.cfg.ContextFreeGrammar.Builder.Terminal;
import vartas.parser.cfg.ContextFreeGrammar.Type;
import vartas.xml.XMLPermission;

/**
 *
 * @author u/Zavarov
 */
public class CommandTest {
    Command command;
    OfflineInstance instance;
    @Before
    public void setUp(){
        instance = new OfflineInstance();
        
        command = new Command(){
            @Override
            protected void execute(){
                throw new RuntimeException();
            }
        };
        command.setMessage(instance.guild_message);
        command.setParameter(Collections.emptyList());
        command.setBot(instance.bot);
        command.setConfig(instance.config);
        command.setPermission(XMLPermission.create(new File("src/test/resources/permission.xml")));
    }
    
    @Test
    public void filterTest(){
        AbstractSyntaxTree.Builder b = new AbstractSyntaxTree.Builder();
        b.descent("a", "a", Type.TERMINAL);
        b.descent("b", "b", Type.NONTERMINAL);
        command.setParameter(b.build());
        assertEquals(command.filter(),Lists.newArrayList(new Terminal("a")));
    }
    
    @Test
    public void setParameterTest(){
        AbstractSyntaxTree.Builder b = new AbstractSyntaxTree.Builder();
        b.descent("c", "t", Type.TERMINAL);
        
        assertTrue(command.parameter.isEmpty());
        command.setParameter(b.build());
        assertFalse(command.parameter.isEmpty());
        assertTrue(command.parameter.contains(new Terminal("c","t")));
    }
    
    @Test
    public void setMessageTest(){
        assertNotNull(command.message);
        command.setMessage(null);
        assertNull(command.message);
    }
    
    @Test
    public void setBotTest(){
        assertNotNull(command.bot);
        command.setBot(null);
        assertNull(command.bot);
    }
    
    @Test
    public void setConfigTest(){
        assertNotNull(command.config);
        command.setConfig(null);
        assertNull(command.config);
    }
    
    @Test
    public void getDefaultUserTest(){
        Set<User> user = command.getDefaultUser(Arrays.asList(new Terminal(instance.self.getId(),"integer")));
        assertEquals(user.size(),1);
        assertTrue(user.contains(instance.self));
    }
    
    @Test
    public void getDefaultEmptyUserTest(){
        Set<User> user = command.getDefaultUser(Arrays.asList());
        assertEquals(user.size(),1);
        assertTrue(user.contains(instance.self));
    }
    
    @Test
    public void getUserTest(){
        assertTrue(command.getUser(Arrays.asList(new Terminal(instance.self.getName(),"quotation"))).contains(instance.self) );
    }
    
    @Test
    public void getRoleByNameTest(){
        assertTrue(command.getRole(Arrays.asList(new Terminal(instance.role1.getName(),"quotation"))).contains(instance.role1) );
    }
    
    @Test
    public void getRoleByIdTest(){
        assertTrue(command.getRole(Arrays.asList(new Terminal(instance.role1.getId(),"integer"))).contains(instance.role1) );
    }
    
    @Test
    public void getDefaultMemberTest(){
        assertTrue(command.getDefaultMember(Arrays.asList()).contains(instance.self_member) );
        assertTrue(command.getDefaultMember(Arrays.asList(new Terminal(instance.self.getId(),"integer"))).contains(instance.self_member));
    }
    
    @Test
    public void getDefaultMemberOutsideGuildTest(){
        command.setMessage(instance.private_message);
        assertTrue(command.getDefaultMember(Arrays.asList(new Terminal(instance.self.getId(),"integer"))).isEmpty());
    }
    
    @Test
    public void getMemberTest(){
        assertTrue(command.getMember(Arrays.asList(new Terminal(instance.member.getEffectiveName(),"quotation"))).contains(instance.member));
    }
    
    @Test
    public void getMemberOutsideGuildTest(){
        command.setMessage(instance.private_message);
        assertTrue(command.getMember(Arrays.asList()).isEmpty());
    }
    
    @Test
    public void getDefaultTextChannelTest(){
        assertTrue(command.getDefaultTextChannel(Arrays.asList()).contains(instance.channel1));
        assertTrue(command.getDefaultTextChannel(Arrays.asList(new Terminal(instance.channel1.getName(),"quotation"))).contains(instance.channel1));
        command.setMessage(instance.private_message);
        assertTrue(command.getDefaultTextChannel(Arrays.asList()).isEmpty());
    }
    
    @Test
    public void getTextChannelByNameTest(){
        assertTrue(command.getTextChannel(Arrays.asList(new Terminal(instance.channel1.getName(),"quotation"))).contains(instance.channel1));
    }
    
    @Test
    public void getTextChannelByIdTest(){
        assertTrue(command.getTextChannel(Arrays.asList(new Terminal(instance.channel1.getId(),"integer"))).contains(instance.channel1));
    }
    
    @Test
    public void getDefaultGuildTest(){
        assertTrue(command.getDefaultGuild(Arrays.asList()).contains(instance.guild));
        assertTrue(command.getDefaultGuild(Arrays.asList(new Terminal(instance.guild.getId(),"integer"))).contains(instance.guild) );
    }
    
    @Test
    public void getDefaultGuildOutsideGuildTest(){
        command.setMessage(instance.private_message);
        assertTrue(command.getDefaultGuild(Arrays.asList()).isEmpty() );
    }
    
    @Test
    public void getGuildTest(){
        assertTrue(command.getGuild(Arrays.asList(new Terminal(instance.guild.getId(),"integer"))).contains(instance.guild) );
    }
    
    @Test
    public void runExpectedExceptionTest(){
        command = new Command() {
            @Override
            protected void execute(){throw new CommandRequiresGuildException();}
        };
        command.setMessage(instance.private_message);
        command.setParameter(Arrays.asList());
        command.setBot(instance.bot);
        command.setConfig(instance.config);
        command.setPermission(XMLPermission.create(new File("src/test/resources/permission.xml")));
        command.run();
        assertTrue(instance.messages.get(0).getContentRaw().contains("This command can only be executed inside of a guild."));
        assertFalse(instance.messages.get(0).getContentRaw().contains("vartas.discordbot.command"));
    }
    
    @Test
    public void runExceptionTest() throws InterruptedException{
        command.run();
        assertTrue(instance.messages.get(0).getEmbeds().get(0).getFields().get(0).getValue().contains("RuntimeException"));
        assertTrue(instance.messages.get(0).getEmbeds().get(0).getFields().get(0).getValue().contains("vartas.discordbot.command"));
    }
    
    @Test(expected=CommandRequiresGuildException.class)
    public void requiresGuildInvalidTest(){
        command.setMessage(instance.private_message);
        command.requiresGuild();
    }
    
    @Test
    public void requiresGuildValidTest(){
        command.requiresGuild();
    }
    
    @Test(expected=CommandRequiresAttachmentException.class)
    public void requiresAttachmentInvalidTest(){
        instance.guild_message.setAttachments(Arrays.asList());
        command.requiresAttachment();
    }
    
    @Test
    public void requiresAttachmentValidTest(){
        instance.guild_message.setAttachments(Arrays.asList(new Attachment(0,"","","",0,0,0,instance.jda)));
        command.requiresAttachment();
    }
    
    @Test(expected=UnknownEntityException.class)
    public void getEntityInvalidIdTest(){
        command.getGuild(Arrays.asList(new Terminal("100","integer")));
    }
    
    @Test(expected=UnknownEntityException.class)
    public void getEntityUnknownNameTest(){
        command.getGuild(Arrays.asList(new Terminal("invalid name","quotation")));
    }
    
    @Test(expected=AmbiguousNameException.class)
    public void getEntityAmbiguousNameTest(){
        GuildImpl g = new GuildImpl(instance.jda,100);
        g.setName("guild");
        instance.jda.getGuildMap().put(100, g);
        command.getGuild(Arrays.asList(new Terminal("guild","quotation")));
    }
    @Test(expected=MissingRankException.class)
    public void checkRankMissingRankTest(){
        command.ranks.clear();
        command.ranks.add(Rank.ROOT);
        command.checkRank();
    }
}