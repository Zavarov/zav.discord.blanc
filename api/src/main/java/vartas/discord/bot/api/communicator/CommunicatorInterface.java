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
package vartas.discord.bot.api.communicator;

import net.dv8tion.jda.core.JDA;
import vartas.discord.bot.api.environment.EnvironmentInterface;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * This interface is intended to hide the communication with the underlying
 * APIs and other libraries from the respective commands.
 * Instead of directly, they have to be accessed via this
 */
public interface CommunicatorInterface extends SendInterface, ActivityInterface, ConfigInterface{
    /**
     * The executor that deals with all asynchronous processes.
     */
    ExecutorService executor = Executors.newSingleThreadExecutor();
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
}
