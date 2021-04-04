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

package zav.discord.blanc;

import zav.discord.blanc._visitor.ArchitectureVisitor;
import zav.discord.blanc.io.Credentials;

public abstract class ShardLoader implements ArchitectureVisitor {
    private final Credentials credentials;
    private final int shardCount;

    public ShardLoader(Credentials credentials){
        this.credentials = credentials;
        this.shardCount = credentials.getShardCount();
    }

    public abstract Shard load(int shardId);
}
