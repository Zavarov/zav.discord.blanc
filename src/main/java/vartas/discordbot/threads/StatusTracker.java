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

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.utils.JDALogger;
import org.slf4j.Logger;
import vartas.discordbot.comm.Environment;

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
     * The environment of the program.
     */
    protected final Environment environment;
    /**
     * A random number generator to pick a status message.
     */
    protected final Random random = new Random();
    /**
     * Initializes the status.
     * @param environment the environment of the program.
     */
    public StatusTracker(Environment environment){
        this.environment = environment;
        
        executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(
                StatusTracker.this,
                0,
                environment.config().getStatusInterval(), 
                TimeUnit.MINUTES);
        log.info("Status Tracker started");
    }
    /**
     * Changes the message to a new one.
     */
    @Override
    public void run() {
        String message = environment.status().get(random.nextInt(environment.status().size()));
        environment.game(Game.playing(message));
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