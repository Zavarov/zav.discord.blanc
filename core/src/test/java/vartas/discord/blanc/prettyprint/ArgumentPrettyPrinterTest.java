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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import vartas.discord.blanc.mock.ArithmeticArgumentMock;
import vartas.discord.blanc.mock.MentionArgumentMock;
import vartas.discord.blanc.mock.StringArgumentMock;
import vartas.discord.blanc.parser.Argument;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

public class ArgumentPrettyPrinterTest {
    Argument stringArgument;
    Argument mentionArgument;
    Argument arithmeticArgument;

    @BeforeEach
    public void setUp(){
        stringArgument = new StringArgumentMock("Content");
        mentionArgument = new MentionArgumentMock(12345L);
        arithmeticArgument = new ArithmeticArgumentMock(BigDecimal.TEN);
    }

    @Test
    public void testPrintPrettyStringArgument(){
        assertThat(ArgumentPrettyPrinter.printPretty(stringArgument)).isEqualTo("Content");
    }

    @Test
    public void testPrintPrettyMentionArgument(){
        assertThat(ArgumentPrettyPrinter.printPretty(mentionArgument)).isEqualTo("12345");
    }

    @Test
    public void testPrintPrettyArithmeticArgument(){
        assertThat(ArgumentPrettyPrinter.printPretty(arithmeticArgument)).isEqualTo("10");
    }
}
