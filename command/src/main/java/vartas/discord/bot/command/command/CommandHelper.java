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

package vartas.discord.bot.command.command;

import de.monticore.symboltable.GlobalScope;
import de.monticore.symboltable.ResolvingConfiguration;
import vartas.discord.bot.command.command._ast.ASTCommandArtifact;
import vartas.discord.bot.command.command._parser.CommandParser;
import vartas.discord.bot.command.command._symboltable.CommandSymbolTableCreator;

import java.io.IOException;
import java.util.Optional;

public abstract class CommandHelper {
    public static ASTCommandArtifact parse(GlobalScope scope, String filePath) throws IllegalArgumentException{
        try{
            CommandSymbolTableCreator symbolTableCreator = createSymbolTableCreator(scope);

            CommandParser parser = new CommandParser();
            Optional<ASTCommandArtifact> commands = parser.parse(filePath);
            if(parser.hasErrors())
                throw new IllegalArgumentException("The parser encountered errors while parsing "+filePath);
            if(!commands.isPresent())
                throw new IllegalArgumentException("The guild configuration file couldn't be parsed");

            ASTCommandArtifact ast = commands.get();
            symbolTableCreator.createFromAST(ast);

            return ast;
        }catch(IOException e){
            throw new IllegalArgumentException(e);
        }
    }

    private static CommandSymbolTableCreator createSymbolTableCreator(GlobalScope scope){
        ResolvingConfiguration resolvingConfiguration = new ResolvingConfiguration();

        resolvingConfiguration.addDefaultFilters(scope.getResolvingFilters());

        return new CommandSymbolTableCreator(resolvingConfiguration, scope);
    }
}