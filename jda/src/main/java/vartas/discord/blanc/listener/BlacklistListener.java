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

package vartas.discord.blanc.listener;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.internal.utils.PermissionUtil;
import vartas.discord.blanc.*;

import javax.annotation.Nonnull;

public class BlacklistListener extends ShardListener {
    public BlacklistListener(@Nonnull Shard shard){
        super(shard);
    }

    @Override
    public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event){
        //Exclude this bot from the blacklist & only proceed when the bot has the required permissions
        if(!isSelf(event.getAuthor()) && canDelete(event.getGuild(), event.getChannel())){
            Guild guild = shard.getUncheckedGuilds(event.getGuild().getIdLong());

            //Only proceed if a blacklist has been defined
            guild.getPattern().ifPresent(pattern -> {
                //Does the message contain the pattern?
                if(pattern.matcher(event.getMessage().getContentRaw()).find())
                    event.getMessage().delete().complete();
            });
        }
    }

    private boolean isSelf(net.dv8tion.jda.api.entities.User author){
        return author.getIdLong() == shard.getSelfUser().getId();
    }

    private boolean canDelete(net.dv8tion.jda.api.entities.Guild guild, net.dv8tion.jda.api.entities.TextChannel channel){
        return PermissionUtil.checkPermission(channel, guild.getSelfMember(), Permission.MESSAGE_MANAGE);
    }
}
