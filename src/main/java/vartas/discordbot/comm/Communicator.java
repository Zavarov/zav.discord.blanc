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

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Set;
import java.util.function.Consumer;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.SelfUser;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.managers.Presence;
import net.dv8tion.jda.core.requests.RestAction;
import org.jfree.chart.JFreeChart;
import vartas.discordbot.messages.InteractiveMessage;
import vartas.discordbot.threads.Killable;
import vartas.parser.cfg.ContextFreeGrammar.Token;
import vartas.xml.XMLPermission;
import vartas.xml.XMLServer;

/**
 * This interface is intended to hide the communication with the underlying
 * APIs and other libraries from the respective commands.<br>
 * Instead of directly, they have to be accessed via this 
 * @author u/Zavarov
 */
public interface Communicator extends Killable{
    /**
     * Sends a message in the specified channel and calls the consumer upon success or failure.
     * @param channel the channel the message is sent to.
     * @param message the message.
     * @param success the consumer that is called when the message was sent successfully.
     * @param failure  the consumer that is called when the message couldn't be sent.
     */
    public abstract void send(MessageChannel channel, MessageBuilder message, Consumer<Message> success, Consumer<Throwable> failure);
    /**
     * Calls the send function with null as the consumer that is call upon a failure.
     * @param channel the channel the message is sent to.
     * @param message the message.
     * @param success the consumer that is called when the message was sent successfully.
     */
    public default void send(MessageChannel channel, MessageBuilder message, Consumer<Message> success){
        send(channel, message, success, null);
    }
    /**
     * Calls the send function with null as the consumer for both success and failure.
     * @param channel the channel the message is sent to.
     * @param message the message.
     */
    public default void send(MessageChannel channel, MessageBuilder message){
        send(channel, message, null);
    }
    /**
     * Wraps the message around a MessageBuilder instance and sends that.
     * @param channel the channel the message is sent to.
     * @param message the raw content of the message.
     */
    public default void send(MessageChannel channel, String message){
        MessageBuilder builder = new MessageBuilder();
        builder.setContent(message);
        send(channel, builder);
    }
    /**
     * Wraps the embed around a MessageBuilder instance and sends that.
     * @param channel the channel the message is sent to.
     * @param embed the embed of the message.
     */
    public default void send(MessageChannel channel, MessageEmbed embed){
        MessageBuilder builder = new MessageBuilder();
        builder.setEmbed(embed);
        send(channel, builder);
    }
    /**
     * Sends an image in the specified channel.
     * @param channel the channel the message is sent to.
     * @param image the image that is sent.
     */
    public default void send(MessageChannel channel, BufferedImage image){
        byte[] data = ((DataBufferByte)image.getRaster().getDataBuffer()).getData();
        send(channel.sendFile(new ByteArrayInputStream(data), "image.png"));
    }
    /**
     * Sends a file in the specified channel.
     * @param channel the channel the message is sent to.
     * @param file the file that is sent.
     */
    public default void send(MessageChannel channel, File file){
        send(channel.sendFile(file));
    }
    /**
     * Sends a the interactive message in the specified channel.
     * @param message the interactive message that is sent.
     */
    public abstract void send(InteractiveMessage message);
    /**
     * Completes the specified action and calls the consumer upon success or failure.
     * @param <T> the return value of the action.
     * @param action the action that is executed.
     * @param success the consumer that is called when the action was executed successfully.
     * @param failure  the consumer that is called when the action couldn't be executed.
     */
    public abstract <T> void send(RestAction<T> action, Consumer<T> success, Consumer<Throwable> failure);
    /**
     * Calls the send function with null as the consumer that is call upon a failure.
     * @param <T> the return value of the action.
     * @param action the action that is executed.
     * @param success the consumer that is called when the action was executed successfully.
     */
    public default <T> void send(RestAction<T> action, Consumer<T> success){
        send(action, success, null);
    }
    /**
     * Calls the send function with null as the consumer for both success and failure.
     * @param <T> the return value of the action.
     * @param action the action that is executed.
     */
    public default <T> void send(RestAction<T> action){
        send(action, null);
    }
    /**
     * Deletes the message with the specified id.
     * @param channel the channel the message is in.
     * @param id the message id.
     */
    public abstract void delete(MessageChannel channel, long id);
    /**
     * @param objects a set of tokens that identify text channels.
     * @param message the message that acts as a reference point.
     * @return the textchannel specified by the data or the current channel.
     */
    public abstract Set<TextChannel> defaultTextChannel(Iterable<Token> objects, Message message);
    /**
     * @param objects a set of tokens that identify text channels.
     * @param message the message that acts as a reference point.
     * @return the textchannel specified by the data.
     */
    public abstract Set<TextChannel> textChannel(Iterable<Token> objects, Message message);
    /**
     * @param objects a set of tokens that identify guilds.
     * @param message the message that acts as a reference point.
     * @return the guilds specified by the data or the current guild.
     */
    public abstract Set<Guild> defaultGuild(Iterable<Token> objects, Message message);
    /**
     * @param objects a set of tokens that identify guilds.
     * @param message the message that acts as a reference point.
     * @return the guilds specified by the data.
     */
    public abstract Set<Guild> guild(Iterable<Token> objects, Message message);
    /**
     * @param objects a set of tokens that identify members.
     * @param message the message that acts as a reference point.
     * @return the members specified by the data or the current member.
     */
    public abstract Set<Member> defaultMember(Iterable<Token> objects, Message message);
    /**
     * @param objects a set of tokens that identify members.
     * @param message the message that acts as a reference point.
     * @return the members specified by the data.
     */
    public abstract Set<Member> member(Iterable<Token> objects, Message message);
    /**
     * @param objects a set of tokens that identify users.
     * @param message the message that acts as a reference point.
     * @return the users specified by the data or the author of the message.
     */
    public abstract Set<User> defaultUser(Iterable<Token> objects, Message message);
    /**
     * @param objects a set of tokens that identify users.
     * @param message the message that acts as a reference point.
     * @return the users specified by the data.
     */
    public abstract Set<User> user(Iterable<Token> objects, Message message);
    /**
     * @param objects a set of tokens that identify roles.
     * @param message the message that acts as a reference point.
     * @return the roles specified by the data.
     */
    public abstract Set<Role> role(Iterable<Token> objects, Message message);
    /**
     * Updates the activity tracker by a new message in the given channel.
     * @param channel the channel in which activity was observed.
     */
    public abstract void activity(TextChannel channel);
    /**
     * @param guild the guild the chart is plotted over.
     * @param channels the channels that are also plotted.
     * @return a chart over the activity in the guild and also the selected channels.
     */
    public abstract JFreeChart activity(Guild guild, Collection<TextChannel> channels);
    /**
     * @param guild the guild we want the server file from.
     * @return the server file that is connected to the guild. 
     */
    public abstract XMLServer server(Guild guild);
    /**
     * A wrapper that requests the server of the guild the channel is in.
     * @param channel the text channel of a guild.
     * @return the server file that is connected to the guild of the text channel. 
     */
    public default XMLServer server(TextChannel channel){
        return server(channel.getGuild());
    }
    /**
     * A wrapper that requests the server of the guild the role is in.
     * @param role the role of a guild.
     * @return the server file that is connected to the guild of the text channel. 
     */
    public default XMLServer server(Role role){
        return server(role.getGuild());
    }
    /**
     * @return all guilds that in the current shard. 
     */
    public abstract Collection<Guild> guild();
    /**
     * Updates the XML file associated with the guild.
     * @param guild the guild whose XML file is updated.
     * @throws IOException if the data couldn't be written into a file.
     * @throws InterruptedException if the program was interrupted before the writing process was finished.
     */
    public abstract void update(Guild guild) throws IOException, InterruptedException;
    /**
     * Deletes the XML file associated with the guild.
     * @param guild the guild whose XML file is deleted.
     */
    public abstract void delete(Guild guild);
    /**
     * Submits a runnable to be executed and some unspecific point in time.
     * @param runnable the runnable that is going to be executed.
     */
    public abstract void submit(Runnable runnable);
    /**
     * @return the instance of the bot itself in the current guild.
     */
    public abstract SelfUser self();
    /**
     * @return the underlying environment of the program. 
     */
    public abstract Environment environment();
    /**
     * @return the presence of this program in the current shard. 
     */
    public abstract Presence presence();
    /**
     * @return the jda in the current shard. 
     */
    public abstract JDA jda();
    /**
     * Updates the XML file of the permission map.
     * @param permission the permission map
     * @throws IOException if the data couldn't be written into a file.
     * @throws InterruptedException if the program was interrupted before the writing process was finished.
     */
    public abstract void update(XMLPermission permission) throws IOException, InterruptedException;
}
