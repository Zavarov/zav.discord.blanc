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
import java.time.ZoneId;
import net.dv8tion.jda.core.entities.MessageReaction;
import net.dv8tion.jda.core.entities.MessageReaction.ReactionEmote;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import vartas.OfflineInstance;
import vartas.discordbot.messages.InteractiveMessage;

/**
 *
 * @author u/Zavarov
 */
public class MessageTrackerTest {
    OfflineInstance instance;
    MessageTracker tracker;
    InteractiveMessage message;
    @Before
    public void setUp(){
        instance = new OfflineInstance();
        tracker = new MessageTracker(10);
        InteractiveMessage.Builder builder = new InteractiveMessage.Builder(instance.channel1, instance.user);
        builder.nextPage();
        message = builder.build();
        message.send((c) -> {});
        message.accept(instance.guild_message);
        tracker.put(0L, message);
    }
    @Test
    public void updateTest() throws InterruptedException{
        tracker.executor.shutdownNow();
        while(!tracker.executor.isTerminated()){}
        
        OffsetDateTime old = message.getLastReaction();
        Thread.sleep(10);
        tracker.update(0L, instance.user, new MessageReaction(instance.channel1,new ReactionEmote("1",1L,instance.jda),0L,false,1));
        assertNotEquals(old, message.getLastReaction());
        
    }
    @Test
    public void runTest(){
        tracker.executor.shutdownNow();
        while(!tracker.executor.isTerminated()){}
        
        assertEquals(tracker.size(),1);
        tracker.run();
        assertEquals(tracker.size(),1);
        
    }
    @Test
    public void runRemoveTest(){
        tracker.executor.shutdownNow();
        while(!tracker.executor.isTerminated()){}
        
        tracker.put(0L, new Message());
        assertEquals(tracker.size(),1);
        tracker.run();
        assertEquals(tracker.size(),0);
        
    }
    @Test
    public void shutdownTest(){
        assertFalse(tracker.executor.isShutdown());
        tracker.shutdown();
        assertTrue(tracker.executor.isShutdown());
    }
    @Test
    public void addTest(){
        tracker.clear();
        assertFalse(tracker.containsValue(message));
        tracker.add(message);
        assertTrue(tracker.containsValue(message));
    }
    
    
    private static class Message extends InteractiveMessage{
        Message(){
            super(null,null,null);
        }
        @Override
        public OffsetDateTime getLastReaction() {
            return OffsetDateTime.ofInstant(Instant.ofEpochSecond(0), ZoneId.of("UTC"));
        }
    }
}
