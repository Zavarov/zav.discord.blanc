package vartas.discord.bot.io.permission;

import vartas.discord.bot.io.permission._ast.ASTPermissionArtifact;
import vartas.discord.bot.io.permission._parser.PermissionParser;

import java.io.IOException;
import java.io.File;
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
public abstract class PermissionHelper {

    public static PermissionConfiguration parse(String filePath, File reference){
        ASTPermissionArtifact ast = parse(filePath);

        return new PermissionConfiguration(ast, reference);
    }

    private static ASTPermissionArtifact parse(String filePath){
        try{
            PermissionParser parser = new PermissionParser();
            Optional<ASTPermissionArtifact> permission = parser.parse(filePath);
            if(parser.hasErrors())
                throw new IllegalArgumentException("The parser encountered errors while parsing "+filePath);
            if(!permission.isPresent())
                throw new IllegalArgumentException("The permission file couldn't be parsed");

            return permission.get();
        }catch(IOException e){
            throw new IllegalArgumentException(e);
        }
    }
}
