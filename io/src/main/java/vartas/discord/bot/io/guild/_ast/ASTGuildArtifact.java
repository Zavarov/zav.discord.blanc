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

package vartas.discord.bot.io.guild._ast;


import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import de.monticore.mcbasicliterals._ast.ASTBasicLongLiteral;
import de.monticore.mcbasicliterals._ast.ASTStringLiteral;
import vartas.discord.bot.io.guild._symboltable.FilterSymbol;
import vartas.discord.bot.io.guild._symboltable.PrefixSymbol;
import vartas.discord.bot.io.guild._symboltable.SubredditSymbol;
import vartas.discord.bot.io.guild._symboltable.TagSymbol;

import java.util.*;
import java.util.stream.Collectors;

public class ASTGuildArtifact extends ASTGuildArtifactTOP{
    protected ASTGuildArtifact(){
        super();
    }
    protected ASTGuildArtifact(List<ASTEntry> entryList){
        super(entryList);
    }

    public Optional<String> getPrefix(){
        Optional<PrefixSymbol> symbol = getEnclosingScope().resolve("prefix", PrefixSymbol.KIND);
        if(symbol.isPresent())
            return Optional.of(symbol.get().getPrefixNode().get().getPrefix().getValue());
        else
            return Optional.empty();
    }

    public Multimap<String, Long> getSubredditFeeds(){
        Collection<SubredditSymbol> symbols = getEnclosingScope().resolveMany("subreddit", SubredditSymbol.KIND);
        Multimap<String, Long> target = HashMultimap.create();

        for(SubredditSymbol symbol : symbols){
            ASTSubreddit ast = symbol.getSubredditNode().get();
            for(ASTBasicLongLiteral textchannel : ast.getTextchannelList())
                target.put(ast.getSubreddit().getValue(), textchannel.getValue());
        }

        return target;
    }

    public Multimap<String, Long> getRoleGroups(){
        Collection<TagSymbol> symbols = getEnclosingScope().resolveMany("tag", TagSymbol.KIND);
        Multimap<String, Long> target = HashMultimap.create();

        for(TagSymbol symbol : symbols){
            ASTTag ast = symbol.getTagNode().get();
            for(ASTBasicLongLiteral role : ast.getRoleList())
                target.put(ast.getTag().getValue(), role.getValue());
        }

        return target;
    }

    public Set<String> getFilter(){
        Optional<FilterSymbol> symbol = getEnclosingScope().resolve("filter", FilterSymbol.KIND);

        if(symbol.isPresent())
            return symbol.get().getFilterNode().get().getExpressionList().stream().map(ASTStringLiteral::getValue).collect(Collectors.toSet());
        else
            return Collections.emptySet();
    }
}
