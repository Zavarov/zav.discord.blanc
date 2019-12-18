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

package vartas.discord.bot;

import net.dv8tion.jda.internal.utils.JDALogger;
import org.slf4j.Logger;
import vartas.discord.bot.entities.Shard;
import vartas.discord.bot.entities.Status;
import vartas.discord.bot.mpi.command.MPIUpdateStatusMessage;
import vartas.discord.bot.mpi.serializable.MPIStatusMessageModification;

import java.util.Optional;

/**
 * This runner is responsible for updating the activity of the bot, which is used as a status message.
 */
public class StatusTracker implements Runnable{
    /**
     * The log of this class.
     */
    protected final Logger log = JDALogger.getLog(this.getClass().getSimpleName());
    /**
     * The shard of the master node.
     */
    protected final Shard shard;
    /**
     * All status messages.
     */
    protected final Status status;
    /**
     * Initializes the status.
     * @param shard the shard of the master node
     * @param status all valid status messages
     */
    public StatusTracker(Shard shard, Status status){
        this.shard = shard;
        this.status = status;
        log.info("Status Tracker started");
    }
    /**
     * Picks a new status message at random and updates
     */
    @Override
    public void run() {
        Optional<String> messageOpt = status.get();

        messageOpt.ifPresent(message -> {
            MPIStatusMessageModification object = new MPIStatusMessageModification(message);
            shard.send(MPIUpdateStatusMessage.createSendCommand(), object);
            log.info(String.format("Status message changed to '%s'",message));
        });
    }
}