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
import vartas.discord.blanc.Guild;
import vartas.discord.blanc.JDAMessage;
import vartas.discord.blanc.Message;
import vartas.discord.blanc.Shard;
import vartas.discord.blanc.command.CommandBuilder;

import javax.annotation.Nonnull;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Nonnull
public class GuildCommandListener extends ListenerAdapter {
    @Nonnull
    private final ExecutorService executor = Executors.newWorkStealingPool();
    @Nonnull
    private final CommandBuilder commandBuilder;
    @Nonnull
    private final Shard shard;

    public GuildCommandListener(@Nonnull CommandBuilder commandBuilder, @Nonnull Shard shard){
        this.commandBuilder = commandBuilder;
        this.shard = shard;
    }

    @Override
    public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event){
        Message message = JDAMessage.create(event.getMessage());
        Guild guild = shard.getUncheckedGuilds(event.getGuild().getIdLong());

        commandBuilder.build(message, guild).ifPresent(executor::submit);
    }
}
