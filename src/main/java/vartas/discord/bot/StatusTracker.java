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

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.internal.utils.JDALogger;
import org.slf4j.Logger;
import vartas.discord.bot.entities.DiscordEnvironment;
import vartas.discord.bot.entities.Status;

import java.util.Optional;

/**
 * This runner is responsible for updating the game of the bot, which is used as a status message.
 */
public class StatusTracker implements Runnable{
    /**
     * The log of this class.
     */
    protected final Logger log = JDALogger.getLog(this.getClass().getSimpleName());
    /**
     * The environment of the program.
     */
    protected final DiscordEnvironment environment;
    /**
     * All status messages.
     */
    protected final Status status;
    /**
     * Initializes the status.
     * @param environment the environment of the program.
     */
    public StatusTracker(DiscordEnvironment environment, Status status){
        this.environment = environment;
        this.status = status;
        log.info("Status Tracker started");
    }
    /**
     * Changes the message to a new one.
     */
    @Override
    public void run() {
        Optional<String> messageOpt = status.get();

        if(messageOpt.isPresent()) {
            String message = messageOpt.get();
            environment.jdas()
                    .stream()
                    .map(JDA::getPresence)
                    .forEach(p -> p.setActivity(Activity.playing(message)));
            log.info(String.format("Status message changed to '%s'",message));
        }else{
            log.info("No status message to switch to.");
        }
    }
}