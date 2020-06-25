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
import de.monticore.cd.cd4analysis._symboltable.CDDefinitionSymbol;
import de.monticore.cd.cd4code.CD4CodePrettyPrinterDelegator;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.io.paths.IterablePath;
import de.monticore.io.paths.ModelPath;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.prettyprint.MCFullGenericTypesPrettyPrinter;
import vartas.discord.blanc.command._ast.ASTCommandArtifact;
import vartas.discord.blanc.command.creator.CommandBuilderCreator;
import vartas.monticore.cd4analysis.CDGeneratorHelper;
import vartas.monticore.cd4analysis.CDTemplateGenerator;
import vartas.monticore.cd4analysis._symboltable.CD4CodeGlobalScope;
import vartas.monticore.cd4analysis._symboltable.CD4CodeLanguage;
import vartas.monticore.cd4analysis.template.CDBindImportTemplate;
import vartas.monticore.cd4analysis.template.CDBindPackageTemplate;
import vartas.monticore.cd4analysis.template.CDHandwrittenFileTemplate;
import vartas.monticore.cd4analysis.template.CDInitializerTemplate;

import javax.annotation.Nonnull;
import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandBuilderGenerator {
    private static Path MODELS_PATH;
    private static Path TEMPLATES_PATH;
    private static Path SOURCES_PATH;
    private static Path OUTPUT_PATH;
    private static List<ASTCommandArtifact> MODELS;

    private static CD4CodeGlobalScope GLOBAL_SCOPE;
    private static GlobalExtensionManagement GLEX;
    private static GeneratorSetup GENERATOR_SETUP;
    private static CDGeneratorHelper GENERATOR_HELPER;
    private static CDTemplateGenerator GENERATOR;


    public static void generate(
            @Nonnull Path modelsPath,
            @Nonnull Path templatesPath,
            @Nonnull Path sourcesPath,
            @Nonnull Path outputPath,
            @Nonnull List<ASTCommandArtifact> commands
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
            @Nonnull List<ASTCommandArtifact> commands
    )
    {
        MODELS_PATH = modelsPath;
        TEMPLATES_PATH = templatesPath;
        SOURCES_PATH = sourcesPath;
        OUTPUT_PATH = outputPath;
        MODELS = commands;
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
        GENERATOR = new CDTemplateGenerator(
                GENERATOR_SETUP,
                GENERATOR_HELPER,
                Arrays.asList(
                        new CDBindPackageTemplate(GLEX),
                        new CDBindImportTemplate(GLEX),
                        new CDInitializerTemplate(GLEX),
                        new CDHandwrittenFileTemplate(GLEX, GENERATOR_HELPER)
                )
        );
    }

    private static void generate(){
        assert GLOBAL_SCOPE != null;
        assert GLEX != null;
        assert MODELS != null;
        assert GENERATOR != null;

        //Generate class diagram
        CommandBuilderCreator transformer = new CommandBuilderCreator(GLEX, GLOBAL_SCOPE);
        ASTCDCompilationUnit ast = transformer.decorate(MODELS);

        CDDefinitionSymbol cdDefinitionSymbol = ast.getCDDefinition().getSymbol();

        GENERATOR.generate(cdDefinitionSymbol);
    }
}
