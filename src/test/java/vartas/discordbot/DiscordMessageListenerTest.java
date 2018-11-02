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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.MessageReaction;
import net.dv8tion.jda.core.entities.MessageReaction.ReactionEmote;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.message.MessageUpdateEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import vartas.OfflineInstance;
import vartas.TestCommand;
import vartas.discordbot.messages.InteractiveMessage;
import vartas.discordbot.threads.ActivityTracker;

/**
 *
 * @author u/Zavarov
 */
public class DiscordMessageListenerTest {
    DiscordBot bot;
    DiscordParser parser;
    DiscordMessageListener listener;
    OfflineInstance instance;
    InteractiveMessage message;
    
    @Before
    public void setUp(){
        instance = new OfflineInstance();
        instance.bot.getServer(instance.guild).setPrefix("prefix");
        instance.bot.getServer(instance.guild).addFilter("word");
        instance.guild_message.setContent("prefixab");
        listener = new DiscordMessageListener(instance.bot, instance.config);
        message = new InteractiveMessage.Builder(instance.channel1, instance.self)
                .addLines(Arrays.asList("aaaaa","bbbbb"), 1)
                .build();
        message.accept(instance.guild_message);
        listener.messages.put(message.getCurrentMessage().getIdLong(),message);
    }
    @Test
    public void shutdownTest(){
        assertFalse(listener.parser_executor.isShutdown());
        assertFalse(listener.command_executor.isShutdown());
        listener.shutdown();
        assertTrue(listener.parser_executor.isShutdown());
        assertTrue(listener.command_executor.isShutdown());
    }
    @Test
    public void onMessageReactionAddTest(){
        assertTrue(instance.messages.isEmpty());
        listener.onMessageReactionAdd(
                new MessageReactionAddEvent(
                    instance.jda,
                    0,
                    instance.self,
                    new MessageReaction(instance.channel1,new ReactionEmote(InteractiveMessage.ARROW_LEFT,0L,instance.jda),instance.guild_message.getIdLong(),true,1)
        ));
        assertTrue(instance.messages.get(0).getEmbeds().get(0).getFields().get(0).getValue().contains("bbbbb"));
    }
    @Test
    public void onMessageReactionAddNoInteractiveMessageTest(){
        listener.messages.clear();
        assertTrue(instance.messages.isEmpty());
        listener.onMessageReactionAdd(
                new MessageReactionAddEvent(
                    instance.jda,
                    0,
                    instance.self,
                    new MessageReaction(instance.channel1,new ReactionEmote(InteractiveMessage.ARROW_LEFT,0L,instance.jda),instance.guild_message.getIdLong(),true,1)
                ));
        assertTrue(instance.messages.isEmpty());
    }
    @Test
    public void onMessageReactionAddBotTest(){
        instance.self.setBot(true);
        assertTrue(instance.messages.isEmpty());
        listener.onMessageReactionAdd(
                new MessageReactionAddEvent(
                    instance.jda,
                    0,
                    instance.self,
                    new MessageReaction(instance.channel1,new ReactionEmote(InteractiveMessage.ARROW_LEFT,0L,instance.jda),instance.guild_message.getIdLong(),true,1)
        ));
        assertTrue(instance.messages.isEmpty());
    }
    @Test
    public void onGuildLeaveTest(){
        List<String> temp = new ArrayList<>();
        assertTrue(temp.isEmpty());
        instance.bot = new DiscordBot(null,instance.jda,instance.config){
            @Override
            public void deleteServer(Guild guild){
                temp.add("deleted");
            }
        };
        listener = new DiscordMessageListener(instance.bot, instance.config);
        listener.onGuildLeave(new GuildLeaveEvent(instance.jda,0, instance.guild));
        assertEquals(temp, Arrays.asList("deleted"));
    }
    @Test
    public void onMessageUpdateTest() throws InterruptedException{
        TestCommand.LOG.clear();
        
        assertTrue(TestCommand.LOG.isEmpty());
        
        instance.guild_message.setEditedTime(OffsetDateTime.now());
        listener.onMessageUpdate(new MessageUpdateEvent(instance.jda,0,instance.guild_message));
        
        listener.parser_executor.shutdown();
        while(!listener.parser_executor.isTerminated()){}
        listener.command_executor.shutdown();
        while(!listener.command_executor.isTerminated()){}

        assertFalse(TestCommand.LOG.isEmpty());
    }
    @Test
    public void onMessageUpdateTooOldTest(){
        TestCommand.LOG.clear();
        
        assertTrue(TestCommand.LOG.isEmpty());
        
        instance.guild_message.setEditedTime(OffsetDateTime.now().minusDays(1));
        listener.onMessageUpdate(new MessageUpdateEvent(instance.jda,0,instance.guild_message));
        
        listener.parser_executor.shutdown();
        while(!listener.parser_executor.isTerminated()){}
        listener.command_executor.shutdown();
        while(!listener.command_executor.isTerminated()){}
        
        assertTrue(TestCommand.LOG.isEmpty());
    }
    @Test
    public void onGuildMessageReceivedRemoveSelfMessageTest(){
        instance.actions.clear();
        assertTrue(instance.actions.isEmpty());
        instance.guild_message.setContent("word");
        listener.onGuildMessageReceived(new GuildMessageReceivedEvent(instance.jda,0,instance.guild_message));
        assertTrue(instance.actions.isEmpty());
    }
    @Test
    public void onGuildMessageReceivedRemoveMessageTest(){
        instance.actions.clear();
        assertTrue(instance.actions.isEmpty());
        instance.guild_message.setAuthor(instance.user);
        instance.guild_message.setContent("word");
        listener.onGuildMessageReceived(new GuildMessageReceivedEvent(instance.jda,0,instance.guild_message));
        assertTrue(instance.actions.get(0).contains("deleted"));
    }
    @Test
    public void onGuildMessageReceivedDontRemoveMessageTest(){
        instance.actions.clear();
        assertTrue(instance.actions.isEmpty());
        instance.guild_message.setAuthor(instance.user);
        listener.onGuildMessageReceived(new GuildMessageReceivedEvent(instance.jda,0,instance.guild_message));
        assertTrue(instance.actions.isEmpty());
    }
    @Test
    public void onGuildMessageReceivedTest(){
        List<String> updates = new ArrayList<>();
        listener.activity = new ActivityTracker(instance.jda,10){
            @Override
            public void increase(Guild guild, TextChannel channel){
                updates.add("updated");
            }
        };
        listener.activity.run();
        assertTrue(updates.isEmpty());
        listener.onGuildMessageReceived(new GuildMessageReceivedEvent(instance.jda,0,instance.guild_message));
        assertFalse(updates.isEmpty());
    }
    @Test
    public void onGuildMessageReceivedIsBotTest(){
        List<String> updates = new ArrayList<>();
        listener.activity = new ActivityTracker(instance.jda,10){
            @Override
            public void increase(Guild guild, TextChannel channel){
                updates.add("updated");
            }
        };
        
        instance.self.setBot(true);
        assertTrue(updates.isEmpty());
        listener.onGuildMessageReceived(new GuildMessageReceivedEvent(instance.jda,0,instance.guild_message));
        assertTrue(updates.isEmpty());
    }
    @Test
    public void onMessageReceivedTest() throws InterruptedException{
        TestCommand.LOG.clear();
        assertTrue(TestCommand.LOG.isEmpty());
        listener.onMessageReceived(new MessageReceivedEvent(instance.jda,0,instance.guild_message));
        
        listener.parser_executor.shutdown();
        while(!listener.parser_executor.isTerminated()){}
        listener.command_executor.shutdown();
        while(!listener.command_executor.isTerminated()){}
        
        assertFalse(TestCommand.LOG.isEmpty());
    }
    @Test
    public void onMessageReceivedBotTest(){
        TestCommand.LOG.clear();
        assertTrue(TestCommand.LOG.isEmpty());
        instance.self.setBot(true);
        listener.onMessageReceived(new MessageReceivedEvent(instance.jda,0,instance.guild_message));
        
        listener.parser_executor.shutdown();
        while(!listener.parser_executor.isTerminated()){}
        listener.command_executor.shutdown();
        while(!listener.command_executor.isTerminated()){}
        
        assertTrue(TestCommand.LOG.isEmpty());
    }
    @Test
    public void onMessageReceivedNoPrefixTest(){
        instance.guild_message.setContent(instance.config.getPrefix()+"ab");
        instance.bot.getServer(instance.guild).setPrefix(null);
        TestCommand.LOG.clear();
        assertTrue(TestCommand.LOG.isEmpty());
        listener.onMessageReceived(new MessageReceivedEvent(instance.jda,0,instance.guild_message));
        
        listener.parser_executor.shutdown();
        while(!listener.parser_executor.isTerminated()){}
        listener.command_executor.shutdown();
        while(!listener.command_executor.isTerminated()){}
        
        assertFalse(TestCommand.LOG.isEmpty());
    }
    @Test
    public void onMessageReceivedNoGuild(){
        instance.private_message.setContent(instance.config.getPrefix()+"ab");
        TestCommand.LOG.clear();
        assertTrue(TestCommand.LOG.isEmpty());
        listener.onMessageReceived(new MessageReceivedEvent(instance.jda,0,instance.private_message));
        
        listener.parser_executor.shutdown();
        while(!listener.parser_executor.isTerminated()){}
        listener.command_executor.shutdown();
        while(!listener.command_executor.isTerminated()){}
        
        assertFalse(TestCommand.LOG.isEmpty());
    }
    @Test
    public void onMessageReceivedNoCommand(){
        instance.guild_message.setContent("junk");
        TestCommand.LOG.clear();
        assertTrue(TestCommand.LOG.isEmpty());
        listener.onMessageReceived(new MessageReceivedEvent(instance.jda,0,instance.guild_message));
        
        listener.parser_executor.shutdown();
        while(!listener.parser_executor.isTerminated()){}
        listener.command_executor.shutdown();
        while(!listener.command_executor.isTerminated()){}
        
        assertTrue(TestCommand.LOG.isEmpty());
    }
}