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

import com.google.common.collect.LinkedListMultimap;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.requests.RestAction;
import vartas.xml.XMLPermission;

/**
 * This class is intended to serve as a debugging tool to simulate the API
 * calls.
 * @author u/Zavarov
 */
public class OfflineCommunicator extends AbstractCommunicator{
    /**
     * A list to store generic status messages.
     */
    public final List<String> actions = new ArrayList<>();
    /**
     * A history of all messages that have been sent.
     */
    public final LinkedListMultimap<MessageChannel, Message> discord = LinkedListMultimap.create();
    /**
     * Initializes all necessary tasks for the communicator in this shard.
     * @param environment the environment of the program
     * @param jda the JDA that this communicator uses.
     */
    public OfflineCommunicator(Environment environment, JDA jda) {
        super(environment, jda);
    }
    /**
     * Completes the specified action and calls the consumer upon success or failure.
     * @param <T> the return value of the action.
     * @param action the action that is executed.
     * @param success the consumer that is called when the action was executed successfully.
     * @param failure  the consumer that is called when the action couldn't be executed.
     */
    @Override
    public <T> void send(RestAction<T> action, Consumer<T> success, Consumer<Throwable> failure){
        actions.add("action queued");
    }
    /**
     * Puts the message into the database and calls the success consumer..
     * @param channel the channel the message is sent to.
     * @param message the message.
     * @param success the consumer that is called when the message was sent successfully.
     * @param failure  the consumer that is called when the message couldn't be sent.
     */
    @Override
    public void send(MessageChannel channel, MessageBuilder message, Consumer<Message> success, Consumer<Throwable> failure) {
        Message m = message.build();
        discord.put(channel, m);
        if(success != null)
            success.accept(m);
    }
    /**
     * Deletes the message with the specified id.
     * @param channel the channel the message is in.
     * @param id the message id.
     */
    @Override
    public void delete(MessageChannel channel, long id) {
        discord.get(channel).removeIf(e -> e.getIdLong() == id);
    }
    /**
     * Pushes a notification to the actions list that this guild got updated.
     * @param guild the guild whose XML file is updated.
     */
    @Override
    public void update(Guild guild) {
        actions.add(String.format("%s updated",guild.getName()));
    }
    /**
     * Deletes the XML file associated with the guild.
     * @param guild the guild whose XML file is deleted.
     */
    @Override
    public void delete(Guild guild){
        actions.add(String.format("%s deleted",guild.getName()));
    }
    /**
     * Updates the XML file of the permission map.
     * @param permission the permission map
     */
    @Override
    public void update(XMLPermission permission){
        actions.add("Permission file updated");
    }
}
