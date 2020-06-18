/*
 * Copyright (c) 2020 Zavarov
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

package vartas.discord.blanc.command;

import de.monticore.cd.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.cd.cd4analysis._ast.ASTCDType;
import de.monticore.cd.cd4analysis._symboltable.CDDefinitionSymbol;
import de.monticore.cd.cd4code.CD4CodePrettyPrinterDelegator;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.io.paths.IterablePath;
import de.monticore.io.paths.ModelPath;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.prettyprint.MCFullGenericTypesPrettyPrinter;
import vartas.discord.blanc.command._ast.ASTCommandArtifact;
import vartas.discord.blanc.command.creator.CommandArtifactCreator;
import vartas.monticore.cd4analysis.CDDecoratorGenerator;
import vartas.monticore.cd4analysis.CDFactoryGenerator;
import vartas.monticore.cd4analysis.CDGeneratorHelper;
import vartas.monticore.cd4analysis._symboltable.CD4CodeGlobalScope;
import vartas.monticore.cd4analysis._symboltable.CD4CodeLanguage;

import javax.annotation.Nonnull;
import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class CommandGenerator {
    private static Path MODELS_PATH;
    private static Path TEMPLATES_PATH;
    private static Path SOURCES_PATH;
    private static Path OUTPUT_PATH;
    private static ASTCommandArtifact MODEL;

    private static CD4CodeGlobalScope GLOBAL_SCOPE;
    private static GlobalExtensionManagement GLEX;
    private static GeneratorSetup GENERATOR_SETUP;
    private static CDGeneratorHelper GENERATOR_HELPER;

    private static CDFactoryGenerator FACTORY_GENERATOR;
    private static CDDecoratorGenerator DECORATOR_GENERATOR;

    public static void generate(
            @Nonnull Path modelsPath,
            @Nonnull Path templatesPath,
            @Nonnull Path sourcesPath,
            @Nonnull Path outputPath,
            @Nonnull ASTCommandArtifact commands
    )
    {
        processArguments(modelsPath, templatesPath, sourcesPath, outputPath, commands);

        buildGlobalScope();
        buildGlex();
        buildGeneratorSetup();
        buildGeneratorHelper();
        buildGenerator();

        generate();
    }

    private static void processArguments(
            @Nonnull Path modelsPath,
            @Nonnull Path templatesPath,
            @Nonnull Path sourcesPath,
            @Nonnull Path outputPath,
            @Nonnull ASTCommandArtifact commands
    )
    {
        MODELS_PATH = modelsPath;
        TEMPLATES_PATH = templatesPath;
        SOURCES_PATH = sourcesPath;
        OUTPUT_PATH = outputPath;
        MODEL = commands;
    }

    private static void buildGlobalScope(){
        assert MODELS_PATH != null;

        CD4CodeLanguage language = new CD4CodeLanguage();
        ModelPath modelPath = new ModelPath();

        if(MODELS_PATH.toFile().exists())
            modelPath.addEntry(MODELS_PATH);

        GLOBAL_SCOPE = new CD4CodeGlobalScope(modelPath, language);
    }

    private static void buildGlex(){
        GLEX = new GlobalExtensionManagement();
        GLEX.setGlobalValue("cdPrinter", new CD4CodePrettyPrinterDelegator());
        GLEX.setGlobalValue("mcPrinter", new MCFullGenericTypesPrettyPrinter(new IndentPrinter()));
    }

    private static void buildGeneratorSetup(){
        assert TEMPLATES_PATH != null;
        assert SOURCES_PATH != null;
        assert OUTPUT_PATH != null;
        assert GLEX != null;

        List<File> templatePaths = new ArrayList<>();
        if(TEMPLATES_PATH.toFile().exists())
            templatePaths.add(TEMPLATES_PATH.toFile());

        GENERATOR_SETUP = new GeneratorSetup();
        GENERATOR_SETUP.setAdditionalTemplatePaths(templatePaths);
        GENERATOR_SETUP.setDefaultFileExtension(CDGeneratorHelper.DEFAULT_FILE_EXTENSION);
        GENERATOR_SETUP.setGlex(GLEX);
        GENERATOR_SETUP.setHandcodedPath(IterablePath.from(SOURCES_PATH.toFile(), CDGeneratorHelper.DEFAULT_FILE_EXTENSION));
        GENERATOR_SETUP.setOutputDirectory(OUTPUT_PATH.toFile());
        GENERATOR_SETUP.setTracing(false);
    }

    private static void buildGeneratorHelper(){
        assert SOURCES_PATH != null;

        GENERATOR_HELPER = new CDGeneratorHelper(SOURCES_PATH);
    }

    private static void buildGenerator(){
        FACTORY_GENERATOR = new CDFactoryGenerator(GENERATOR_SETUP, GENERATOR_HELPER, GLOBAL_SCOPE);
        DECORATOR_GENERATOR = new CDDecoratorGenerator(GENERATOR_SETUP, GENERATOR_HELPER);
    }

    private static void generate(){
        assert GLOBAL_SCOPE != null;
        assert GLEX != null;
        assert MODEL != null;
        assert FACTORY_GENERATOR != null;
        assert DECORATOR_GENERATOR != null;

        //Generate class diagram
        CommandArtifactCreator transformer = new CommandArtifactCreator(GLEX, GLOBAL_SCOPE);
        ASTCDCompilationUnit ast = transformer.decorate(MODEL);

        CDDefinitionSymbol cdDefinitionSymbol = ast.getCDDefinition().getSymbol();
        
        FACTORY_GENERATOR.generate(cdDefinitionSymbol);
        DECORATOR_GENERATOR.generate(cdDefinitionSymbol);
    }
}
