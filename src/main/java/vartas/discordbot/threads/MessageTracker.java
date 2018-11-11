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

package vartas.discordbot.threads;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import net.dv8tion.jda.core.entities.MessageReaction;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.utils.JDALogger;
import org.atteo.evo.inflector.English;
import org.slf4j.Logger;
import vartas.discordbot.comm.Communicator;
import vartas.discordbot.messages.InteractiveMessage;

/**
 * A runnable that removes interactive messages, once the user hasn't used them
 * after a certain amount of time.
 * @author u/Zavarov
 */
public class MessageTracker implements Runnable, Killable{
    /**
     * The map that contains all stored messages.
     */
    protected final Map<Long,InteractiveMessage> messages;
    /**
     * The scheduler that removes the ability to interact
     * with messages that are too old.
     */
    protected final ScheduledExecutorService executor;
    /**
     * The log of this class.
     */
    protected final Logger log = JDALogger.getLog(this.getClass().getSimpleName());
    /**
     * The communicator of the program.
     */
    protected final Communicator comm;
    /**
     * @param comm the communicator of the program.
     */
    public MessageTracker(Communicator comm){
        this.comm = comm;
        this.executor = Executors.newSingleThreadScheduledExecutor();
        this.messages = new Object2ObjectOpenHashMap<>();
        executor.scheduleAtFixedRate(
                MessageTracker.this, 
                comm.environment().config().getInteractiveMessageAge(), 
                comm.environment().config().getInteractiveMessageAge(), 
                TimeUnit.MINUTES);
    }
    /**
     * Adds the message to the underlying map.
     * @param message the message.
     */
    public synchronized void add(InteractiveMessage message){
        messages.put(message.getCurrentMessage().getIdLong(),message);
    }
    /**
     * Forwards the reaction to the message, if such a message exists.
     * @param id the id of the message.
     * @param user the user that reacted.
     * @param reaction the reaction.
     */
    public synchronized void update(long id, User user, MessageReaction reaction){
        messages.computeIfPresent(id, (k,v) -> {v.add(user, reaction); return v;});
    }
    /**
     * Resubmits the interface when the command has been used since the last update or disable it otherwise.
     */
    @Override
    public synchronized void run(){
        int count = messages.size();
        
        OffsetDateTime now = OffsetDateTime.now();
        messages.entrySet().
                removeIf(e -> e.getValue()
                        .getLastReaction()
                        .plusMinutes(comm.environment().config().getInteractiveMessageAge())
                        .isBefore(now));
        
        //Number of removed elements
        count -= messages.size();
        if(count > 0){
            log.info(String.format("Removed %d interactive %s",count, English.plural("message", count)));
        }
    }
    /**
     * Stops removing interactive messages that have been abandoned.
     */
    @Override
    public void shutdown() {
        executor.shutdownNow();
    }
}