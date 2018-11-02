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

import java.util.Arrays;
import java.util.function.Consumer;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.MessageReaction;
import net.dv8tion.jda.core.entities.MessageReaction.ReactionEmote;
import net.dv8tion.jda.core.requests.restaction.MessageAction;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import vartas.OfflineInstance;
import vartas.OfflineMessage;

/**
 *
 * @author u/Zavarov
 */
public class InteractiveMessageTest {
    OfflineInstance instance;
    InteractiveMessage message;
    @Before
    public void setUp(){
        instance = new OfflineInstance();
        InteractiveMessage.Builder builder = new InteractiveMessage.Builder(instance.channel1, instance.self);
        builder.addLines(Arrays.asList("a","b","c","d","e"), 2);
        message = builder.build();
        message.current_message = instance.guild_message;
    }
    @Test
    public void addRightSinglePageTest(){
        message.pages.retainAll(Arrays.asList(message.pages.get(0)));
        
        message.add(instance.self, 
                new MessageReaction(
                        instance.channel1,
                        new ReactionEmote(InteractiveMessage.ARROW_RIGHT,0L,instance.jda),
                        instance.guild_message.getIdLong(),
                        true,
                        1
                )
        );
        assertEquals(message.current_page,0);
    }
    @Test
    public void addRightTest(){
        message.add(instance.self, 
                new MessageReaction(
                        instance.channel1,
                        new ReactionEmote(InteractiveMessage.ARROW_RIGHT,0L,instance.jda),
                        instance.guild_message.getIdLong(),
                        true,
                        1
                )
        );
        assertEquals(message.current_page,1);
    }
    @Test
    public void addRightBacktrackTest(){
        message.current_page = message.pages.size()-1;
        message.add(instance.self, 
                new MessageReaction(
                        instance.channel1,
                        new ReactionEmote(InteractiveMessage.ARROW_RIGHT,0L,instance.jda),
                        instance.guild_message.getIdLong(),
                        true,
                        1
                )
        );
        assertEquals(message.current_page,0);
    }
    @Test
    public void addLeftSinglePageTest(){
        message.pages.retainAll(Arrays.asList(message.pages.get(0)));
        
        message.add(instance.self, 
                new MessageReaction(
                        instance.channel1,
                        new ReactionEmote(InteractiveMessage.ARROW_LEFT,0L,instance.jda),
                        instance.guild_message.getIdLong(),
                        true,
                        1
                )
        );
        assertEquals(message.current_page,0);
    }
    @Test
    public void addLeftTest(){
        message.add(instance.self, 
                new MessageReaction(
                        instance.channel1,
                        new ReactionEmote(InteractiveMessage.ARROW_LEFT,0L,instance.jda),
                        instance.guild_message.getIdLong(),
                        true,
                        1
                )
        );
        assertEquals(message.current_page,message.pages.size()-1);
    }
    @Test
    public void addLeftBacktrackTest(){
        message.add(instance.self, 
                new MessageReaction(
                        instance.channel1,
                        new ReactionEmote(InteractiveMessage.ARROW_LEFT,0L,instance.jda),
                        instance.guild_message.getIdLong(),
                        true,
                        1
                )
        );
        assertEquals(message.current_page,message.pages.size()-1);
    }
    @Test
    public void addJunkTest(){
        message.add(instance.self, 
                new MessageReaction(
                        instance.channel1,
                        new ReactionEmote(":D",0L,instance.jda),
                        instance.guild_message.getIdLong(),
                        true,
                        1
                )
        );
        assertEquals(message.current_page,0);
    }
    @Test
    public void addDifferentUserTest(){
        message.add(instance.user, 
                new MessageReaction(
                        instance.channel1,
                        new ReactionEmote(InteractiveMessage.ARROW_RIGHT,0L,instance.jda),
                        instance.guild_message.getIdLong(),
                        true,
                        1
                )
        );
        assertEquals(message.current_page,0);
    }
    @Test
    public void addPrivateChannel(){
        message.current_message = instance.private_message;
        
        message.add(instance.self, 
                new MessageReaction(
                        instance.channel1,
                        new ReactionEmote(InteractiveMessage.ARROW_RIGHT,0L,instance.jda),
                        instance.guild_message.getIdLong(),
                        true,
                        1
                )
        );
        assertEquals(message.current_page,1);
    }
    @Test
    public void addMissingPermissionChannel(){
        instance.guild.setOwner(instance.member);
        
        message.add(instance.self, 
                new MessageReaction(
                        instance.channel1,
                        new ReactionEmote(InteractiveMessage.ARROW_RIGHT,0L,instance.jda),
                        instance.guild_message.getIdLong(),
                        true,
                        1
                )
        );
        assertEquals(message.current_page,1);
    }
    @Test
    public void addUpdateChannelTest(){
        message.current_message = new OfflineMessage(instance.jda,0,null,null,null){
            @Override
            public MessageAction editMessage(MessageEmbed message){
                return new MessageAction(jda,null,channel){
                    @Override
                    public void queue(Consumer<? super Message> success, Consumer<? super Throwable> failure){
                        success.accept(instance.private_message);
                    }
                };
            }
        };
        
        assertNotEquals(message.current_message,instance.private_message);
        message.add(instance.self, 
                new MessageReaction(
                        instance.channel1,
                        new ReactionEmote(InteractiveMessage.ARROW_RIGHT,0L,instance.jda),
                        instance.guild_message.getIdLong(),
                        true,
                        1
                )
        );
        assertEquals(message.current_message,instance.private_message);
    }
    @Test
    public void getCurrentMessageTest(){
        assertEquals(message.current_message,message.getCurrentMessage());
    }
    @Test
    public void getLastReactionTest(){
        assertEquals(message.last_reaction,message.getLastReaction());
    }
    @Test
    public void sendTest(){
        assertTrue(instance.messages.isEmpty());
        message.send();
        assertEquals(instance.messages.size(),1);
    }
    @Test
    public void acceptTest(){
        assertNotEquals(message.current_message,instance.private_message);
        assertTrue(instance.actions.isEmpty());
        message.accept(instance.private_message);
        assertEquals(message.current_message,instance.private_message);
        assertEquals(instance.actions.size(),2);
    }
}