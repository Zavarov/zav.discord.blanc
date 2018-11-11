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
import java.lang.reflect.InvocationTargetException;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Collections;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.Message.Attachment;
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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import vartas.discordbot.comm.OfflineCommunicator;
import vartas.discordbot.comm.OfflineEnvironment;
import vartas.parser.ast.AbstractSyntaxTree;
import vartas.parser.cfg.ContextFreeGrammar.Builder.Terminal;
import vartas.parser.cfg.ContextFreeGrammar.Type;
import vartas.xml.XMLServer;

/**
 *
 * @author u/Zavarov
 */
public class CommandTest {
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
    
    Command command;
    @Before
    public void setUp(){
        command = new Command(){
            @Override
            protected void execute(){
                throw new RuntimeException();
            }
        };
        command.setMessage(message1);
        command.setParameter(Collections.emptyList());
        command.setCommunicator(comm);
        
        comm.actions.clear();
        comm.discord.clear();
    }
    
    @Test
    public void filterTest(){
        AbstractSyntaxTree.Builder b = new AbstractSyntaxTree.Builder();
        b.descent("a", "integer", Type.TERMINAL);
        b.descent("b", "integer", Type.NONTERMINAL);
        command.setParameter(b.build());
        assertEquals(command.filter(),Lists.newArrayList(new Terminal("a","integer")));
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
    public void setCommunicatorTest(){
        assertNotNull(command.message);
        command.setMessage(null);
        assertNull(command.message);
    }
    
    @Test
    public void runExpectedExceptionTest(){
        command = new Command() {
            @Override
            protected void execute(){throw new CommandRequiresGuildException();}
        };
        command.setMessage(message2);
        command.setParameter(Arrays.asList());
        command.setCommunicator(comm);
        command.run();
        assertTrue(comm.discord.get(channel2).get(0).getContentRaw().contains("This command can only be executed inside of a guild."));
        assertFalse(comm.discord.get(channel2).get(0).getContentRaw().contains("vartas.discordbot.command"));
    }
    
    @Test
    public void runExceptionTest(){
        command.run();
        assertEquals(comm.actions,Arrays.asList("action queued"));
    }
    
    @Test(expected=CommandRequiresGuildException.class)
    public void requiresGuildInvalidTest(){
        command.setMessage(message2);
        command.requiresGuild();
    }
    
    @Test
    public void requiresGuildValidTest(){
        command.requiresGuild();
    }
    
    @Test(expected=CommandRequiresAttachmentException.class)
    public void requiresAttachmentInvalidTest(){
        command.requiresAttachment();
    }
    
    @Test
    public void requiresAttachmentValidTest(){
        message1 = new ReceivedMessage(
                1L, channel1, MessageType.DEFAULT,
                false, false, null,null, false, false, 
                "content", "", self, OffsetDateTime.now()
                ,Arrays.asList(), Arrays.asList(new Attachment(0,"","","",0,0,0,jda)), Arrays.asList());
        command.setMessage(message1);
        command.requiresAttachment();
    }
    @Test(expected=MissingRankException.class)
    public void checkRankMissingRankTest(){
        command.ranks.clear();
        command.ranks.add(Rank.ROOT);
        command.checkRank();
    }
    @Test
    public void checkRankOrderTest(){
        try{
            command.ranks.clear();
            command.ranks.add(Rank.ROOT);
            command.ranks.add(Rank.DEVELOPER);
            command.checkRank();
            throw new IllegalStateException("The test failed when it reached this state.");
        }catch(MissingRankException e){
            assertEquals(e.getMessage(),"You need to have the Developer or any higher rank to execute this command.");
        }
    }
    @Test
    public void createCommandTest() throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
        command = Command.createCommand("vartas.discordbot.command.TestCommand", message1, new AbstractSyntaxTree.Builder().build(), comm);
        assertTrue(command instanceof TestCommand);
    }
}