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
import vartas.discord.blanc.GuildModule;
import vartas.discord.blanc.mock.StringArgumentMock;
import vartas.discord.blanc.parser.Argument;
import vartas.discord.blanc.parser.GuildModuleResolver;

import static org.assertj.core.api.Assertions.assertThat;

public class GuildModuleResolverTest {
    GuildModuleResolver resolver;
    GuildModule module;
    Argument stringArgument;
    Argument invalidArgument;
    @BeforeEach
    public void setUp(){
        resolver = new GuildModuleResolver();
        module = GuildModule.BLACKLIST;
        stringArgument = new StringArgumentMock(GuildModule.BLACKLIST.name());
        invalidArgument = new StringArgumentMock("junk");
    }
    @Test
    public void testResolve(){
        assertThat(resolver.apply(stringArgument)).contains(module);
        assertThat(resolver.apply(invalidArgument)).isEmpty();
    }
}
