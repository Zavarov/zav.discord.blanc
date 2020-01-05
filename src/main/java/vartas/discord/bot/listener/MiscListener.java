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

package vartas.discord.bot.listener;

import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import vartas.discord.bot.entities.Shard;
import vartas.discord.bot.internal.DeleteConfigurationFile;

import javax.annotation.Nonnull;

/**
 * This listener keeps track of all activities that aren't covered by the remaining listeners
 */
@Nonnull
public class MiscListener extends ListenerAdapter{
    /**
     * The shard is necessary for the I/O access. More specifically, we at least need it when removing configurations
     * and their corresponding files.
     */
    @Nonnull
    private final Shard shard;

    /**
     * Creates a fresh listener
     * @param shard the shard associated with this listener.
     */
    public MiscListener(@Nonnull Shard shard){
        this.shard = shard;
    }

    /**
     * This bot left a guild. Meaning that we can safely delete its configuration.
     * @param event the corresponding event.
     */
    @Override
    public void onGuildLeave(@Nonnull GuildLeaveEvent event){
        shard.getCluster().accept(new DeleteConfigurationFile(event.getGuild().getIdLong()));
    }

    /**
     * The hook point for the visitor pattern.
     * @param visitor the visitor traversing through this listener
     */
    public void accept(@Nonnull Visitor visitor){
        visitor.handle(this);
    }

    /**
     * The visitor pattern for this listener.
     */
    @Nonnull
    public interface Visitor {
        /**
         * The method that is invoked before the sub-nodes are handled.
         * @param miscListener the corresponding listener
         */
        default void visit(@Nonnull MiscListener miscListener){}

        /**
         * The method that is invoked to handle all sub-nodes.
         * @param miscListener the corresponding listener
         */
        default void traverse(@Nonnull MiscListener miscListener) {}

        /**
         * The method that is invoked after the sub-nodes have been handled.
         * @param miscListener the corresponding listener
         */
        default void endVisit(@Nonnull MiscListener miscListener){}

        /**
         * The top method of the listener visitor, calling the remaining visitor methods.
         * The order in which the methods are called is
         * <ul>
         *      <li>visit</li>
         *      <li>traverse</li>
         *      <li>endvisit</li>
         * </ul>
         * @param miscListener the corresponding listener
         */
        default void handle(@Nonnull MiscListener miscListener) {
            visit(miscListener);
            traverse(miscListener);
            endVisit(miscListener);
        }
    }
}
