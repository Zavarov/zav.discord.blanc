package vartas.discord.bot.command.parameter._symboltable;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import vartas.discord.bot.command.entity._ast.ASTUserType;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;

/*
 * Copyright (C) 2019 Zavarov
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
public class UserSymbol extends UserSymbolTOP{
    protected ASTUserType ast;

    public UserSymbol(String name) {
        super(name);
    }

    public void setValue(ASTUserType ast){
        this.ast = ast;
    }

    public ASTUserType getValue(){
        return ast;
    }

    public Optional<User> resolve(Message context){
        checkNotNull(context);
        checkNotNull(context.getJDA());

        Collection<User> users = Collections.emptyList();

        if(ast.isPresentId())
            users = Collections.singleton(context.getJDA().getUserById(ast.getId().getValue()));
        else if(ast.isPresentName())
            users = context.getJDA().getUsersByName(ast.getName().getValue(), false);

        if(users.size() != 1)
            return Optional.empty();
        else
            return users.stream().findAny();
    }
}
