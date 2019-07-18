package vartas.discord.bot.command.command.generator;

import de.monticore.codegen.GeneratorHelper;
import de.monticore.generating.GeneratorEngine;
import de.monticore.io.paths.IterablePath;
import vartas.discord.bot.command.command._ast.ASTCommand;
import vartas.discord.bot.command.command._ast.ASTCommandArtifact;

import java.nio.file.Path;

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
public class CommandGenerator {
    protected CommandGenerator(){}

    public static void generate(ASTCommandArtifact ast, GeneratorEngine generator, IterablePath targetPath){
        String packageName = CommandGeneratorHelper.getPackage(ast);

        ast.getCommandList().forEach(cmd -> generate(cmd, generator, targetPath, packageName));
    }

    public static void generate(ASTCommand ast, GeneratorEngine generator, IterablePath targetPath, String packageName){
        String className = ast.getCommandSymbol().getClassName();

        boolean fileExists = GeneratorHelper.existsHandwrittenClass(className, packageName, targetPath);

        String fileName = GeneratorHelper.getSimpleTypeNameToGenerate(className, packageName, targetPath);

        Path path = CommandGeneratorHelper.getQualifiedPath(packageName, fileName);

        generator.generate("Command", path, ast, packageName, fileExists, fileName);
    }
}
