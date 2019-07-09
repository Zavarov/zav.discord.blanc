package vartas.discord.bot.io.guild;

import de.monticore.ModelingLanguage;
import de.monticore.io.paths.ModelPath;
import de.monticore.symboltable.GlobalScope;
import de.monticore.symboltable.ResolvingConfiguration;
import vartas.discord.bot.io.guild._ast.ASTGuildArtifact;
import vartas.discord.bot.io.guild._parser.GuildParser;
import vartas.discord.bot.io.guild._symboltable.GuildLanguage;
import vartas.discord.bot.io.guild._symboltable.GuildSymbolTableCreator;

import java.io.IOException;
import java.io.File;
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
public abstract class GuildHelper {
    public static GuildConfiguration parse(String filePath, File reference){
        ASTGuildArtifact ast = parse(filePath);
        return new GuildConfiguration(ast, reference);
    }

    private static ASTGuildArtifact parse(String filePath){
        try{
            GuildSymbolTableCreator symbolTableCreator = createSymbolTableCreator();

            GuildParser parser = new GuildParser();
            Optional<ASTGuildArtifact> config = parser.parse(filePath);
            if(parser.hasErrors())
                throw new IllegalArgumentException("The parser encountered errors while parsing "+filePath);
            if(!config.isPresent())
                throw new IllegalArgumentException("The guild configuration file couldn't be parsed");

            ASTGuildArtifact ast = config.get();
            symbolTableCreator.createFromAST(ast);

            return ast;
        }catch(IOException e){
            throw new IllegalArgumentException(e);
        }
    }

    private static GlobalScope createGlobalScope(){
        ModelPath path = new ModelPath(Paths.get(""));
        ModelingLanguage language = new GuildLanguage();
        return new GlobalScope(path, language);
    }

    private static GuildSymbolTableCreator createSymbolTableCreator(){
        GlobalScope globalScope = createGlobalScope();
        ResolvingConfiguration resolvingConfiguration = new ResolvingConfiguration();

        resolvingConfiguration.addDefaultFilters(globalScope.getResolvingFilters());

        return new GuildSymbolTableCreator(resolvingConfiguration, globalScope);
    }
}
