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

import vartas.discord.blanc.Snowflake;
import vartas.discord.blanc.parser.$visitor.ParserVisitor;
import vartas.discord.blanc.parser.Argument;
import vartas.discord.blanc.parser.ArithmeticArgument;
import vartas.discord.blanc.parser.MentionArgument;
import vartas.discord.blanc.parser.StringArgument;

import javax.annotation.Nonnull;


/**
 * The pretty printer unwraps arguments and transforms their content into a humanly readable format.
 */
@Nonnull
public class ArgumentPrettyPrinter implements ParserVisitor {
    /**
     * The string representation of the {@link Argument}.
     */
    @Nonnull
    private String content = "";

    /**
     * The empty constructor of the pretty printer. Private to encourage the use of {@link #printPretty(Argument)}.
     */
    private ArgumentPrettyPrinter(){}

    /**
     * The {@link String} is calculated by taking the content of the {@link Argument}.
     * @param argument The {@link Argument} that is turned into a {@link String}.
     * @return A {@link String} representation of the provided {@link Argument}.
     */
    public static String printPretty(@Nonnull Argument argument){
        ArgumentPrettyPrinter prettyPrinter = new ArgumentPrettyPrinter();

        argument.accept(prettyPrinter);

        return prettyPrinter.content;
    }

    /**
     * The {@link String} representation of a {@link StringArgument} is simply its value.
     * @param argument The {@link Argument} that is turned into a {@link String}.
     */
    @Override
    public void visit(@Nonnull StringArgument argument){
        content = argument.getContent();
    }

    /**
     * The {@link String} representation of an {@link ArithmeticArgument} is the final result after evaluating the
     * underlying arithmetic expression.
     * @param argument The {@link Argument} that is turned into a {@link String}.
     */
    @Override
    public void visit(@Nonnull ArithmeticArgument argument){
        content = argument.getValue().toString();
    }

    /**
     * The {@link String} representation of an {@link MentionArgument} is the ID of the mentioned {@link Snowflake}.
     * @param argument The {@link Argument} that is turned into a {@link String}.
     */
    @Override
    public void visit(@Nonnull MentionArgument argument){
        content = argument.getNumber().toString();
    }
}
