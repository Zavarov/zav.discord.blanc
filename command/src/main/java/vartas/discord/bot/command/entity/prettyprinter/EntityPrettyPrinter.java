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

package vartas.discord.bot.command.entity.prettyprinter;

import de.monticore.MCBasicLiteralsPrettyPrinter;
import de.monticore.expressions.commonexpressions._ast.ASTCommonExpressionsNode;
import de.monticore.expressions.prettyprint2.CommonExpressionsPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.prettyprint.MCBasicsPrettyPrinter;
import vartas.discord.bot.command.entity._ast.*;
import vartas.discord.bot.command.entity._visitor.EntityDelegatorVisitor;
import vartas.discord.bot.command.entity._visitor.EntityVisitor;

public class EntityPrettyPrinter extends EntityDelegatorVisitor{
    IndentPrinter printer;

    public EntityPrettyPrinter(IndentPrinter printer){
        setCommonExpressionsVisitor(new CommonExpressionsPrettyPrinter(printer));
        setMCBasicLiteralsVisitor(new MCBasicLiteralsPrettyPrinter(printer));
        setMCBasicsVisitor(new MCBasicsPrettyPrinter(printer));
        setEntityVisitor(new EntityPrettyPrinterSublanguage(printer));

        this.printer = printer;
    }

    public String prettyprint(ASTEntityNode node){
        printer.clearBuffer();
        node.accept(getRealThis());
        return printer.getContent();
    }

    public String prettyprint(ASTCommonExpressionsNode node){
        printer.clearBuffer();
        node.accept(getRealThis());
        return printer.getContent();
    }

    private static class EntityPrettyPrinterSublanguage implements EntityVisitor{
        protected EntityVisitor realThis;
        protected IndentPrinter printer;

        public EntityPrettyPrinterSublanguage(IndentPrinter printer){
            this.printer = printer;
            this.realThis = this;
        }

        @Override
        public EntityVisitor getRealThis(){
            return realThis;
        }

        @Override
        public void setRealThis(EntityVisitor realThis){
            this.realThis = realThis;
        }

        @Override
        public void handle(ASTDateType node){
            node.getDay().accept(getRealThis());
            printer.print("-");
            node.getMonth().accept(getRealThis());
            printer.print("-");
            node.getYear().accept(getRealThis());
        }

        @Override
        public void handle(ASTUserType node){
            printer.print("<@");
            if(node.isSemicolon())
                printer.print("!");
            printer.print(node.getId());
            printer.print(">");
        }

        @Override
        public void handle(ASTTextChannelType node){
            printer.print("<#");
            printer.print(node.getId());
            printer.print(">");
        }

        @Override
        public void handle(ASTRoleType node){
            printer.print("<@&");
            printer.print(node.getId());
            printer.print(">");
        }
    }
}