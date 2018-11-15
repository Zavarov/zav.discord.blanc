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

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.function.Consumer;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageReaction;
import net.dv8tion.jda.core.entities.MessageReaction.ReactionEmote;
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
import net.dv8tion.jda.core.requests.RestAction;
import static org.junit.Assert.assertEquals;
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
public class InteractiveMessageTest {
    static OfflineEnvironment environment;
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
    static Consumer<Consumer<?>> handler = (v) -> {};
    @BeforeClass
    public static void startUp(){
        environment = new OfflineEnvironment();
        comm = new OfflineCommunicator(environment, OfflineEnvironment.create()){
            @Override
            public <T> void send(RestAction<T> action, Consumer<T> success){
                super.send(action, success);
                handler.accept(success);
            }
        };
        
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
        guild.setOwner(memberself);
        guild.setPublicRole(role0);
        role0.setRawPermissions(Permission.getRaw(Permission.MESSAGE_READ,Permission.MESSAGE_WRITE,Permission.MESSAGE_EMBED_LINKS));
        user.setPrivateChannel(channel2);
        
        message1 = new ReceivedMessage(
                1L, channel1, MessageType.DEFAULT,
                false, false, null,null, false, false, 
                "content", "", self, OffsetDateTime.now()
                ,Arrays.asList(), Arrays.asList(), Arrays.asList());
        
        message2 = new ReceivedMessage(
                1L, channel2, MessageType.DEFAULT,
                false, false, null,null, false, false, 
                "content", "", self, OffsetDateTime.now()
                ,Arrays.asList(), Arrays.asList(), Arrays.asList());
        
        server = comm.server(guild);
    }
    
    InteractiveMessage interactive;
    @Before
    public void setUp(){
        handler = (v) -> {};
        InteractiveMessage.Builder builder = new InteractiveMessage.Builder(channel1, self, comm);
        builder.addLines(Arrays.asList("a","b","c","d","e"), 2);
        interactive = builder.build();
        interactive.current_message = message1;
        
        comm.actions.clear();
        comm.discord.clear();
    }
    
    @Test
    public void addAndUpdateTest(){
        comm = new OfflineCommunicator(comm.environment(),comm.jda()){
            @Override
            @SuppressWarnings("unchecked")
            public <T> void send(RestAction<T> action, Consumer<T> success, Consumer<Throwable> failure){
                success.accept((T)message2);
            }
        };
        interactive.comm = comm;
        interactive.current_message = message1;
        interactive.add(self, 
                new MessageReaction(
                        channel2,
                        new ReactionEmote(InteractiveMessage.ARROW_RIGHT,0L,jda),
                        message2.getIdLong(),
                        true,
                        1
                )
        );
        assertEquals(interactive.current_message, message2);
        startUp();
    }
    @Test
    public void addRightSinglePageTest(){
        interactive.pages.retainAll(Arrays.asList(interactive.pages.get(0)));
        
        interactive.add(self, 
                new MessageReaction(
                        channel1,
                        new ReactionEmote(InteractiveMessage.ARROW_RIGHT,0L,jda),
                        message1.getIdLong(),
                        true,
                        1
                )
        );
        assertEquals(interactive.current_page,0);
    }
    @Test
    public void addRightMissingPermissionTest(){
        interactive.current_message = message2;
        interactive.add(self, 
                new MessageReaction(
                        channel2,
                        new ReactionEmote(InteractiveMessage.ARROW_RIGHT,0L,jda),
                        message2.getIdLong(),
                        true,
                        1
                )
        );
        assertEquals(interactive.current_page,1);
    }
    @Test
    public void addRightTest(){
        interactive.add(self, 
                new MessageReaction(
                        channel1,
                        new ReactionEmote(InteractiveMessage.ARROW_RIGHT,0L,jda),
                        message1.getIdLong(),
                        true,
                        1
                )
        );
        assertEquals(interactive.current_page,1);
    }
    @Test
    public void addRightBacktrackTest(){
        interactive.current_page = interactive.pages.size()-1;
        interactive.add(self, 
                new MessageReaction(
                        channel1,
                        new ReactionEmote(InteractiveMessage.ARROW_RIGHT,0L,jda),
                        message1.getIdLong(),
                        true,
                        1
                )
        );
        assertEquals(interactive.current_page,0);
    }
    @Test
    public void addLeftSinglePageTest(){
        interactive.pages.retainAll(Arrays.asList(interactive.pages.get(0)));
        
        interactive.add(self, 
                new MessageReaction(
                        channel1,
                        new ReactionEmote(InteractiveMessage.ARROW_LEFT,0L,jda),
                        message1.getIdLong(),
                        true,
                        1
                )
        );
        assertEquals(interactive.current_page,0);
    }
    @Test
    public void addLeftTest(){
        interactive.add(self, 
                new MessageReaction(
                        channel1,
                        new ReactionEmote(InteractiveMessage.ARROW_LEFT,0L,jda),
                        message1.getIdLong(),
                        true,
                        1
                )
        );
        assertEquals(interactive.current_page,interactive.pages.size()-1);
    }
    @Test
    public void addLeftBacktrackTest(){
        interactive.add(self, 
                new MessageReaction(
                        channel1,
                        new ReactionEmote(InteractiveMessage.ARROW_LEFT,0L,jda),
                        message1.getIdLong(),
                        true,
                        1
                )
        );
        assertEquals(interactive.current_page,interactive.pages.size()-1);
    }
    @Test
    public void addJunkTest(){
        interactive.add(self, 
                new MessageReaction(
                        channel1,
                        new ReactionEmote(":D",0L,jda),
                        message1.getIdLong(),
                        true,
                        1
                )
        );
        assertEquals(interactive.current_page,0);
    }
    @Test
    public void addDifferentUserTest(){
        interactive.add(user, 
                new MessageReaction(
                        channel1,
                        new ReactionEmote(InteractiveMessage.ARROW_RIGHT,0L,jda),
                        message1.getIdLong(),
                        true,
                        1
                )
        );
        assertEquals(interactive.current_page,0);
    }
    @Test
    public void addMissingPermissionChannel(){
        guild.setOwner(member);
        
        interactive.add(self, 
                new MessageReaction(
                        channel1,
                        new ReactionEmote(InteractiveMessage.ARROW_RIGHT,0L,jda),
                        message1.getIdLong(),
                        true,
                        1
                )
        );
        assertEquals(interactive.current_page,1);
    }
    @Test
    public void getCurrentMessageTest(){
        assertEquals(interactive.current_message,interactive.getCurrentMessage());
    }
    @Test
    public void getLastReactionTest(){
        assertEquals(interactive.last_reaction,interactive.getLastReaction());
    }
    @Test
    public void toRestActionTest(){
        assertTrue(interactive.toRestAction(c -> {}) instanceof RestAction);
    }
    @Test
    public void acceptTest(){
        handler = (v) -> v.accept(null);
        interactive.consumer = (c) -> comm.actions.add("success");
        
        interactive.accept(message1);
        assertEquals(comm.actions,Arrays.asList("action queued","action queued","success"));
    }
}