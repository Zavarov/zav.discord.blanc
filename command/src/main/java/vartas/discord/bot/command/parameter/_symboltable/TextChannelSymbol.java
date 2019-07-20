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

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;

public class TextChannelSymbol extends TextChannelSymbolTOP{
    protected Optional<Long> id = Optional.empty();
    protected Optional<String> name = Optional.empty();

    public TextChannelSymbol(String name) {
        super(name);
    }

    public void setValue(String name){
        this.name = Optional.of(name);
    }

    public void setValue(long id){
        this.id = Optional.of(id);
    }


    public Optional<TextChannel> resolve(Message context){
        checkNotNull(context);
        checkNotNull(context.getGuild());

        Collection<TextChannel> channels = Collections.emptyList();

        if(id.isPresent())
            channels = Collections.singleton(context.getGuild().getTextChannelById(id.get()));
        else if(name.isPresent())
            channels = context.getGuild().getTextChannelsByName(name.get(), false);

        if(channels.size() != 1)
            return Optional.empty();
        else
            return channels.stream().findAny();
    }
}
