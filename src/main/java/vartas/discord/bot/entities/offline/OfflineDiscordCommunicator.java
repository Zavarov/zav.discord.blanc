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

package vartas.discord.bot.entities.offline;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.requests.RestAction;
import vartas.discord.bot.CommandBuilder;
import vartas.discord.bot.EntityAdapter;
import vartas.discord.bot.entities.DiscordCommunicator;
import vartas.discord.bot.entities.DiscordEnvironment;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class OfflineDiscordCommunicator extends DiscordCommunicator {
    public List<? super Object> send = new ArrayList<>();
    public List<Guild> removed = new ArrayList<>();

    public OfflineDiscordCommunicator(DiscordEnvironment environment, JDA jda, Function<DiscordCommunicator, CommandBuilder> builder, EntityAdapter adapter) {
        super(environment, jda, builder, adapter);
    }

    @Override
    public <T> void send(RestAction<T> action, Consumer<T> success, Consumer<Throwable> failure){
        send.add(action);
    }

    @Override
    public void schedule(Runnable runnable){
        runnable.run();
    }

    @Override
    public void remove(Guild guild){
        removed.add(guild);
    }
}
