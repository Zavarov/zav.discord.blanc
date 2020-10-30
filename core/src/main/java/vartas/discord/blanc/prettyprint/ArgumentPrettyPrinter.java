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

package vartas.discord.blanc.prettyprint;

import vartas.discord.blanc.parser.$visitor.ParserVisitor;
import vartas.discord.blanc.parser.Argument;
import vartas.discord.blanc.parser.ArithmeticArgument;
import vartas.discord.blanc.parser.MentionArgument;
import vartas.discord.blanc.parser.StringArgument;

import javax.annotation.Nonnull;


public class ArgumentPrettyPrinter implements ParserVisitor {
    private String content = "";

    private ArgumentPrettyPrinter(){}

    public static String printPretty(@Nonnull Argument argument){
        ArgumentPrettyPrinter prettyPrinter = new ArgumentPrettyPrinter();

        argument.accept(prettyPrinter);

        return prettyPrinter.content;
    }

    @Override
    public void visit(@Nonnull StringArgument argument){
        content = argument.getContent();
    }

    @Override
    public void visit(@Nonnull ArithmeticArgument argument){
        content = argument.getValue().toString();
    }

    @Override
    public void visit(@Nonnull MentionArgument argument){
        content = argument.getNumber().toString();
    }
}
