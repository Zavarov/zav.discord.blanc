package vartas.discord.bot.command.command._cocos;

import de.se_rwth.commons.logging.Log;
import vartas.discord.bot.command.command._ast.ASTCommand;
import vartas.discord.bot.command.command._ast.ASTCommandArtifact;
import vartas.discord.bot.command.command._symboltable.CommandSymbol;

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
public class CommandNameIsUniqueCoCo implements CommandASTCommandArtifactCoCo{
    public static final String ERROR_MESSAGE = "All command names have to be unique.";
    @Override
    public void check(ASTCommandArtifact node) {
        long count = node.getCommandList()
                .stream()
                .map(ASTCommand::getCommandSymbol)
                .map(CommandSymbol::getName)
                .distinct()
                .count();
        if(count != node.getCommandList().size())
            Log.error(ERROR_MESSAGE);
    }
}
