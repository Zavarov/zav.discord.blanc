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

package vartas.discord.bot.entities.guild;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.internal.utils.cache.UpstreamReference;
import vartas.discord.bot.entities.DiscordCommunicator;
import vartas.discord.bot.listener.CommandListener;
import vartas.discord.bot.visitor.DiscordCommunicatorVisitor;
import vartas.discord.bot.visitor.guild.PrefixVisitor;

import java.util.Optional;

public class Prefix {
    protected String prefix;
    protected DiscordCommunicator communicator;
    protected UpstreamReference<Guild> guild;

    public Prefix(Guild guild, DiscordCommunicator communicator){
        this.communicator = communicator;
        this.guild = new UpstreamReference<>(guild);
    }

    public void set(String prefix){
        this.prefix = prefix;

        Optional<String> prefixOpt = get();
        if(prefixOpt.isPresent())
            new AddPrefixVisitor().accept(guild.get(), prefixOpt.get());
        else
            new RemovePrefixVisitor().accept(guild.get());
    }

    public Optional<String> get(){
        return Optional.ofNullable(prefix);
    }

    public void accept(PrefixVisitor visitor){
        get().ifPresent(visitor::handle);
    }

    @Override
    public synchronized String toString(){
        StringBuilder builder = new StringBuilder();

        get().ifPresent(prefix -> {
            builder.append("  prefix \"").append(prefix).append("\"\n");
        });

        return builder.toString();
    }

    private class RemovePrefixVisitor implements DiscordCommunicatorVisitor {
        protected Guild guild;

        public void accept(Guild guild){
            this.guild = guild;
            communicator.accept(this);
        }

        public void handle(CommandListener listener){
            listener.remove(guild);
        }
    }

    private class AddPrefixVisitor implements DiscordCommunicatorVisitor{
        protected Guild guild;
        protected String prefix;

        public void accept(Guild guild, String prefix){
            this.guild = guild;
            this.prefix = prefix;
            communicator.accept(this);
        }

        public void handle(CommandListener listener){
            listener.set(guild, prefix);
        }
    }
}
