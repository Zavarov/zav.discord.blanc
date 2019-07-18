package vartas.discord.bot.io.rank;

import vartas.discord.bot.io.rank._ast.ASTRankArtifact;
import vartas.discord.bot.io.rank._parser.RankParser;

import java.io.File;
import java.io.IOException;
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
public abstract class RankHelper {

    public static RankConfiguration parse(String filePath, File reference){
        ASTRankArtifact ast = parse(filePath);

        return new RankConfiguration(ast, reference);
    }

    private static ASTRankArtifact parse(String filePath){
        try{
            RankParser parser = new RankParser();
            Optional<ASTRankArtifact> rank = parser.parse(filePath);
            if(parser.hasErrors())
                throw new IllegalArgumentException("The parser encountered errors while parsing "+filePath);
            if(!rank.isPresent())
                throw new IllegalArgumentException("The rank file couldn't be parsed");

            return rank.get();
        }catch(IOException e){
            throw new IllegalArgumentException(e);
        }
    }
}