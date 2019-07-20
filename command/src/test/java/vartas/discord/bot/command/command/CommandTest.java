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

package vartas.discord.bot.command.command;

import de.monticore.symboltable.GlobalScope;
import net.dv8tion.jda.core.Permission;
import org.junit.Before;
import org.junit.Test;
import vartas.discord.bot.command.AbstractTest;
import vartas.discord.bot.command.command._ast.ASTCommandArtifact;
import vartas.discord.bot.command.command._symboltable.CommandSymbol;
import vartas.discord.bot.command.parameter._ast.ASTDate;
import vartas.discord.bot.command.parameter._ast.ASTGuild;
import vartas.discord.bot.io.rank.RankType;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class CommandTest extends AbstractTest {
    ASTCommandArtifact ast;
    GlobalScope scope;
    CommandSymbol command;
    @Before
    public void setUp(){
        scope = createGlobalScope();
        ast = CommandHelper.parse(scope, "src/test/resources/Command.cmd");
        Optional<CommandSymbol> symbol = scope.resolve("example.test", CommandSymbol.KIND);

        command = symbol.get();
    }

    @Test
    public void testIsInGuild(){
        assertThat(command.requiresGuild()).isTrue();
    }

    @Test
    public void testParameters(){
        assertThat(command.getParameters()).hasSize(2);
        assertThat(command.getParameters().get(0)).isInstanceOf(ASTGuild.class);
        assertThat(command.getParameters().get(1)).isInstanceOf(ASTDate.class);
    }

    @Test
    public void testPermissions(){
        assertThat(command.getRequiredPermissions()).containsExactlyInAnyOrder(Permission.ADMINISTRATOR, Permission.MESSAGE_MANAGE);
    }

    @Test
    public void testRanks(){
        assertThat(command.getValidRanks()).containsExactlyInAnyOrder(RankType.ROOT, RankType.DEVELOPER);
    }

    @Test
    public void testFullName(){
        assertThat(command.getFullName()).isEqualTo("example.test");
    }

    @Test
    public void testClassName(){
        assertThat(command.getClassName()).isEqualTo("TestCommand");
    }
}
