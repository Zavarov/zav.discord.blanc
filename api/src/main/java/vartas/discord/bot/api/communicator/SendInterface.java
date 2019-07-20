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

package vartas.discord.bot.api.communicator;

import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.requests.RestAction;
import vartas.discord.bot.api.message.InteractiveMessage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.function.Consumer;

public interface SendInterface {
    /**
     * Sends a message in the specified channel and calls the consumer upon success or failure.
     * @param channel the channel the message is sent to.
     * @param message the message.
     * @param success the consumer that is called when the message was sent successfully.
     * @param failure  the consumer that is called when the message couldn't be sent.
     */
    void send(MessageChannel channel, MessageBuilder message, Consumer<Message> success, Consumer<Throwable> failure);
    /**
     * Calls the send function with null as the consumer that is call upon a failure.
     * @param channel the channel the message is sent to.
     * @param message the message.
     * @param success the consumer that is called when the message was sent successfully.
     */
    default void send(MessageChannel channel, MessageBuilder message, Consumer<Message> success){
        send(channel, message, success, null);
    }
    /**
     * Calls the send function with null as the consumer for both success and failure.
     * @param channel the channel the message is sent to.
     * @param message the message.
     */
    default void send(MessageChannel channel, MessageBuilder message){
        send(channel, message, null);
    }
    /**
     * Wraps the message around a MessageBuilder instance and sends that.
     * @param channel the channel the message is sent to.
     * @param message the raw content of the message.
     */
    default void send(MessageChannel channel, String message){
        MessageBuilder builder = new MessageBuilder();
        builder.setContent(message);
        send(channel, builder);
    }
    /**
     * Wraps the embed around a MessageBuilder instance and sends that.
     * @param channel the channel the message is sent to.
     * @param embed the embed of the message.
     */
    default void send(MessageChannel channel, MessageEmbed embed){
        MessageBuilder builder = new MessageBuilder();
        builder.setEmbed(embed);
        send(channel, builder);
    }
    /**
     * Sends an image in the specified channel.
     * @param channel the channel the message is sent to.
     * @param image the image that is sent.
     * @throws IllegalArgumentException in case the image couldn't be sent.
     */
    default void send(MessageChannel channel, BufferedImage image) throws IllegalArgumentException{
        try{
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            ImageIO.write(image, "png", output);

            ByteArrayInputStream input = new ByteArrayInputStream(output.toByteArray());
            send(channel.sendFile(input, "image.png"));
            //Fuck ImageIO and just catch anything (And use "Throwable" to allow testing)
        }catch(Throwable e){
            throw new IllegalArgumentException(e);
        }
    }
    /**
     * Sends a file in the specified channel.
     * @param channel the channel the message is sent to.
     * @param file the file that is sent.
     */
    default void send(MessageChannel channel, File file){
        send(channel.sendFile(file));
    }
    /**
     * Sends a the interactive message in the specified channel.
     * @param message the interactive message that is sent.
     */
    void send(InteractiveMessage message);
    /**
     * Completes the specified action and calls the consumer upon success or failure.
     * @param <T> the return value of the action.
     * @param action the action that is executed.
     * @param success the consumer that is called when the action was executed successfully.
     * @param failure  the consumer that is called when the action couldn't be executed.
     */
    <T> void send(RestAction<T> action, Consumer<T> success, Consumer<Throwable> failure);
    /**
     * Calls the send function with null as the consumer that is call upon a failure.
     * @param <T> the return value of the action.
     * @param action the action that is executed.
     * @param success the consumer that is called when the action was executed successfully.
     */
    default <T> void send(RestAction<T> action, Consumer<T> success){
        send(action, success, null);
    }
    /**
     * Calls the send function with null as the consumer for both success and failure.
     * @param <T> the return value of the action.
     * @param action the action that is executed.
     */
    default <T> void send(RestAction<T> action){
        send(action, null);
    }
}
