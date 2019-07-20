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

package vartas.discord.bot.command.parameter._symboltable;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;

public class GuildSymbol extends GuildSymbolTOP{
    protected Optional<BigDecimal> id = Optional.empty();
    protected Optional<String> name = Optional.empty();

    public GuildSymbol(String name) {
        super(name);
    }

    public void setValue(String name){
        this.name = Optional.of(name);
    }

    public void setValue(BigDecimal id){
        this.id = Optional.of(id);
    }

    public Optional<Guild> resolve(Message context){
        checkNotNull(context);
        checkNotNull(context.getJDA());

        Collection<Guild> guilds = Collections.emptyList();

        if(id.isPresent())
            guilds = Collections.singleton(context.getJDA().getGuildById(id.get().longValueExact()));
        else if(name.isPresent())
            guilds = context.getJDA().getGuildsByName(name.get(), false);

        if(guilds.size() != 1)
            return Optional.empty();
        else
            return guilds.stream().findAny();
    }
}
