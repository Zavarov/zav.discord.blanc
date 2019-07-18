package vartas.discord.bot.command.command._symboltable;

import de.monticore.symboltable.ArtifactScope;
import de.monticore.symboltable.ResolvingConfiguration;
import de.monticore.symboltable.Scope;
import de.se_rwth.commons.Joiners;
import vartas.discord.bot.command.command._ast.ASTCommand;
import vartas.discord.bot.command.command._ast.ASTCommandArtifact;

import java.util.ArrayList;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

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
public class CommandSymbolTableCreator extends CommandSymbolTableCreatorTOP{
    public CommandSymbolTableCreator(ResolvingConfiguration resolverConfig, Scope enclosingScope) {
        super(resolverConfig, enclosingScope);
    }

    @Override
    public ArtifactScope createFromAST(ASTCommandArtifact rootNode) {
        requireNonNull(rootNode);

        String packageName = Joiners.DOT.join(rootNode.getPrefixList());

        final ArtifactScope artifactScope = new ArtifactScope(Optional.empty(), packageName, new ArrayList<>());
        putOnStack(artifactScope);

        rootNode.accept(this);

        return artifactScope;
    }

    @Override
    public void visit(ASTCommand node){
        CommandSymbol symbol = new CommandSymbol(node.getName());
        addToScopeAndLinkWithNode(symbol, node);
        node.setCommandSymbol(symbol);
    }
}
