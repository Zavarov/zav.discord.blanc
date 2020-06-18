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

package vartas.discord.blanc.resolver;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import vartas.discord.blanc.mock.MentionArgumentMock;
import vartas.discord.blanc.mock.StringArgumentMock;
import vartas.discord.blanc.parser.Argument;
import vartas.discord.blanc.parser.StringResolver;

import static org.assertj.core.api.Assertions.assertThat;

public class StringResolverTest {
    StringResolver resolver;
    Argument stringArgument;
    Argument numberArgument;
    String string;
    @BeforeEach
    public void setUp(){
        resolver = new StringResolver();
        string = "content";
        stringArgument = new StringArgumentMock(string);
        numberArgument = new MentionArgumentMock(12345);
    }
    @Test
    public void testResolve(){
        assertThat(resolver.apply(stringArgument)).contains(string);
        assertThat(resolver.apply(numberArgument)).isEmpty();
    }
}
