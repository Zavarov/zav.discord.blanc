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

package vartas.discord.entities;

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
@Nonnull
public class Status {
    /**
     * The random number generator for picking one status message out of all valid messages.
     */
    @Nonnull
    protected Random random = new Random();
    /**
     * The internal list containing all valid status messages.
     */
    @Nonnull
    protected List<String> entries = new ArrayList<>();

    /**
     * Adds a new status message to the list of valid messages.<br>
     * Duplicates are allowed.
     * @param element one of the valid status messages.
     * @throws NullPointerException if {@code element} is null
     */
    public synchronized void add(@Nonnull String element) throws NullPointerException{
        Preconditions.checkNotNull(element);
        entries.add(element);
    }

    /**
     * Since it is perfectly valid to not have a status message for the bot, we can't just pick a random
     * element in the list, without running into an {@link IndexOutOfBoundsException}.<br>
     * In an attempt to reduce the usage of {@code null}, we utilize {@link Optional} to indicate that no
     * new status message exists.
     * @return an {@link Optional} with a random status message if at least one valid message exists.
     *         Otherwise {@link Optional#empty()}.
     */
    @Nonnull
    public synchronized Optional<String> get(){
        if(entries.isEmpty())
            return Optional.empty();

        int index = random.nextInt(entries.size());
        return Optional.of(entries.get(index));
    }

    /**
     * The hook point for the visitor pattern.
     * @param visitor the visitor traversing through the status
     */
    public void accept(@Nonnull Visitor visitor){
        visitor.handle(this);
    }

    /**
     * The status visitor.
     */
    @Nonnull
    public interface Visitor{
        /**
         * The method that is invoked before the sub-nodes are handled.
         * @param status the corresponding status
         */
        default void visit(@Nonnull Status status){}

        /**
         * The method that is invoked to handle all sub-nodes.
         * @param status the corresponding status
         */
        default void traverse(@Nonnull Status status) {}

        /**
         * The method that is invoked after the sub-nodes have been handled.
         * @param status the corresponding status
         */
        default void endVisit(@Nonnull Status status){}

        /**
         * The top method of the status visitor, calling the visitor methods.
         * The order in which the methods are called is
         * <ul>
         *      <li>visit</li>
         *      <li>traverse</li>
         *      <li>endvisit</li>
         * </ul>
         * @param status the corresponding status
         */
        default void handle(@Nonnull Status status) {
            visit(status);
            traverse(status);
            endVisit(status);
        }
    }
}
