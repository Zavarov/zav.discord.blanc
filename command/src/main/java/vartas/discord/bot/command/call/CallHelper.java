package vartas.discord.bot.command.call;

import de.monticore.ModelingLanguage;
import de.monticore.io.paths.ModelPath;
import de.monticore.symboltable.GlobalScope;
import de.monticore.symboltable.ResolvingConfiguration;
import vartas.discord.bot.command.call._ast.ASTCallArtifact;
import vartas.discord.bot.command.call._parser.CallParser;
import vartas.discord.bot.command.call._symboltable.CallSymbolTableCreator;
import vartas.discord.bot.command.command._symboltable.CommandLanguage;
import vartas.discord.bot.command.command._symboltable.CommandSymbol;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Optional;

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
public abstract class CallHelper {
    public static ASTCallArtifact parse(GlobalScope commandScope, String content) throws IllegalArgumentException{
        try{
            CallSymbolTableCreator symbolTableCreator = createSymbolTableCreator();
            CallParser parser = new CallParser();

            Optional<ASTCallArtifact> call = parser.parse_String(content);
            if(parser.hasErrors())
                throw new IllegalArgumentException("The parser encountered errors while parsing "+content);
            if(!call.isPresent())
                throw new IllegalArgumentException("The command file couldn't be parsed");

            ASTCallArtifact ast = call.get();

            Optional<CommandSymbol> symbol = commandScope.resolve(ast.getQualifiedName(), CommandSymbol.KIND);

            if(!symbol.isPresent())
                throw new IllegalArgumentException("The command "+ast.getQualifiedName()+" couldn't be resolved.");

            symbolTableCreator.setCommandSymbol(symbol.get());
            symbolTableCreator.createFromAST(ast);

            return ast;
        }catch(IOException e){
            throw new IllegalArgumentException(e);
        }
    }

    private static GlobalScope createGlobalScope(){
        ModelPath path = new ModelPath(Paths.get(""));
        ModelingLanguage language = new CommandLanguage();
        return new GlobalScope(path, language);
    }

    private static CallSymbolTableCreator createSymbolTableCreator(){
        GlobalScope globalScope = createGlobalScope();
        ResolvingConfiguration resolvingConfiguration = new ResolvingConfiguration();

        resolvingConfiguration.addDefaultFilters(globalScope.getResolvingFilters());

        return new CallSymbolTableCreator(resolvingConfiguration, globalScope);
    }
}
