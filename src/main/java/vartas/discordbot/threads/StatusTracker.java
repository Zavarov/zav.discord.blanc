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

import java.io.File;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.utils.JDALogger;
import org.slf4j.Logger;
import vartas.xml.strings.XMLStringList;

/**
 * This runner is responsible for updating the game of the bot, which is used as a status message.
 * @author u/Zavarov
 */
public class StatusTracker implements Runnable, Killable{
    /**
     * The executor that frequently changes the status message.
     */
    protected final ScheduledExecutorService executor;
    /**
     * The log of this class.
     */
    protected final Logger log = JDALogger.getLog(this.getClass().getSimpleName());
    /**
     * The client.
     */
    protected final JDA jda;
    /**
     * A random number generator to pick a status message.
     */
    protected final Random random = new Random();
    /**
     * The file containing all available messages.
     */
    protected final XMLStringList status;
    /**
     * @param jda the api.
     * @param file the file containing the status messages.
     * @param interval the interval between each change.
     */
    public StatusTracker(JDA jda, File file, int interval){
        this.jda = jda;
        this.status = XMLStringList.create(file);
        executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(StatusTracker.this,0, interval, TimeUnit.MINUTES);
        log.info(String.format("Tracker #%d started.",jda.getShardInfo().getShardId()));
    }
    /**
     * Changes the message to a new one.
     */
    @Override
    public void run() {
        String message = status.get(random.nextInt(status.size()));
        jda.getPresence().setGame(Game.playing(message));
        log.info(String.format("Game changed to '%s'",message));
    }
    /**
     * Stops changing the status message.
     */
    @Override
    public void shutdown(){
        executor.shutdownNow();
    }
}