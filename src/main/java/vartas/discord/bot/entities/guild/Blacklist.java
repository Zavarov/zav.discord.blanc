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
import vartas.discord.bot.listener.BlacklistListener;
import vartas.discord.bot.visitor.DiscordCommunicatorVisitor;
import vartas.discord.bot.visitor.guild.BlacklistVisitor;

import java.util.Optional;
import java.util.regex.Pattern;

public class Blacklist {
    protected Pattern pattern = null;
    protected DiscordCommunicator communicator;
    protected UpstreamReference<Guild> guild;

    public Blacklist(Guild guild, DiscordCommunicator communicator){
        this.communicator = communicator;
        this.guild = new UpstreamReference<>(guild);
    }

    public void set(Pattern blacklist){
        this.pattern = blacklist;

        Optional<Pattern> patternOpt = get();
        if(patternOpt.isPresent())
            new AddBlacklistVisitor().accept(guild.get(), patternOpt.get());
        else
            new RemoveBlacklistVisitor().accept(guild.get());

    }

    public Optional<Pattern> get(){
        return Optional.ofNullable(pattern);
    }

    public void accept(BlacklistVisitor visitor){
        get().ifPresent(visitor::handle);
    }

    @Override
    public String toString(){
        StringBuilder builder = new StringBuilder();

        get().ifPresent(prefix -> {
            builder.append("  blacklist \"").append(pattern).append("\"\n");
        });

        return builder.toString();
    }

    private class RemoveBlacklistVisitor implements DiscordCommunicatorVisitor{
        protected Guild guild;

        public void accept(Guild guild){
            this.guild = guild;
            communicator.accept(this);
        }

        public void handle(BlacklistListener listener){
            listener.remove(guild);
        }
    }

    private class AddBlacklistVisitor implements DiscordCommunicatorVisitor{
        protected Guild guild;
        protected Pattern pattern;

        public void accept(Guild guild, Pattern pattern){
            this.guild = guild;
            this.pattern = pattern;
            communicator.accept(this);
        }

        public void handle(BlacklistListener listener){
            listener.set(guild, pattern);
        }
    }
}
