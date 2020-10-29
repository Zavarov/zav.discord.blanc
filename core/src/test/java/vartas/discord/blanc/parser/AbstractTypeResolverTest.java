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

package vartas.discord.blanc.parser;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import vartas.discord.blanc.ConfigurationModule;
import vartas.discord.blanc.mock.AbstractTypeResolverMock;
import vartas.discord.blanc.mock.ArithmeticArgumentMock;
import vartas.discord.blanc.mock.MentionArgumentMock;
import vartas.discord.blanc.mock.StringArgumentMock;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AbstractTypeResolverTest {
    AbstractTypeResolver resolver;

    Argument stringArgument;
    Argument localDateArgument;
    Argument bigDecimalArgument;
    Argument configurationModuleArgument;
    Argument intervalArgument;
    Argument dummyArgument;

    @BeforeEach
    public void setUp(){
        resolver = new AbstractTypeResolverMock();

        stringArgument = new StringArgumentMock("Content");
        localDateArgument = new StringArgumentMock(LocalDate.ofEpochDay(1337).toString());
        bigDecimalArgument = new ArithmeticArgumentMock(BigDecimal.TEN);
        configurationModuleArgument = new StringArgumentMock(ConfigurationModule.BLACKLIST.name());
        intervalArgument = new StringArgumentMock(ChronoUnit.HOURS.name());
        dummyArgument = new MentionArgumentMock(12345L);
    }

    @Test
    public void testResolveString(){
        assertThat(resolver.resolveString(stringArgument)).contains("Content");
        assertThrows(NoSuchElementException.class, () -> resolver.resolveString(dummyArgument));
    }

    @Test
    public void testResolveLocalDate(){
        assertThat(resolver.resolveLocalDate(localDateArgument)).isEqualTo(LocalDate.ofEpochDay(1337));
        assertThrows(NoSuchElementException.class, () -> resolver.resolveLocalDate(dummyArgument));
    }

    @Test
    public void testResolveBigDecimal(){
        assertThat(resolver.resolveBigDecimal(bigDecimalArgument)).isEqualTo(BigDecimal.TEN);
        assertThrows(NoSuchElementException.class, () -> resolver.resolveBigDecimal(dummyArgument));
    }

    @Test
    public void testResolveConfigurationModule(){
        assertThat(resolver.resolveConfigurationModule(configurationModuleArgument)).isEqualTo(ConfigurationModule.BLACKLIST);
        assertThrows(NoSuchElementException.class, () -> resolver.resolveConfigurationModule(dummyArgument));
    }

    @Test
    public void testResolveInterval(){
        assertThat(resolver.resolveChronoUnit(intervalArgument)).isEqualTo(ChronoUnit.HOURS);
        assertThrows(NoSuchElementException.class, () -> resolver.resolveChronoUnit(dummyArgument));
    }
}
