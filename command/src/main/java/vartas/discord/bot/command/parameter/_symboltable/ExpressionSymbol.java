package vartas.discord.bot.command.parameter._symboltable;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import vartas.discord.bot.command.entity._ast.ASTExpressionType;

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
public class ExpressionSymbol extends ExpressionSymbolTOP{
    protected ASTExpressionType ast;

    public ExpressionSymbol(String name) {
        super(name);
    }

    public void setValue(ASTExpressionType ast){
        this.ast = ast;
    }

    public ASTExpressionType getValue(){
        return ast;
    }

    public ASTExpression resolve(){
        return ast.getExpression();
    }
}
