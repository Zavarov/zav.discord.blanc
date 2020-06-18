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

package vartas.discord.blanc.parser;

import net.dv8tion.jda.api.JDA;
import vartas.discord.blanc.*;

import javax.annotation.Nonnull;

@Nonnull
public class JDATypeResolver extends AbstractTypeResolver{
    @Nonnull
    private final Shard shard;
    @Nonnull
    private final JDA jda;

    public JDATypeResolver(@Nonnull Shard shard, @Nonnull JDA jda){
        this.shard = shard;
        this.jda = jda;
    }

    @Override
    public Guild resolveGuild(Argument argument) {
        return new JDAGuildResolver(shard, jda).apply(argument).orElseThrow();
    }

    @Override
    public TextChannel resolveTextChannel(Guild guild, Argument argument) {
        return null;
    }

    @Override
    public User resolveUser(Argument argument) {
        return new JDAUserResolver(shard, jda).apply(argument).orElseThrow();
    }

    @Override
    public Role resolveRole(Guild guild, Argument argument) {
        return null;
    }
}
