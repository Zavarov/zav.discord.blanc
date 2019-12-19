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

package vartas.discord.bot.visitor;

import com.google.common.base.Preconditions;
import vartas.discord.bot.entities.Shard;

import javax.annotation.Nonnull;

public interface ShardVisitor extends ConfigurationVisitor, RankVisitor, CredentialsVisitor, ActivityListenerVisitor {
    default void visit(int shardId, @Nonnull Shard shard){}

    default void traverse(int shardId, @Nonnull Shard shard) throws NullPointerException{
        Preconditions.checkNotNull(shard);
        shard.accept(this);
    }

    default void endVisit(int shardId, @Nonnull Shard shard){

    }

    default void handle(@Nonnull Shard shard) throws NullPointerException{
        Preconditions.checkNotNull(shard);
        int shardId = shard.jda().getShardInfo().getShardId();
        visit(shardId, shard);
        traverse(shardId, shard);
        endVisit(shardId, shard);
    }
}
