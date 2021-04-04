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

package zav.discord.blanc._visitor;

import zav.discord.blanc.Guild;
import zav.discord.blanc.Shard;
import zav.discord.blanc.TextChannel;
import zav.discord.blanc.Webhook;

import javax.annotation.Nonnull;

public interface ArchitectureVisitor extends ArchitectureVisitorTOP{
    @Override
    default ArchitectureVisitor getRealThis(){
        return this;
    }

    @Override
    default void traverse(@Nonnull Shard shard){
        shard.retrieveGuilds().forEach(guild -> guild.accept(getRealThis()));
        shard.retrieveUsers().forEach(user -> user.accept(getRealThis()));
        shard.retrieveSelfUser().accept(getRealThis());
    }

    @Override
    default void traverse(@Nonnull Guild guild){
        guild.retrieveMembers().forEach(member -> member.accept(getRealThis()));
        guild.retrieveRoles().forEach(role -> role.accept(getRealThis()));
        guild.retrieveTextChannels().forEach(channel -> channel.accept(getRealThis()));
        guild.retrieveSelfMember().accept(getRealThis());
    }

    @Override
    default void traverse(@Nonnull TextChannel channel){
        channel.retrieveWebhooks().forEach(webhook -> webhook.accept(getRealThis()));
        channel.retrieveMessages().forEach(message -> message.accept(getRealThis()));
    }

    @Override
    default void traverse(@Nonnull Webhook webhook){
        webhook.retrieveMessages().forEach(message -> message.accept(getRealThis()));
    }
}
