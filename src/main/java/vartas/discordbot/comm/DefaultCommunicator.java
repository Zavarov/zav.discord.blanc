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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.function.Consumer;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.requests.RestAction;
import vartas.xml.XMLPermission;

/**
 * This class is the default communicator that handels Discord requests for
 * each respective shard.
 * @author u/Zavarov
 */
public class DefaultCommunicator extends AbstractCommunicator{
    /**
     * @param environment the environment this communicator is in.
     * @param jda the JDA instance for this shard.
     */
    public DefaultCommunicator(Environment environment, JDA jda) {
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
        submit(() -> action.queue(success, failure));
    }
    /**
     * Sends a message in the specified channel and calls the consumer upon success or failure.
     * @param channel the channel the message is sent to.
     * @param message the message.
     * @param success the consumer that is called when the message was sent successfully.
     * @param failure  the consumer that is called when the message couldn't be sent.
     */
    @Override
    public void send(MessageChannel channel, MessageBuilder message, Consumer<Message> success, Consumer<Throwable> failure) {
        Message m = message.stripMentions(jda()).build();
        send(channel.sendMessage(m),success,failure);
    }
    /**
     * Deletes the message with the specified id.
     * @param channel the channel the message is in.
     * @param id the message id.
     */
    @Override
    public void delete(MessageChannel channel, long id) {
        send(channel.deleteMessageById(id));
    }
    /**
     * Updates the XML file of the permission map.
     * @param permission the permission map
     * @throws IOException if the data couldn't be written into a file.
     * @throws InterruptedException if the program was interrupted before the writing process was finished.
     */
    @Override
    public void update(XMLPermission permission) throws IOException, InterruptedException{
        permission.update().write(
                new FileOutputStream(
                        new File(
                                String.format(
                                        "%s/permission.xml",
                                        environment().config().getDataFolder()))
                ), null);
        log.info("Updated the permission file");
    }
    /**
     * Updates the XML file associated with the guild.
     * @param guild the guild whose XML file is updated.
     * @throws IOException if the data couldn't be written into a file.
     * @throws InterruptedException if the program was interrupted before the writing process was finished.
     */
    @Override
    public void update(Guild guild) throws IOException, InterruptedException{
        File file = new File(
                String.format("%s/guilds/%s.server",
                        environment().config().getDataFolder(),
                        guild.getId()));
        server(guild).update().write(new FileOutputStream(file), null);
        log.info(String.format("Updated the file for the server %s", guild.getId()));
    }
    /**
     * Deletes the XML file associated with the guild.
     * @param guild the guild whose XML file is deleted.
     */
    @Override
    public void delete(Guild guild){
        servers.remove(guild);
        File file = new File(String.format("%s/guilds/%s.server",environment().config().getDataFolder(),guild.getId()));
        if(file.exists()){
            file.delete();
            log.info(String.format("Deleted server %s",guild));
        }
    }
}
