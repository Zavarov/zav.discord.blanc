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

package vartas.discord.blanc.command.creator;

import de.monticore.cd.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.cd.cd4analysis._ast.ASTCDCompilationUnitBuilder;
import de.monticore.cd.cd4analysis._ast.ASTCDDefinitionBuilder;
import de.monticore.cd.cd4analysis._ast.CD4AnalysisMill;
import de.monticore.cd.cd4analysis._symboltable.CDDefinitionSymbol;
import de.monticore.cd.cd4analysis._symboltable.CDTypeSymbol;
import de.monticore.cd.cd4analysis._symboltable.CDTypeSymbolLoader;
import de.monticore.cd.cd4code._symboltable.CD4CodeArtifactScope;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.se_rwth.commons.Joiners;
import vartas.discord.blanc.command._ast.ASTCommand;
import vartas.discord.blanc.command._ast.ASTCommandArtifact;
import vartas.discord.blanc.command._visitor.CommandVisitor;
import vartas.monticore.cd4analysis._symboltable.CD4CodeGlobalScope;
import vartas.monticore.cd4analysis._symboltable.CD4CodeSymbolTableCreatorDelegator;

public class CommandArtifactCreator extends AbstractCreator<ASTCommandArtifact, ASTCDCompilationUnit> implements CommandVisitor {
    private ASTCDCompilationUnitBuilder cdCompilationUnitBuilder;
    private ASTCDDefinitionBuilder cdDefinitionBuilder;
    private final CD4CodeGlobalScope globalScope;

    public CommandArtifactCreator(GlobalExtensionManagement glex, CD4CodeGlobalScope globalScope){
        super(glex);
        this.globalScope = globalScope;
    }

    @Override
    public ASTCDCompilationUnit decorate(ASTCommandArtifact ast) {
        cdCompilationUnitBuilder = createCDCompilationUnitBuilder(ast);
        cdDefinitionBuilder = createCDDefinitionBuilder(ast);

        ast.accept(this);

        ASTCDCompilationUnit cdCompilationUnit = cdCompilationUnitBuilder.build();
        //A symbol table is required for generation
        createSymbolTable(ast, cdCompilationUnit);
        importArchitectureClasses(cdCompilationUnit);
        importCommandClasses(cdCompilationUnit);
        return cdCompilationUnit;
    }

    @Override
    public void visit(ASTCommand ast){
        cdDefinitionBuilder.addCDClass(new CommandCreator(glex).decorate(ast));
    }

    @Override
    public void endVisit(ASTCommandArtifact ast){
        cdCompilationUnitBuilder.setCDDefinition(cdDefinitionBuilder.build());
    }

    private ASTCDDefinitionBuilder createCDDefinitionBuilder(ASTCommandArtifact ast){
        return CD4AnalysisMill.cDDefinitionBuilder()
                .setName(ast.isPresentGroup() ? ast.getGroup().getQName() : "");
    }

    private void createSymbolTable(ASTCommandArtifact ast, ASTCDCompilationUnit cdCompilationUnit){
        CDDefinitionSymbol commandSymbol = globalScope.resolveCDDefinition("vartas.discord.blanc.command.Command").orElseThrow();
        CD4CodeSymbolTableCreatorDelegator stc = new CD4CodeSymbolTableCreatorDelegator(globalScope);
        CD4CodeArtifactScope artifactScope = stc.createFromAST(cdCompilationUnit);

        for(CDDefinitionSymbol cdDefinition : artifactScope.getLocalCDDefinitionSymbols()) {
            //Doesn't matter what we name it. As long as it's not a qualified name.
            cdDefinition.getAstNode().setName("CommandArtifact");
            //The super classes are declared in a different scope/class diagram
            for (CDTypeSymbol cdType : cdDefinition.getTypes())
                cdType.setSuperClass(new CDTypeSymbolLoader(cdType.getSuperClass().getName(), commandSymbol.getSpannedScope()));
        }
    }

    private ASTCDCompilationUnitBuilder createCDCompilationUnitBuilder(ASTCommandArtifact ast){
        return CD4AnalysisMill.cDCompilationUnitBuilder()
                .setPackageList(ast.getPackageList())
                .addAllMCImportStatements(ast.getImportList());
    }

    private void importArchitectureClasses(ASTCDCompilationUnit target){
        importClasses(
                globalScope.resolveCDDefinition("vartas.discord.blanc.Architecture").orElseThrow(),
                target.getCDDefinition().getSymbol()
        );
    }

    private void importCommandClasses(ASTCDCompilationUnit target){
        importClasses(
                globalScope.resolveCDDefinition("vartas.discord.blanc.command.Command").orElseThrow(),
                target.getCDDefinition().getSymbol()
        );
    }

    private void importClasses(CDDefinitionSymbol source, CDDefinitionSymbol target){
        for(CDTypeSymbol type : source.getTypes())
            target.addImport(Joiners.DOT.join(source.getPackageName(), source.getName(), type.getName()));
    }
}