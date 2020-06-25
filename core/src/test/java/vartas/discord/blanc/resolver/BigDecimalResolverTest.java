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
import vartas.discord.blanc.mock.ExpressionArgumentMock;
import vartas.discord.blanc.parser.Argument;
import vartas.discord.blanc.parser.BigDecimalResolver;
import vartas.discord.blanc.parser.StringResolver;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

public class BigDecimalResolverTest {
    BigDecimalResolver resolver;
    BigDecimal value;
    Argument numberArgument;
    @BeforeEach
    public void setUp(){
        resolver = new BigDecimalResolver();
        value = BigDecimal.valueOf(12345);
        numberArgument = new ExpressionArgumentMock(value);
    }
    @Test
    public void testResolve(){
        assertThat(resolver.apply(numberArgument)).contains(value);
    }
}
