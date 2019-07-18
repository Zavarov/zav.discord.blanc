package vartas.discord.bot.command.command._cocos;

import de.se_rwth.commons.logging.Log;
import vartas.discord.bot.command.command._ast.ASTCommand;
import vartas.discord.bot.command.command._visitor.CommandVisitor;
import vartas.discord.bot.command.parameter._ast.ASTMember;

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
public class MemberParameterRequiresGuildCoCo implements CommandASTCommandCoCo, CommandVisitor {
    public static final String ERROR_MESSAGE = "This command must be restricted to a guild if it has a member as a parameter.";
    protected boolean inGuild;

    @Override
    public void check(ASTCommand node) {
        inGuild = node.isGuild();

        node.accept(getRealThis());
    }

    @Override
    public void visit(ASTMember node){
        if(!inGuild)
            Log.error(ERROR_MESSAGE);
    }
}
