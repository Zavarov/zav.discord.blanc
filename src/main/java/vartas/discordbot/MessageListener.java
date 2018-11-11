/*
 * Copyright (C) 2017 u/Zavarov
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

import com.google.common.collect.Lists;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.message.MessageUpdateEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.dv8tion.jda.core.utils.JDALogger;
import org.slf4j.Logger;
import vartas.discordbot.comm.Communicator;
import vartas.discordbot.command.Command;
import vartas.discordbot.threads.MessageTracker;
import vartas.xml.XMLServer;

/**
 * This class acts as an interface between the the messages received from Discord and this program.
 * @author u/Zavarov
 */
public class MessageListener extends ListenerAdapter{
    /**
     * The tracker for all interactive messages.
     */
    protected MessageTracker messages;
    /**
     * An executor for the parser.
     */
    protected ExecutorService parser_executor;
    /**
     * The communicator of the program.
     */
    protected Communicator comm;
    /**
     * The parser that processes all commands.
     */
    protected CommandParser parser;
    /**
     * The log for this nested class.
     */
    protected Logger log = JDALogger.getLog(this.getClass().getSimpleName());
    /**
     * @param comm the communicator of the program.
     */
    public MessageListener(Communicator comm){
        this.comm = comm;
        this.parser = new CommandParser.Builder(comm).build();
        this.messages = new MessageTracker(comm);
        this.parser_executor = Executors.newSingleThreadExecutor();
    }
    /**
     * An reaction was added to a message.
     * @param event the corresponding event.
     */
    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent event){
        if(!event.getUser().isBot())
            messages.update(event.getMessageIdLong(), event.getUser(), event.getReaction());
    }
    /**
     * A message was received inside a guild.
     * Parsing the actuall message will be done by the onMessageReceived
     * function, so this one just executes the server-specific actions.
     * @param event the corresponding event.
     */
    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event){
        //This bot is an exception to the rule.
        if(!comm.self().equals(event.getMessage().getAuthor()) && 
                comm.server(event.getGuild()).getFilter().stream().anyMatch(event.getMessage().getContentRaw()::contains)){
            comm.delete(event.getChannel(), event.getMessageIdLong());
            log.info(String.format("Deleted message %s",event.getMessage().getId()));
            return;
        }
        //Ignore bots
        if(!event.getAuthor().isBot()){
            comm.activity(event.getChannel());
        }
    }

    /**
     * A message was edited.
     * @param event the corresponding event.
     */
    @Override
    public void onMessageUpdate(MessageUpdateEvent event){
        //If the message isn't older than a minute
        if( ((System.currentTimeMillis()/1000) - event.getMessage().getEditedTime().toEpochSecond()) <= 60){
            messageReceived(event.getMessage());
        }
    }
    /**
     * A message was received on either a private channel or a guild channel.
     * @param event the corresponding event.
     */
    @Override
    public void onMessageReceived(MessageReceivedEvent event){
        messageReceived(event.getMessage());
    }
    /**
     * This bot left a guild.
     * @param event the corresponding event.
     */
    @Override
    public void onGuildLeave(GuildLeaveEvent event){
        comm.delete(event.getGuild());
    }
    /**
     * @param message the input message
     * @return all valid prefixes for the message.
     */
    private List<String> getPrefixes(Message message){
        List<String> prefixes = Lists.newArrayList(comm.environment().config().getPrefix());
        if(message.getGuild() != null){
            XMLServer server = comm.server(message.getGuild());
            if(server.hasPrefix())
                prefixes.add(server.getPrefix());
        }
        return prefixes;
    }
    /**
     * @param message the input message.
     * @return the raw content of the message without a prefix. 
     */
    private String getContent(Message message){
        String prefix = getPrefixes(message).stream()
                .filter(message.getContentRaw()::startsWith)
                .findFirst()
                .get();
        return message.getContentRaw().substring(prefix.length());
    }
    /**
     * @param message the input message.
     * @return true if the message starts with a valid prefix
     */
    private boolean hasPrefix(Message message){
        return getPrefixes(message).stream().anyMatch(message.getContentRaw()::startsWith);
    }
    /**
     * A helper class that submits the message to the parser if the author isn't a bot.
     * @param message the message.
     */
    private void messageReceived(Message message){
        if(!message.getAuthor().isBot() && hasPrefix(message)){
            parser_executor.submit(() -> {
                Command command = parser.parseCommand(message, getContent(message));
                command.setCommunicator(comm);
                comm.submit(command);
            });
        }
    }
    /**
     * Terminates the executor for the parser.
     */
    public void shutdown(){
        messages.shutdown();
        parser_executor.shutdownNow();
        log.info("MessageListener terminated.");
    }
}