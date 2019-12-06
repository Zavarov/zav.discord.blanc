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

package vartas.discord.bot.entities;

import com.google.common.base.Preconditions;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

/**
 * This name contains all valid status messages of the Discord bot.<br>
 * The current status message is displayed via an {@link net.dv8tion.jda.api.entities.Activity} of the individual
 * {@link net.dv8tion.jda.api.JDA} instance. However, this means that a change in status has to be made in all
 * shards.<br>
 * It is to note that there seems to be some inconsistencies regarding what status message is shown, when they differ
 * across shards.
 */
public class Status {
    /**
     * The random number generator for picking one status message out of all valid messages.
     */
    protected Random random = new Random();
    /**
     * The internal list containing all valid status messages.
     */
    protected List<String> status = new ArrayList<>();

    /**
     * Adds a new status message to the list of valid messages.<br>
     * Duplicates are allowed.
     * @param element one of the valid status messages.
     * @throws NullPointerException if {@code element} is null
     */
    public synchronized void add(@Nonnull String element) throws NullPointerException{
        Preconditions.checkNotNull(element);
        status.add(element);
    }

    /**
     * Since it is perfectly valid to not have a status message for the bot, we can't just pick a random
     * element in the list, without running into an {@link IndexOutOfBoundsException}.<br>
     * In an attempt to reduce the usage of {@code null}, we utilize {@link Optional} to indicate that no
     * new status message exists.
     * @return an {@link Optional} with a random status message if at least one valid message exists.
     *         Otherwise {@link Optional#empty()}.
     */
    public synchronized Optional<String> get(){
        if(status.isEmpty())
            return Optional.empty();

        int index = random.nextInt(status.size());
        return Optional.of(status.get(index));
    }
}
