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

import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.cd.facade.CDModifier;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateHookPoint;
import vartas.discord.blanc.command.GuildCommand;
import vartas.discord.blanc.command.MessageCommand;
import vartas.discord.blanc.command._ast.*;
import vartas.discord.blanc.command._visitor.CommandVisitor;
import vartas.monticore.cd4analysis.CDGeneratorHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Transforms an instance of an {@link ASTCommand} into an {@link ASTCDClass} for code generation.
 */
public class CommandCreator extends AbstractCreator<ASTCommand, ASTCDClass> implements CommandVisitor {
    private static final String RUN = "public void run();";
    private static final String VALIDATE = "public void validate();";

    private ASTCDClassBuilder cdClassBuilder;
    private final List<ASTRank> ranks = new ArrayList<>();
    private final List<ASTPermission> permissions = new ArrayList<>();
    private boolean requiresAttachment;

    public CommandCreator(GlobalExtensionManagement glex){
        super(glex);
    }

    @Override
    public ASTCDClass decorate(ASTCommand ast) {
        cdClassBuilder = createClassBuilder();
        permissions.clear();
        ranks.clear();
        requiresAttachment = false;

        ast.accept(this);

        ASTCDClass cdClass = cdClassBuilder.build();
        cdClass.addCDMethod(createRunMethod());
        cdClass.addCDMethod(createValidateMethod());

        return cdClass;
    }

    @Override
    public void visit(ASTCommand ast){
        requiresAttachment = ast.containsRestriction(ASTRestriction.ATTACHMENT);

        if(ast.containsRestriction(ASTRestriction.GUILD))
            cdClassBuilder.setSuperclass(getMCTypeFacade().createQualifiedType(GuildCommand.class.getSimpleName()));
        else
            cdClassBuilder.setSuperclass(getMCTypeFacade().createQualifiedType(MessageCommand.class.getSimpleName()));
    }

    @Override
    public void visit(ASTPermission ast){
        permissions.add(ast);
    }

    @Override
    public void visit(ASTRank ast){
        ranks.add(ast);
    }


    @Override
    public void visit(ASTClassAttribute ast){
        cdClassBuilder.setName(ast.getName());
    }

    @Override
    public void visit(ASTParameter ast){
        cdClassBuilder.addCDAttribute(createAttribute(ast));
    }

    private ASTCDClassBuilder createClassBuilder(){
        return CD4AnalysisMill.cDClassBuilder().setModifier(CDModifier.PUBLIC.build());
    }

    private ASTCDAttribute createAttribute(ASTParameter ast){
        return getCDAttributeFacade().createAttribute(CDModifier.PROTECTED, ast.getMCType(), ast.getName());
    }

    private ASTCDMethod createValidateMethod(){
        ASTCDMethod ast = getCDMethodFacade().createMethodByDefinition(VALIDATE);

        glex.replaceTemplate(CDGeneratorHelper.METHOD_HOOK, ast, new TemplateHookPoint("command.Validate", permissions, ranks, requiresAttachment));

        return ast;
    }

    private ASTCDMethod createRunMethod(){
        return getCDMethodFacade().createMethodByDefinition(RUN);
    }
}
