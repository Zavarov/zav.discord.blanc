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

import com.google.common.base.Preconditions;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import vartas.discord.bot.entities.Shard;

import javax.annotation.Nonnull;

/**
 * This listener keeps track of all activities that aren't covered by the remaining listeners
 */
@Nonnull
public class MiscListener extends ListenerAdapter {
    /**
     * The shard is necessary for the I/O access. More specifically, we at least need it when removing configurations
     * and their corresponding files.
     */
    @Nonnull
    protected Shard shard;

    /**
     * Creates a fresh listener
     * @param shard the shard associated with this listener.
     * @throws NullPointerException if {@code shard} is null
     */
    public MiscListener(@Nonnull Shard shard) throws NullPointerException{
        Preconditions.checkNotNull(shard);
        this.shard = shard;
    }

    /**
     * This bot left a guild. Meaning that we can safely delete its configuration.
     * @param event the corresponding event.
     * @throws NullPointerException if {@code event} is null
     */
    @Override
    public void onGuildLeave(@Nonnull GuildLeaveEvent event) throws NullPointerException{
        Preconditions.checkNotNull(event);
        shard.remove(event.getGuild());
    }
}
