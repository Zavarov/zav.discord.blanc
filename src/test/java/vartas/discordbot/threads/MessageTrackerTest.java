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
package vartas.discordbot.threads;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageReaction;
import net.dv8tion.jda.core.entities.MessageReaction.ReactionEmote;
import net.dv8tion.jda.core.entities.MessageType;
import net.dv8tion.jda.core.entities.impl.GuildImpl;
import net.dv8tion.jda.core.entities.impl.JDAImpl;
import net.dv8tion.jda.core.entities.impl.MemberImpl;
import net.dv8tion.jda.core.entities.impl.ReceivedMessage;
import net.dv8tion.jda.core.entities.impl.SelfUserImpl;
import net.dv8tion.jda.core.entities.impl.TextChannelImpl;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import vartas.discordbot.comm.Communicator;
import vartas.discordbot.comm.OfflineCommunicator;
import vartas.discordbot.comm.OfflineEnvironment;
import vartas.discordbot.messages.InteractiveMessage;

/**
 *
 * @author u/Zavarov
 */
public class MessageTrackerTest {
    
    static Communicator comm;
    static GuildImpl guild0;
    static TextChannelImpl channel1;
    static SelfUserImpl user0;
    static MemberImpl member0;
    static Message message;
    @BeforeClass
    public static void create(){
        comm = new OfflineCommunicator(new OfflineEnvironment(), OfflineEnvironment.create());
        
        guild0 = new GuildImpl((JDAImpl)comm.jda(),0L);
        guild0.setName("guild0");
        ((JDAImpl)comm.jda()).getGuildMap().put(guild0.getIdLong(),guild0);
        
        channel1 = new TextChannelImpl(1L,guild0);
        channel1.setName("channel0");
        guild0.getTextChannelsMap().put(channel1.getIdLong(),channel1);
        
        user0 = new SelfUserImpl(0L,(JDAImpl)comm.jda());
        
        member0 = new MemberImpl(guild0, user0);
        guild0.getMembersMap().put(user0.getIdLong(),member0);
        guild0.setOwner(member0);
        
        ((JDAImpl)comm.jda()).getUserMap().put(user0.getIdLong(),user0);
        ((JDAImpl)comm.jda()).setSelfUser(user0);
        
        message = new ReceivedMessage(
                1L, channel1, MessageType.DEFAULT,
                false, false, null,null, false, false, 
                "content", "", user0, OffsetDateTime.now()
                ,Arrays.asList(), Arrays.asList(), Arrays.asList());
    }
    
    MessageTracker tracker;
    InteractiveMessage interactive;
    @Before
    public void setUp(){
        tracker = new MessageTracker(comm);
        InteractiveMessage.Builder builder = new InteractiveMessage.Builder(channel1, user0,comm);
        builder.nextPage();
        interactive = builder.build();
        interactive.toRestAction((m) -> {});
        interactive.accept(message);
        tracker.messages.put(0L, interactive);
    }
    @Test
    public void updateTest() throws InterruptedException{
        tracker.executor.shutdownNow();
        while(!tracker.executor.isTerminated()){}
        
        OffsetDateTime old = interactive.getLastReaction();
        Thread.sleep(10);
        tracker.update(0L, user0, new MessageReaction(channel1,new ReactionEmote("1",1L,comm.jda()),0L,false,1));
        assertNotEquals(old, interactive.getLastReaction());
        
    }
    @Test
    public void runTest(){
        tracker.executor.shutdownNow();
        while(!tracker.executor.isTerminated()){}
        
        assertEquals(tracker.messages.size(),1);
        tracker.run();
        assertEquals(tracker.messages.size(),1);
        
    }
    @Test
    public void runRemoveTest(){
        tracker.executor.shutdownNow();
        while(!tracker.executor.isTerminated()){}
        
        tracker.messages.put(0L, new FakeMessage());
        assertEquals(tracker.messages.size(),1);
        tracker.run();
        assertEquals(tracker.messages.size(),0);
        
    }
    @Test
    public void shutdownTest(){
        assertFalse(tracker.executor.isShutdown());
        tracker.shutdown();
        assertTrue(tracker.executor.isShutdown());
    }
    @Test
    public void addTest(){
        tracker.messages.clear();
        assertFalse(tracker.messages.containsValue(interactive));
        tracker.add(interactive);
        assertTrue(tracker.messages.containsValue(interactive));
    }
    //To get custom values for the oldest reaction.
    private static class FakeMessage extends InteractiveMessage{
        public FakeMessage(){
            super(null,null,null,null);
        }
        @Override
        public OffsetDateTime getLastReaction(){
            return Instant.ofEpochMilli(0).atOffset(ZoneOffset.UTC);
        }
    }
}
