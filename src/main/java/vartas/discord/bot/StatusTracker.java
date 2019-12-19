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

import vartas.discord.bot.entities.Cluster;
import vartas.discord.bot.entities.Status;

/**
 * This runner is responsible for updating the activity of the bot, which is used as a status message.
 */
public class StatusTracker {
    /**
     * The cluster of all shards
     */
    protected final Cluster cluster;
    /**
     * All status messages.
     */
    protected final Status status;
    /**
     * Initializes the status.
     * @param cluster the cluster of all shards
     * @param status all valid status messages
     */
    public StatusTracker(Cluster cluster, Status status){
        this.cluster = cluster;
        this.status = status;
    }
}