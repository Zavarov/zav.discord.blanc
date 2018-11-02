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

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import net.dv8tion.jda.core.entities.MessageReaction;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.utils.JDALogger;
import org.atteo.evo.inflector.English;
import org.slf4j.Logger;
import vartas.discordbot.messages.InteractiveMessage;

/**
 * A runnable that removes interactive messages, once the user hasn't used them
 * after a certain amount of time.
 * @author u/Zavarov
 */
public class MessageTracker extends HashMap<Long,InteractiveMessage> implements Runnable, Killable{
    private static final long serialVersionUID = 1L;
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
     * The time that can pass before the message becomes inactive.
     */
    protected final long interval;
    /**
     * @param interval the time in minutes that can pass before the message becomes inactive.
     */
    public MessageTracker(long interval){
        this.interval = interval;
        this.executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(MessageTracker.this, interval, interval, TimeUnit.MINUTES);
    }
    /**
     * Forwards the reaction to the message, if such a message exists.
     * @param id the id of the message.
     * @param user the user that reacted.
     * @param reaction the reaction.
     */
    public synchronized void update(long id, User user, MessageReaction reaction){
        computeIfPresent(id, (k,v) -> {v.add(user, reaction); return v;});
    }
    /**
     * Resubmits the interface when the command has been used since the last update or disable it otherwise.
     */
    @Override
    public synchronized void run(){
        int size = size();
        
        OffsetDateTime now = OffsetDateTime.now();
        entrySet().removeIf(e -> e.getValue().getLastReaction().plusMinutes(interval).isBefore(now));
        
        //Number of removed elements
        size -= size();
        if(size > 0){
            log.info(String.format("Removed %d interactive %s",size, English.plural("message", size)));
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