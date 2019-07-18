package vartas.discord.bot.command.command._cocos;

import de.se_rwth.commons.logging.Log;
import vartas.discord.bot.command.command._ast.ASTAttribute;
import vartas.discord.bot.command.command._ast.ASTClassName;
import vartas.discord.bot.command.command._visitor.CommandVisitor;

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
public class ClassNameStartsWithCapitalLetterCoCo implements CommandASTAttributeCoCo, CommandVisitor {
    public static final String ERROR_MESSAGE = "The class name of the command needs to start with a capital letter.";
    @Override
    public void check(ASTAttribute node) {
        node.accept(getRealThis());
    }

    @Override
    public void visit(ASTClassName node){
        if(node.getValue().length() > 0 && Character.isLowerCase(node.getValue().charAt(0)))
            Log.error(ERROR_MESSAGE);
    }
}
