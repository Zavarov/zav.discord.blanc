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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vartas.discord.blanc.Guild;
import vartas.discord.blanc.Shard;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Optional;

public abstract class AbstractJDAGuildResolver<U, V> implements GuildTypeResolver<V>{
    @Nonnull
    protected final Logger log = LoggerFactory.getLogger(getClass().getSimpleName());
    @Nonnull
    protected final Shard shard;
    @Nonnull
    protected final JDA jda;
    @Nullable
    protected U snowflake;
    @Nullable
    protected net.dv8tion.jda.api.entities.Guild guild;

    public AbstractJDAGuildResolver(@Nonnull Shard shard, @Nonnull JDA jda) {
        this.shard = shard;
        this.jda = jda;
    }

    @Override
    public Optional<V> apply(Guild guild, Argument argument) {
        this.guild = jda.getGuildById(guild.getId());
        this.snowflake = null;
        argument.accept(this);
        return Optional.ofNullable(snowflake).flatMap(snowflake -> map(guild, snowflake));
    }

    @Nonnull
    protected abstract Collection<U> resolveByName(String name);

    @Nullable
    protected abstract U resolveByNumber(Number number);

    @Nonnull
    protected abstract Optional<V> map(Guild guild, U snowflake);
}
