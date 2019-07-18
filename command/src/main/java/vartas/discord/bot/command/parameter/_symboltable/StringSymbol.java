package vartas.discord.bot.command.parameter._symboltable;

import net.dv8tion.jda.core.entities.Message;
import vartas.discord.bot.command.entity._ast.ASTStringType;

import java.util.Optional;

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
public class StringSymbol extends StringSymbolTOP{
    protected ASTStringType ast;

    public StringSymbol(String name) {
        super(name);
    }

    public void setValue(ASTStringType ast){
        this.ast = ast;
    }

    public ASTStringType getValue(){
        return ast;
    }

    public Optional<String> resolve(Message context){
        return Optional.of(ast.getStringLiteral().getValue());
    }
}
