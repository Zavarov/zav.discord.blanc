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

package vartas.discord.bot.command.command._symboltable;

import net.dv8tion.jda.core.Permission;
import vartas.discord.bot.command.parameter._ast.ASTParameterType;
import vartas.discord.bot.command.permission._ast.ASTPermissionType;
import vartas.discord.bot.command.rank._ast.ASTRankType;
import vartas.discord.bot.io.rank.RankType;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CommandSymbol extends CommandSymbolTOP{
    public CommandSymbol(String name){
        super(name);
    }

    @Override
    public String getFullName(){
        //Avoid calling the broken Names class
        if(getPackageName() != null && !getPackageName().isEmpty())
            return getPackageName() + "." + getName();
        else
            return getName();
    }

    public String getClassName(){
        Optional<ClassNameSymbol> symbol = getSpannedScope().resolveLocally("class", ClassNameSymbol.KIND);
        return symbol.get().getClassNameNode().get().getValue();
    }

    public List<RankType> getValidRanks(){
        Optional<RankSymbol> symbol = getSpannedScope().resolveLocally("rank", RankSymbol.KIND);
        if(symbol.isPresent())
            return symbol.get().getRankNode().get().getRankTypeList().stream().map(ASTRankType::getRankType).collect(Collectors.toList());
        else
            return Collections.emptyList();
    }

    public List<Permission> getRequiredPermissions(){
        Optional<PermissionSymbol> symbol = getSpannedScope().resolveLocally("permission", PermissionSymbol.KIND);
        if(symbol.isPresent())
            return symbol.get().getPermissionNode().get().getPermissionTypeList().stream().map(ASTPermissionType::getPermissionType).collect(Collectors.toList());
        else
            return Collections.emptyList();
    }

    public List<ASTParameterType> getParameters(){
        Optional<ParameterSymbol> symbol = getSpannedScope().resolveLocally("parameter", ParameterSymbol.KIND);
        if(symbol.isPresent())
            return symbol.get().getParameterNode().get().getParameterTypeList();
        else
            return Collections.emptyList();
    }

    public boolean requiresGuild(){
        return getSpannedScope().resolveLocally("guild", GuildRestrictionSymbol.KIND).isPresent();
    }

    public boolean requiresAttachment(){
        return getSpannedScope().resolveLocally("attachment", AttachmentRestrictionSymbol.KIND).isPresent();
    }
}
