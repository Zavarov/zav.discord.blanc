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

package vartas.discord.bot.api.threads;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.utils.JDALogger;
import org.slf4j.Logger;
import vartas.discord.bot.api.environment.EnvironmentInterface;
import vartas.discord.bot.io.status.StatusHelper;

import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * This runner is responsible for updating the game of the bot, which is used as a status message.
 */
public class StatusTracker implements Runnable{
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
    protected final EnvironmentInterface environment;
    /**
     * A random number generator to pick a status message.
     */
    protected final Random random = new Random();
    /**
     * All possible status message the bot can choose from.
     */
    protected final List<String> statusMessages;
    /**
     * Initializes the status.
     * @param environment the environment of the program.
     */
    public StatusTracker(EnvironmentInterface environment){
        this.environment = environment;
        this.statusMessages = StatusHelper.parse("status.stt").getStatusMessageList();
        
        executor = Executors.newSingleThreadScheduledExecutor(
            new ThreadFactoryBuilder().setNameFormat("Status Executor %d").build()
        );
        executor.scheduleAtFixedRate(
                StatusTracker.this,
                environment.config().getStatusMessageUpdateInterval(),
                environment.config().getStatusMessageUpdateInterval(),
                TimeUnit.MINUTES
        );
        log.info("Status Tracker started");
    }
    /**
     * Changes the message to a new one.
     */
    @Override
    public void run() {
        String message = statusMessages.get(random.nextInt(statusMessages.size()));

        environment.jdas()
                .stream()
                .map(JDA::getPresence)
                .forEach(p -> p.setGame(Game.playing(message)));

        log.info(String.format("Game changed to '%s'",message));
    }
}