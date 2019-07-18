package vartas.discord.bot.command;

import com.google.common.base.Preconditions;
import de.monticore.ModelingLanguage;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.io.paths.IterablePath;
import de.monticore.io.paths.ModelPath;
import de.monticore.symboltable.GlobalScope;
import org.apache.commons.io.FileUtils;
import vartas.discord.bot.command.command.CommandHelper;
import vartas.discord.bot.command.command._ast.ASTCommandArtifact;
import vartas.discord.bot.command.command._symboltable.CommandLanguage;
import vartas.discord.bot.command.command.generator.CommandBuilderGenerator;
import vartas.discord.bot.command.command.generator.CommandGenerator;
import vartas.discord.bot.command.command.generator.CommandGeneratorHelper;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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
public class Main {
    private static final File OUTPUT_DIRECTORY = new File("target/generated-test-sources/monticore/sourcecode/").getAbsoluteFile();

    private static final String TEMPLATE_EXTENSION = "ftl";
    private static final File TEMPLATE_DIRECTORY = new File("src/main/resources/command/templates").getAbsoluteFile();

    private static final String TARGET_EXTENSION = "java";
    private static final File TARGET_DIRECTORY = new File("src/main/java").getAbsoluteFile();

    private static final IterablePath TARGET_PATH = IterablePath.from(Collections.singletonList(TARGET_DIRECTORY), TARGET_EXTENSION);
    private static final IterablePath TEMPLATE_PATH = IterablePath.from(Collections.singletonList(TEMPLATE_DIRECTORY), TEMPLATE_EXTENSION);

    private static final GlobalExtensionManagement GLEX = new GlobalExtensionManagement();

    private static final GeneratorSetup SETUP = new GeneratorSetup();

    private static final GeneratorEngine GENERATOR = new GeneratorEngine(SETUP);

    static{
        GLEX.defineGlobalVar("helper", new CommandGeneratorHelper());

        SETUP.setGlex(GLEX);
        SETUP.setOutputDirectory(OUTPUT_DIRECTORY);
        SETUP.setAdditionalTemplatePaths(TEMPLATE_PATH.getPaths().stream().map(Path::toFile).collect(Collectors.toList()));
    }

    public static void main(String[] args){
        Preconditions.checkArgument(args.length > 1, "Please provide the path to the models and a package name for the builder.");
        Preconditions.checkArgument(new File(args[0]).exists(), "Please make sure that the model file exists");

        File modelFolder = new File(args[0]).getAbsoluteFile();
        String packageName = args[1];

        GlobalScope scope = createGlobalScope();

        List<ASTCommandArtifact> models = FileUtils.listFiles(modelFolder, new String[]{ CommandLanguage.COMMAND_FILE_ENDING}, false)
                .stream()
                .map(file -> CommandHelper.parse(scope, file.getPath()))
                .collect(Collectors.toList());

        generateCommands(models, scope);
        generateCommandBuilder(models, scope, packageName);
    }

    private static void generateCommandBuilder(List<ASTCommandArtifact> models, GlobalScope scope, String packageName){
        CommandBuilderGenerator.generate(models, GENERATOR, packageName);
    }


    private static void generateCommands(Collection<ASTCommandArtifact> models, GlobalScope scope){
        models.forEach(model -> generateCommands(model, scope));
    }

    private static void generateCommands(ASTCommandArtifact ast, GlobalScope scope){
        CommandGenerator.generate(ast, GENERATOR, TARGET_PATH);
    }


    private static GlobalScope createGlobalScope(){
        ModelPath path = new ModelPath(Paths.get(""));
        ModelingLanguage language = new CommandLanguage();
        return new GlobalScope(path, language);
    }
}
