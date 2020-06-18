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

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import vartas.discord.blanc.*;
import vartas.discord.blanc.command.CommandBuilder;

import javax.annotation.Nonnull;

public class GuildMessageListener extends ListenerAdapter {
    @Nonnull
    private Shard shard;

    public GuildMessageListener(@Nonnull Shard shard){
        this.shard = shard;
    }
    @Override
    public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event){
        Guild guild = shard.getUncheckedGuilds(event.getGuild().getIdLong());
        TextChannel textChannel = guild.getUncheckedChannels(event.getChannel().getIdLong());
        Message message = JDAMessage.create(event.getMessage());

        textChannel.putMessages(message.getId(), message);
    }
}
