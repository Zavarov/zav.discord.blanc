/*
 * Copyright (c) 2020 Zavarov
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

package zav.discord.blanc.activity;

import zav.discord.blanc._visitor.ArchitectureVisitor;
import zav.discord.blanc.Guild;
import zav.discord.blanc.Shard;
import zav.discord.blanc.activity.Activity;

/**
 * This visitor traverses through every {@link Guild} in a shard and calls the <code>update</code> method of the
 * corresponding {@link Guild}. By doing this periodically, we are able to track the guilds activity over time.
 */
public class ActivityVisitor implements ArchitectureVisitor {
    /**
     * Triggers the update method of the corresponding {@link Activity}
     * @param guild One of the guilds in the corresponding {@link Shard}.
     * @see Activity
     */
    @Override
    public void handle(Guild guild){
        guild.updateActivity(guild);
    }
}
