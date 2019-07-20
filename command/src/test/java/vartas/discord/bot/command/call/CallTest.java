/*
 * Copyright (c) 2019 Zavarov
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

package vartas.discord.bot.command.call;

import de.monticore.symboltable.GlobalScope;
import net.dv8tion.jda.core.Permission;
import org.junit.Before;
import org.junit.Test;
import vartas.discord.bot.command.AbstractTest;
import vartas.discord.bot.command.call._ast.ASTCallArtifact;
import vartas.discord.bot.command.command.CommandHelper;
import vartas.discord.bot.command.command._ast.ASTCommandArtifact;
import vartas.discord.bot.command.command._symboltable.CommandSymbol;
import vartas.discord.bot.command.parameter._ast.ASTDate;
import vartas.discord.bot.command.parameter._ast.ASTGuild;
import vartas.discord.bot.io.rank.RankType;

import static org.assertj.core.api.Assertions.assertThat;

public class CallTest extends AbstractTest {
    GlobalScope commandScope;
    ASTCallArtifact call;
    ASTCommandArtifact command;
    CommandSymbol symbol;
    @Before
    public void setUp(){
        commandScope = createGlobalScope();
        command = CommandHelper.parse(commandScope,"src/test/resources/Command.cmd");

        call = CallHelper.parse(commandScope, "example.test \"guild\" 11-11-2000");

        symbol = commandScope.<CommandSymbol>resolve(call.getQualifiedName(), CommandSymbol.KIND).get();
    }

    @Test
    public void testIsInGuild(){
        assertThat(symbol.requiresGuild()).isTrue();
    }

    @Test
    public void testParameters(){
        assertThat(symbol.getParameters()).hasSize(2);
        assertThat(symbol.getParameters().get(0)).isInstanceOf(ASTGuild.class);
        assertThat(symbol.getParameters().get(1)).isInstanceOf(ASTDate.class);
    }

    @Test
    public void testPermissions(){
        assertThat(symbol.getRequiredPermissions()).containsExactlyInAnyOrder(Permission.ADMINISTRATOR, Permission.MESSAGE_MANAGE);
    }

    @Test
    public void testRanks(){
        assertThat(symbol.getValidRanks()).containsExactlyInAnyOrder(RankType.ROOT, RankType.DEVELOPER);
    }
}
