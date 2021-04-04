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

package zav.discord.blanc.parser;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import zav.discord.blanc.mock.StringArgumentMock;

import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;

public class ChronoUnitResolverTest {
    ChronoUnitResolver resolver;
    ChronoUnit interval;
    Argument stringArgument;
    Argument invalidArgument;
    @BeforeEach
    public void setUp(){
        resolver = new ChronoUnitResolver();
        interval = ChronoUnit.DAYS;
        stringArgument = new StringArgumentMock(ChronoUnit.DAYS.name());
        invalidArgument = new StringArgumentMock("junk");
    }
    @Test
    public void testResolve(){
        assertThat(resolver.apply(stringArgument)).contains(interval);
        assertThat(resolver.apply(invalidArgument)).isEmpty();
    }
}
