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

import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.function.Supplier;

import static com.google.common.base.Preconditions.checkNotNull;

public class MemberSymbol extends MemberSymbolTOP{
    protected Optional<Supplier<BigDecimal>> id = Optional.empty();
    protected Optional<String> name = Optional.empty();

    public MemberSymbol(String name) {
        super(name);
    }

    public void setValue(String name){
        this.name = Optional.of(name);
    }

    public void setValue(Supplier<BigDecimal> id){
        this.id = Optional.of(id);
    }

    public void setValue(BigDecimal id){
        this.id = Optional.of(() -> id);
    }

    public String getQualifiedResolvedName(){
        return Member.class.getCanonicalName();
    }

    /**
     * We first attempt to resolve the member by its name first, if it is present.
     * If this fails we try to resolve it via the id.
     * @param context the message that is necessary to uniquely identify the member.
     * @return The resolved member instance.
     */
    public Optional<Member> resolve(Message context){
        checkNotNull(context);
        checkNotNull(context.getGuild());

        Collection<Member> members = Collections.emptyList();

        if(name.isPresent())
            members = context.getGuild().getMembersByName(name.get(), false);
        if(id.isPresent() && members.isEmpty())
            members = Collections.singleton(context.getGuild().getMemberById(id.get().get().longValueExact()));

        if(members.size() != 1)
            return Optional.empty();
        else
            return members.stream().findAny();
    }
}
