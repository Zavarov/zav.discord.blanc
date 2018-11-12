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
package vartas.discordbot;

import java.time.OffsetDateTime;
import java.util.Arrays;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.MessageReaction;
import net.dv8tion.jda.core.entities.MessageReaction.ReactionEmote;
import net.dv8tion.jda.core.entities.MessageType;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.impl.GuildImpl;
import net.dv8tion.jda.core.entities.impl.JDAImpl;
import net.dv8tion.jda.core.entities.impl.MemberImpl;
import net.dv8tion.jda.core.entities.impl.PrivateChannelImpl;
import net.dv8tion.jda.core.entities.impl.ReceivedMessage;
import net.dv8tion.jda.core.entities.impl.RoleImpl;
import net.dv8tion.jda.core.entities.impl.SelfUserImpl;
import net.dv8tion.jda.core.entities.impl.TextChannelImpl;
import net.dv8tion.jda.core.entities.impl.UserImpl;
import net.dv8tion.jda.core.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.message.MessageUpdateEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import vartas.discordbot.comm.Communicator;
import vartas.discordbot.comm.OfflineCommunicator;
import vartas.discordbot.comm.OfflineEnvironment;
import vartas.discordbot.messages.InteractiveMessage;
import vartas.discordbot.threads.MessageTracker;
import vartas.parser.cfg.ContextFreeGrammar;
import vartas.xml.XMLServer;

/**
 *
 * @author u/Zavarov
 */
public class MessageListenerTest {
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
    static Message message3;
    static Message message4;
    static Message message5;
    static MessageTracker messages;
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
        channel2 = new PrivateChannelImpl(2L,user);
        
        jda.setSelfUser(self);
        jda.getUserMap().put(self.getIdLong(),self);
        jda.getUserMap().put(user.getIdLong(),user);
        guild.getMembersMap().put(self.getIdLong(),memberself);
        guild.getMembersMap().put(user.getIdLong(),member);
        guild.getRolesMap().put(role0.getIdLong(),role0);
        guild.setOwner(member);
        guild.setPublicRole(role0);
        role0.setRawPermissions(Permission.getRaw(Permission.MESSAGE_READ,Permission.MESSAGE_WRITE,Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_HISTORY));
        user.setPrivateChannel(channel2);
        
        guild.setName("guild");
        
        message1 = new ReceivedMessage(
                1L, channel1, MessageType.DEFAULT,
                false, false, null,null, false, false, 
                "prefixab", "", self, OffsetDateTime.now()
                ,Arrays.asList(), Arrays.asList(), Arrays.asList());
        
        message2 = new ReceivedMessage(
                1L, channel2, MessageType.DEFAULT,
                false, false, null,null, false, false, 
                "\\ab", "", self, OffsetDateTime.now()
                ,Arrays.asList(), Arrays.asList(), Arrays.asList());
        
        message3 = new ReceivedMessage(
                1L, channel1, MessageType.DEFAULT,
                false, false, null,null, false, false, 
                "word", "", self, OffsetDateTime.now()
                ,Arrays.asList(), Arrays.asList(), Arrays.asList());
        
        message4 = new ReceivedMessage(
                1L, channel1, MessageType.DEFAULT,
                false, false, null,null, false, false, 
                "word", "", user, OffsetDateTime.now().minusMonths(1)
                ,Arrays.asList(), Arrays.asList(), Arrays.asList());
        
        message5 = new ReceivedMessage(
                1L, channel1, MessageType.DEFAULT,
                false, false, null,null, false, false, 
                "\\ab", "", user, OffsetDateTime.now().minusMonths(1)
                ,Arrays.asList(), Arrays.asList(), Arrays.asList());
        
        server = comm.server(guild);
        
        ContextFreeGrammar grammar = new ContextFreeGrammar.Builder()
                .addTerminal("a")
                .addTerminal("b")
                .addNonterminal("Command")
                .setStartSymbol("Command")
                .addProduction("Command", "a","b").build();
        comm.environment().grammar().putAll(grammar);
        comm.environment().command().put("ab", "vartas.discordbot.command.TestCommand");
        
        messages = new MessageTracker(comm);
    }
    
    MessageListener listener;
    InteractiveMessage message;
    @Before
    public void setUp(){
        listener = new MessageListener(comm, messages);
        message = new InteractiveMessage.Builder(channel1, self, comm)
                .addLines(Arrays.asList("aaaaa","bbbbb"), 1)
                .build();
        message.toRestAction((c) -> {});
        message.accept(message1);
        listener.messages.add(message);
        
        self.setBot(false);
        
        comm.actions.clear();
        comm.discord.clear();
    }
    @Test
    public void shutdownTest(){
        assertFalse(listener.executor.isShutdown());
        listener.shutdown();
        assertTrue(listener.executor.isShutdown());
    }
    @Test
    public void onMessageReactionAddTest(){
        listener.onMessageReactionAdd(
                new MessageReactionAddEvent(
                    jda,
                    0,
                    self,
                    new MessageReaction(channel1,new ReactionEmote(InteractiveMessage.ARROW_LEFT,0L,jda),message1.getIdLong(),true,1)
        ));
        assertEquals(comm.actions,Arrays.asList("action queued"));
    }
    @Test
    public void onMessageReactionAddBotTest(){
        self.setBot(true);
        listener.onMessageReactionAdd(
                new MessageReactionAddEvent(
                    jda,
                    0,
                    self,
                    new MessageReaction(channel1,new ReactionEmote(InteractiveMessage.ARROW_LEFT,0L,jda),message1.getIdLong(),true,1)
        ));
        assertTrue(comm.discord.get(channel1).isEmpty());
    }
    @Test
    public void onGuildMessageReceivedRemoveSelfMessageTest(){
        listener.onGuildMessageReceived(new GuildMessageReceivedEvent(jda,0,message3));
        assertTrue(comm.actions.isEmpty());
    }
    @Test
    public void onGuildMessageReceivedRemoveMessageTest(){
        Communicator fake = new OfflineCommunicator(comm.environment(),comm.jda()){
            @Override
            public void delete(MessageChannel channel, long id){
                comm.actions.add("deleted");
            }
        };
        listener = new MessageListener(fake, messages);
        listener.onGuildMessageReceived(new GuildMessageReceivedEvent(jda,0,message4));
        assertEquals(comm.actions,Arrays.asList("deleted"));
    }
    @Test
    public void onGuildMessageReceivedDontRemoveMessageTest(){
        listener.onGuildMessageReceived(new GuildMessageReceivedEvent(jda,0,message5));
        assertTrue(comm.actions.isEmpty());
    }
    @Test
    public void onGuildMessageReceivedTest(){
        Communicator fake = new OfflineCommunicator(comm.environment(),comm.jda()){
            @Override
            public void activity(TextChannel channel){
                comm.actions.add("updated");
            }
        };
        listener = new MessageListener(fake, messages);
        listener.onGuildMessageReceived(new GuildMessageReceivedEvent(jda,0,message1));
        assertEquals(comm.actions,Arrays.asList("updated"));
    }
    @Test
    public void onGuildMessageReceivedIsBotTest(){
        Communicator fake = new OfflineCommunicator(comm.environment(),comm.jda()){
            @Override
            public void activity(TextChannel channel){
                comm.actions.add("updated");
            }
        };
        self.setBot(true);
        listener = new MessageListener(fake, messages);
        listener.onGuildMessageReceived(new GuildMessageReceivedEvent(jda,0,message1));
        assertTrue(comm.actions.isEmpty());
    }
    @Test
    public void onGuildLeaveTest(){
        listener.onGuildLeave(new GuildLeaveEvent(jda,0, guild));
        assertEquals(comm.actions, Arrays.asList("guild deleted"));
    }
    @Test
    public void onMessageUpdateTest(){
        Communicator fake = new OfflineCommunicator(comm.environment(),comm.jda()){
            @Override
            public void submit(Runnable runnable){
                comm.actions.add("submitted");
            }
        };
        listener = new MessageListener(fake, messages);
        
        listener.onMessageUpdate(new MessageUpdateEvent(jda,0,message1));
        
        listener.executor.shutdown();
        while(!listener.executor.isTerminated()){}

        assertEquals(comm.actions, Arrays.asList("submitted"));
    }
    @Test
    public void onMessageUpdateTooOldTest(){
        Communicator fake = new OfflineCommunicator(comm.environment(),comm.jda()){
            @Override
            public void submit(Runnable runnable){
                comm.actions.add("submitted");
            }
        };
        listener = new MessageListener(fake, messages);
        
        listener.onMessageUpdate(new MessageUpdateEvent(jda,0,message5));
        
        listener.executor.shutdown();
        while(!listener.executor.isTerminated()){}

        assertTrue(comm.actions.isEmpty());
    }
    @Test
    public void onMessageReceivedTest(){
        Communicator fake = new OfflineCommunicator(comm.environment(),comm.jda()){
            @Override
            public void submit(Runnable runnable){
                comm.actions.add("submitted");
            }
        };
        listener = new MessageListener(fake, messages);
        
        listener.onMessageReceived(new MessageReceivedEvent(jda,0,message1));
        
        listener.executor.shutdown();
        while(!listener.executor.isTerminated()){}

        assertEquals(comm.actions, Arrays.asList("submitted"));
    }
    @Test
    public void onMessageReceivedBotTest(){
        Communicator fake = new OfflineCommunicator(comm.environment(),comm.jda()){
            @Override
            public void submit(Runnable runnable){
                comm.actions.add("submitted");
            }
        };
        self.setBot(true);
        listener = new MessageListener(fake, messages);
        
        listener.onMessageReceived(new MessageReceivedEvent(jda,0,message1));
        
        listener.executor.shutdown();
        while(!listener.executor.isTerminated()){}

        assertTrue(comm.actions.isEmpty());
    }
    @Test
    public void onMessageReceivedNoPrefixTest(){
        Communicator fake = new OfflineCommunicator(comm.environment(),comm.jda()){
            @Override
            public void submit(Runnable runnable){
                comm.actions.add("submitted");
            }
        };
        fake.server(guild).setPrefix(null);
        listener = new MessageListener(fake, messages);
        
        listener.onMessageReceived(new MessageReceivedEvent(jda,0,message5));
        
        listener.executor.shutdown();
        while(!listener.executor.isTerminated()){}

        assertEquals(comm.actions, Arrays.asList("submitted"));
    }
    @Test
    public void onMessageReceivedNoGuild(){
        Communicator fake = new OfflineCommunicator(comm.environment(),comm.jda()){
            @Override
            public void submit(Runnable runnable){
                comm.actions.add("submitted");
            }
        };
        listener = new MessageListener(fake, messages);
        
        listener.onMessageReceived(new MessageReceivedEvent(jda,0,message2));
        
        listener.executor.shutdown();
        while(!listener.executor.isTerminated()){}

        assertEquals(comm.actions, Arrays.asList("submitted"));
    }
    @Test
    public void onMessageReceivedNoCommand(){
        Communicator fake = new OfflineCommunicator(comm.environment(),comm.jda()){
            @Override
            public void submit(Runnable runnable){
                comm.actions.add("submitted");
            }
        };
        listener = new MessageListener(fake, messages);
        
        listener.onMessageReceived(new MessageReceivedEvent(jda,0,message4));
        
        listener.executor.shutdown();
        while(!listener.executor.isTerminated()){}

        assertTrue(comm.actions.isEmpty());
    }
}