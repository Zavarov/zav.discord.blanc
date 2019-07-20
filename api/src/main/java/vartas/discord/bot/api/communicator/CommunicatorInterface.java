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

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.requests.RestAction;
import net.dv8tion.jda.core.utils.JDALogger;
import org.slf4j.Logger;
import vartas.discord.bot.api.environment.EnvironmentInterface;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

/**
 * This interface is intended to hide the communication with the underlying
 * APIs and other libraries from the respective commands.
 * Instead of directly, they have to be accessed via this
 */
public interface CommunicatorInterface extends SendInterface, ActivityInterface, ConfigInterface{
    /**
     * The logger for the communicator.
     */
    Logger log = JDALogger.getLog(CommunicatorInterface.class.getSimpleName());
    /**
     * The executor that deals with all asynchronous processes.
     */
    ExecutorService executor = Executors.newCachedThreadPool();
    /**
     * Schedules a runnable to be executed and some unspecific point in time.
     * @param runnable the runnable that is going to be executed.
     */
    default void execute(Runnable runnable){
        executor.execute(runnable);
    }
    /**
     * Returns the environment that connects the communicator of this shard with the communicators of all the other
     * shards.
     * @return the environment for all communicators.
     */
    EnvironmentInterface environment();
    /**
     * @return the jda in the current shard. 
     */
    JDA jda();

    /**
     * @param user the user the self user instance is compared with.
     * @return true if the user is equivalent to the self user instance of this shard.
     */
    default boolean isSelfUser(User user){
        return jda().getSelfUser().equals(user);
    }

    /**
     * Completes the specified action and calls the consumer upon success or failure.
     * @param <T> the return value of the action.
     * @param action the action that is executed.
     * @param success the consumer that is called when the action was executed successfully.
     * @param failure  the consumer that is called when the action couldn't be executed.
     */
    @Override
    default <T> void send(RestAction<T> action, Consumer<T> success, Consumer<Throwable> failure){
        execute(() -> action.queue(success, failure));
    }
    /**
     * Sends a message in the specified channel and calls the consumer upon success or failure.
     * @param channel the channel the message is sent to.
     * @param message the message.
     * @param success the consumer that is called when the message was sent successfully.
     * @param failure  the consumer that is called when the message couldn't be sent.
     */
    @Override
    default void send(MessageChannel channel, MessageBuilder message, Consumer<Message> success, Consumer<Throwable> failure) {
        Message m = message.stripMentions(jda()).build();
        send(channel.sendMessage(m),success,failure);
    }

    /**
     * Attempts to shutdown the current shard.
     * @return the task that will await the shutdown of this shard.
     */
    Runnable shutdown();
}
