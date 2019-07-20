/*
 * Copyright (c) 2019 Zavarov
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

package vartas.discord.bot.api.listener;

import com.google.common.collect.Lists;
import de.monticore.symboltable.GlobalScope;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.message.MessageUpdateEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.dv8tion.jda.core.utils.JDALogger;
import org.slf4j.Logger;
import vartas.discord.bot.api.communicator.CommunicatorInterface;
import vartas.discord.bot.api.threads.MessageTracker;
import vartas.discord.bot.command.AbstractCommand;
import vartas.discord.bot.command.call.CallHelper;
import vartas.discord.bot.command.call._ast.ASTCallArtifact;
import vartas.discord.bot.exec.AbstractCommandBuilder;
import vartas.discord.bot.io.guild.GuildConfiguration;

import java.util.List;

/**
 * This class acts as an interface between the the message received from Discord and this program.
 */
public class MessageListener extends ListenerAdapter {
    /**
     * The underlying scope that contains
     */
    protected GlobalScope commands;
    /**
     * The builder for generating the commands from the calls.
     */
    protected AbstractCommandBuilder builder;
    /**
     * The tracker for all interactive message.
     */
    protected MessageTracker messages;
    /**
     * The communicator of the program.
     */
    protected CommunicatorInterface communicator;
    /**
     * The log for this nested class.
     */
    protected Logger log = JDALogger.getLog(this.getClass().getSimpleName());
    /**
     * @param communicator the communicator of the program.
     * @param messages a tracker for all interactive message
     * @param commands the scope for all valid commands
     * @param builder the builder for generating the commands from the calls
     */
    public MessageListener(CommunicatorInterface communicator, MessageTracker messages, GlobalScope commands, AbstractCommandBuilder builder){
        this.commands = commands;
        this.builder = builder;
        this.communicator = communicator;
        this.messages = messages;

        builder.setCommunicator(this.communicator);
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
        boolean isSelfUser = communicator.isSelfUser(event.getAuthor());
        boolean isInvalid = communicator.config(event.getGuild()).anyMatch(event.getMessage().getContentRaw());

        //This bot is an exception to the rule.
        if(!isSelfUser && isInvalid){
            communicator.send(event.getMessage().delete());
            log.info(String.format("Deleted message %s",event.getMessage().getId()));
            return;
        }
        //Ignore bots
        if(!event.getAuthor().isBot()){
            communicator.activity(event.getChannel());
        }
    }

    /**
     * A message was edited.
     * @param event the corresponding event.
     */
    @Override
    public void onMessageUpdate(MessageUpdateEvent event){
        boolean isSelfUser = communicator.isSelfUser(event.getAuthor());
        boolean isInvalid = communicator.config(event.getGuild()).anyMatch(event.getMessage().getContentRaw());

        //This bot is an exception to the rule.
        if(!isSelfUser && isInvalid){
            communicator.send(event.getMessage().delete());
            log.info(String.format("Deleted message %s",event.getMessage().getId()));
            return;
        }

        long age = (System.currentTimeMillis()/1000) - event.getMessage().getEditedTime().toEpochSecond();
        //If the message isn't older than a minute
        if( age <= 60 && !event.getAuthor().isBot() && event.getTextChannel() != null){
            communicator.activity(event.getTextChannel());
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
        communicator.delete(event.getGuild());
    }
    /**
     * @param message the input message
     * @return all valid prefixes for the message.
     */
    private List<String> getPrefixes(Message message){
        List<String> prefixes = Lists.newArrayList(communicator.environment().config().getGlobalPrefix());
        if(message.getGuild() != null){
            GuildConfiguration server = communicator.config(message.getGuild());
            server.getPrefix().ifPresent(prefixes::add);
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
            try {
                ASTCallArtifact source = CallHelper.parse(commands, getContent(message));
                AbstractCommand command = builder.setContext(message).setSource(source).build();
                communicator.execute(command);
                log.info("Executed "+command.getClass().getSimpleName());
            }catch(RuntimeException e){
                e.printStackTrace();
                communicator.send(message.getChannel(), e.getClass().getSimpleName() + ": " + e.getMessage());
            }
        }
    }
}