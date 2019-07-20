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
import vartas.discord.bot.command.entity._ast.ASTGuildType;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;

public class GuildSymbol extends GuildSymbolTOP{
    protected ASTGuildType ast;

    public GuildSymbol(String name) {
        super(name);
    }

    public void setValue(ASTGuildType ast){
        this.ast = ast;
    }

    public ASTGuildType getValue(){
        return ast;
    }

    public Optional<Guild> resolve(Message context){
        checkNotNull(context);
        checkNotNull(context.getJDA());

        Collection<Guild> guilds = Collections.emptyList();

        if(ast.isPresentId())
            guilds = Collections.singleton(context.getJDA().getGuildById(ast.getId().getValue()));
        else if(ast.isPresentName())
            guilds = context.getJDA().getGuildsByName(ast.getName().getValue(), false);

        if(guilds.size() != 1)
            return Optional.empty();
        else
            return guilds.stream().findAny();
    }
}
