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
package vartas.discordbot.comm;

import java.time.OffsetDateTime;
import java.util.Arrays;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageType;
import net.dv8tion.jda.core.entities.impl.GuildImpl;
import net.dv8tion.jda.core.entities.impl.JDAImpl;
import net.dv8tion.jda.core.entities.impl.ReceivedMessage;
import net.dv8tion.jda.core.entities.impl.TextChannelImpl;
import net.dv8tion.jda.core.entities.impl.UserImpl;
import net.dv8tion.jda.core.requests.RestAction;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author u/Zavarov
 */
public class OfflineCommunicatorTest {
    static OfflineCommunicator comm;
    static GuildImpl guild;
    static UserImpl user;
    static TextChannelImpl channel;
    static Message message;
    static MessageBuilder builder;
    @BeforeClass
    public static void setUp(){
        comm = (OfflineCommunicator)new OfflineEnvironment().shards.get(0);
        user = new UserImpl(1L,(JDAImpl)comm.jda);
        ((JDAImpl)comm.jda).getUserMap().put(user.getIdLong(),user);
        guild = new GuildImpl((JDAImpl)comm.jda,1L);
        ((JDAImpl)comm.jda).getGuildMap().put(guild.getIdLong(),guild);
        channel = new TextChannelImpl(1L,guild);
        guild.getTextChannelsMap().put(channel.getIdLong(),channel);
        builder = new MessageBuilder().setContent("message");
        message = new ReceivedMessage(
                1L, channel, MessageType.DEFAULT,
                false, false, null,null, false, false, 
                "content", "", user, OffsetDateTime.now()
                ,Arrays.asList(), Arrays.asList(), Arrays.asList());
    }
    @Before
    public void cleanUp(){
        comm.actions.clear();
        comm.discord.clear();
    }
    @Test
    public void deleteTest(){
        comm.delete(guild);
        assertEquals(comm.actions,Arrays.asList(guild.getName()+" deleted"));
    }
    @Test
    public void updateTest(){
        comm.update(guild);
        assertEquals(comm.actions,Arrays.asList(guild.getName()+" updated"));
    }
    @Test
    public void updatePermissionTEst(){
        comm.update(comm.environment.permission());
        assertEquals(comm.actions,Arrays.asList("Permission file updated"));
    }
    @Test
    public void deleteMessageTest(){
        comm.discord.put(channel, message);
        
        assertTrue(comm.discord.containsValue(message));
        comm.delete(channel, message.getIdLong()+1);
        assertTrue(comm.discord.containsValue(message));
        comm.delete(channel, message.getIdLong());
        assertFalse(comm.discord.containsValue(message));
    }
    @Test
    public void sendTest(){
        assertTrue(comm.discord.isEmpty());
        
        comm.send(channel, builder, null, null);
        
        assertEquals(comm.discord.get(channel).size(),1);
        assertTrue(comm.actions.isEmpty());
    }
    @Test
    public void sendSuccessTest(){
        assertTrue(comm.discord.isEmpty());
        
        comm.send(channel, builder, (m) -> comm.actions.add("success"), null);
        
        assertEquals(comm.discord.get(channel).size(),1);
        assertEquals(comm.actions,Arrays.asList("success"));
    }
    @Test
    public void sendRestactionTest(){
        RestAction<Void> action = new RestAction.EmptyRestAction<>(comm.jda,null);
        comm.send(action,null,null);
        
        assertEquals(comm.actions,Arrays.asList("action queued"));
    }
}
