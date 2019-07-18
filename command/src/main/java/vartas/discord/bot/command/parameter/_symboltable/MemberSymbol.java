package vartas.discord.bot.command.parameter._symboltable;

import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import vartas.discord.bot.command.entity._ast.ASTMemberType;

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
public class MemberSymbol extends MemberSymbolTOP{
    protected ASTMemberType ast;

    public MemberSymbol(String name) {
        super(name);
    }

    public void setValue(ASTMemberType ast){
        this.ast = ast;
    }

    public ASTMemberType getValue(){
        return ast;
    }

    public Optional<Member> resolve(Message source){
        checkNotNull(source);
        checkNotNull(source.getGuild());

        Collection<Member> members = Collections.emptyList();

        if(ast.isPresentId())
            members = Collections.singleton(source.getGuild().getMemberById(ast.getId().getValue()));
        else if(ast.isPresentName())
            members = source.getGuild().getMembersByName(ast.getName().getValue(), false);

        if(members.size() != 1)
            return Optional.empty();
        else
            return members.stream().findAny();
    }
}
