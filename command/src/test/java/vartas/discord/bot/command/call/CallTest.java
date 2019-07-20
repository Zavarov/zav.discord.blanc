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
import vartas.discord.bot.command.entity._ast.ASTEntityType;
import vartas.discord.bot.command.parameter._ast.ASTDate;
import vartas.discord.bot.command.parameter._ast.ASTGuild;
import vartas.discord.bot.command.parameter._symboltable.*;
import vartas.discord.bot.io.rank.RankType;

import static org.assertj.core.api.Assertions.assertThat;

public class CallTest extends AbstractTest {
    GlobalScope commandScope;
    ASTCommandArtifact command;
    @Before
    public void setUp(){
        commandScope = createGlobalScope();
        command = CommandHelper.parse(commandScope,"src/test/resources/Command.cmd");
    }

    protected ASTCallArtifact parse(String expression){
        return CallHelper.parse(commandScope, expression);
    }

    @Test
    public void testIsInGuild(){
        ASTCallArtifact call = parse("example.test \"guild\" 11-11-2000");
        CommandSymbol symbol = commandScope.<CommandSymbol>resolve(call.getQualifiedName(), CommandSymbol.KIND).get();

        assertThat(symbol.requiresGuild()).isTrue();
    }

    @Test
    public void testParameters(){
        ASTCallArtifact call = parse("example.test \"guild\" 11-11-2000");
        CommandSymbol symbol = commandScope.<CommandSymbol>resolve(call.getQualifiedName(), CommandSymbol.KIND).get();

        assertThat(symbol.getParameters()).hasSize(2);
        assertThat(symbol.getParameters().get(0)).isInstanceOf(ASTGuild.class);
        assertThat(symbol.getParameters().get(1)).isInstanceOf(ASTDate.class);
    }

    @Test
    public void testPermissions(){
        ASTCallArtifact call = parse("example.test \"guild\" 11-11-2000");
        CommandSymbol symbol = commandScope.<CommandSymbol>resolve(call.getQualifiedName(), CommandSymbol.KIND).get();

        assertThat(symbol.getRequiredPermissions()).containsExactlyInAnyOrder(Permission.ADMINISTRATOR, Permission.MESSAGE_MANAGE);
    }

    @Test
    public void testRanks(){
        ASTCallArtifact call = parse("example.test \"guild\" 11-11-2000");
        CommandSymbol symbol = commandScope.<CommandSymbol>resolve(call.getQualifiedName(), CommandSymbol.KIND).get();

        assertThat(symbol.getValidRanks()).containsExactlyInAnyOrder(RankType.ROOT, RankType.DEVELOPER);
    }

    @Test
    public void testResolveDate(){
        ASTCallArtifact call = parse("example.date 11-11-2000");
        CommandSymbol symbol = commandScope.<CommandSymbol>resolve(call.getQualifiedName(), CommandSymbol.KIND).get();

        String var = symbol.getParameters().get(0).getVar();
        ASTEntityType parameter = call.getParameterList().get(0);

        assertThat(parameter.getEnclosingScope().resolve(var, DateSymbol.KIND)).isPresent();
    }

    @Test
    public void testResolveDateAsExpression(){
        ASTCallArtifact call = parse("example.expression 11-11-2000");
        CommandSymbol symbol = commandScope.<CommandSymbol>resolve(call.getQualifiedName(), CommandSymbol.KIND).get();

        String var = symbol.getParameters().get(0).getVar();
        ASTEntityType parameter = call.getParameterList().get(0);

        assertThat(parameter.getEnclosingScope().resolve(var, ExpressionSymbol.KIND)).isPresent();
    }

    @Test
    public void testResolveExpression(){
        ASTCallArtifact call = parse("example.expression 1+2*sin(pi)");
        CommandSymbol symbol = commandScope.<CommandSymbol>resolve(call.getQualifiedName(), CommandSymbol.KIND).get();

        String var = symbol.getParameters().get(0).getVar();
        ASTEntityType parameter = call.getParameterList().get(0);

        assertThat(parameter.getEnclosingScope().resolve(var, ExpressionSymbol.KIND)).isPresent();
    }

    @Test
    public void testResolveGuildByName(){
        ASTCallArtifact call = parse("example.guild \"guild\"");
        CommandSymbol symbol = commandScope.<CommandSymbol>resolve(call.getQualifiedName(), CommandSymbol.KIND).get();

        String var = symbol.getParameters().get(0).getVar();
        ASTEntityType parameter = call.getParameterList().get(0);

        assertThat(parameter.getEnclosingScope().resolve(var, GuildSymbol.KIND)).isPresent();
    }

    @Test
    public void testResolveGuildById(){
        ASTCallArtifact call = parse("example.guild 12345");
        CommandSymbol symbol = commandScope.<CommandSymbol>resolve(call.getQualifiedName(), CommandSymbol.KIND).get();

        String var = symbol.getParameters().get(0).getVar();
        ASTEntityType parameter = call.getParameterList().get(0);

        assertThat(parameter.getEnclosingScope().resolve(var, GuildSymbol.KIND)).isPresent();
    }

    @Test
    public void testResolveOnlineStatus(){
        ASTCallArtifact call = parse("example.onlinestatus status");
        CommandSymbol symbol = commandScope.<CommandSymbol>resolve(call.getQualifiedName(), CommandSymbol.KIND).get();

        String var = symbol.getParameters().get(0).getVar();
        ASTEntityType parameter = call.getParameterList().get(0);

        assertThat(parameter.getEnclosingScope().resolve(var, OnlineStatusSymbol.KIND)).isPresent();
    }

    @Test
    public void testResolveInterval(){
        ASTCallArtifact call = parse("example.interval interval");
        CommandSymbol symbol = commandScope.<CommandSymbol>resolve(call.getQualifiedName(), CommandSymbol.KIND).get();

        String var = symbol.getParameters().get(0).getVar();
        ASTEntityType parameter = call.getParameterList().get(0);

        assertThat(parameter.getEnclosingScope().resolve(var, IntervalSymbol.KIND)).isPresent();
    }

    @Test
    public void testResolveString(){
        ASTCallArtifact call = parse("example.string \"content\"");
        CommandSymbol symbol = commandScope.<CommandSymbol>resolve(call.getQualifiedName(), CommandSymbol.KIND).get();

        String var = symbol.getParameters().get(0).getVar();
        ASTEntityType parameter = call.getParameterList().get(0);

        assertThat(parameter.getEnclosingScope().resolve(var, StringSymbol.KIND)).isPresent();
    }

    @Test
    public void testResolveMessage(){
        ASTCallArtifact call = parse("example.message 12345");
        CommandSymbol symbol = commandScope.<CommandSymbol>resolve(call.getQualifiedName(), CommandSymbol.KIND).get();

        String var = symbol.getParameters().get(0).getVar();
        ASTEntityType parameter = call.getParameterList().get(0);

        assertThat(parameter.getEnclosingScope().resolve(var, MessageSymbol.KIND)).isPresent();
    }

    @Test
    public void testResolveUserById(){
        ASTCallArtifact call = parse("example.user 12345");
        CommandSymbol symbol = commandScope.<CommandSymbol>resolve(call.getQualifiedName(), CommandSymbol.KIND).get();

        String var = symbol.getParameters().get(0).getVar();
        ASTEntityType parameter = call.getParameterList().get(0);

        assertThat(parameter.getEnclosingScope().resolve(var, UserSymbol.KIND)).isPresent();
    }

    @Test
    public void testResolveUserByName(){
        ASTCallArtifact call = parse("example.user \"name\"");
        CommandSymbol symbol = commandScope.<CommandSymbol>resolve(call.getQualifiedName(), CommandSymbol.KIND).get();

        String var = symbol.getParameters().get(0).getVar();
        ASTEntityType parameter = call.getParameterList().get(0);

        assertThat(parameter.getEnclosingScope().resolve(var, UserSymbol.KIND)).isPresent();
    }

    @Test
    public void testResolveUserByMention(){
        ASTCallArtifact call = parse("example.user <@!12345>");
        CommandSymbol symbol = commandScope.<CommandSymbol>resolve(call.getQualifiedName(), CommandSymbol.KIND).get();

        String var = symbol.getParameters().get(0).getVar();
        ASTEntityType parameter = call.getParameterList().get(0);

        assertThat(parameter.getEnclosingScope().resolve(var, UserSymbol.KIND)).isPresent();
    }

    @Test
    public void testResolveTextChannelById(){
        ASTCallArtifact call = parse("example.textchannel 12345");
        CommandSymbol symbol = commandScope.<CommandSymbol>resolve(call.getQualifiedName(), CommandSymbol.KIND).get();

        String var = symbol.getParameters().get(0).getVar();
        ASTEntityType parameter = call.getParameterList().get(0);

        assertThat(parameter.getEnclosingScope().resolve(var, TextChannelSymbol.KIND)).isPresent();
    }

    @Test
    public void testResolveTextChannelByName(){
        ASTCallArtifact call = parse("example.textchannel \"name\"");
        CommandSymbol symbol = commandScope.<CommandSymbol>resolve(call.getQualifiedName(), CommandSymbol.KIND).get();

        String var = symbol.getParameters().get(0).getVar();
        ASTEntityType parameter = call.getParameterList().get(0);

        assertThat(parameter.getEnclosingScope().resolve(var, TextChannelSymbol.KIND)).isPresent();
    }

    @Test
    public void testResolveTextChanelByMention(){
        ASTCallArtifact call = parse("example.textchannel <#12345>");
        CommandSymbol symbol = commandScope.<CommandSymbol>resolve(call.getQualifiedName(), CommandSymbol.KIND).get();

        String var = symbol.getParameters().get(0).getVar();
        ASTEntityType parameter = call.getParameterList().get(0);

        assertThat(parameter.getEnclosingScope().resolve(var, TextChannelSymbol.KIND)).isPresent();
    }

    @Test
    public void testResolveMemberById(){
        ASTCallArtifact call = parse("example.member 12345");
        CommandSymbol symbol = commandScope.<CommandSymbol>resolve(call.getQualifiedName(), CommandSymbol.KIND).get();

        String var = symbol.getParameters().get(0).getVar();
        ASTEntityType parameter = call.getParameterList().get(0);

        assertThat(parameter.getEnclosingScope().resolve(var, MemberSymbol.KIND)).isPresent();
    }

    @Test
    public void testResolveMemberByName(){
        ASTCallArtifact call = parse("example.member \"name\"");
        CommandSymbol symbol = commandScope.<CommandSymbol>resolve(call.getQualifiedName(), CommandSymbol.KIND).get();

        String var = symbol.getParameters().get(0).getVar();
        ASTEntityType parameter = call.getParameterList().get(0);

        assertThat(parameter.getEnclosingScope().resolve(var, MemberSymbol.KIND)).isPresent();
    }

    @Test
    public void testResolveMemberByMention(){
        ASTCallArtifact call = parse("example.member <@12345>");
        CommandSymbol symbol = commandScope.<CommandSymbol>resolve(call.getQualifiedName(), CommandSymbol.KIND).get();

        String var = symbol.getParameters().get(0).getVar();
        ASTEntityType parameter = call.getParameterList().get(0);

        assertThat(parameter.getEnclosingScope().resolve(var, MemberSymbol.KIND)).isPresent();
    }

    @Test
    public void testResolveRoleById(){
        ASTCallArtifact call = parse("example.role 12345");
        CommandSymbol symbol = commandScope.<CommandSymbol>resolve(call.getQualifiedName(), CommandSymbol.KIND).get();

        String var = symbol.getParameters().get(0).getVar();
        ASTEntityType parameter = call.getParameterList().get(0);

        assertThat(parameter.getEnclosingScope().resolve(var, RoleSymbol.KIND)).isPresent();
    }

    @Test
    public void testResolveRoleByName(){
        ASTCallArtifact call = parse("example.role \"name\"");
        CommandSymbol symbol = commandScope.<CommandSymbol>resolve(call.getQualifiedName(), CommandSymbol.KIND).get();

        String var = symbol.getParameters().get(0).getVar();
        ASTEntityType parameter = call.getParameterList().get(0);

        assertThat(parameter.getEnclosingScope().resolve(var, RoleSymbol.KIND)).isPresent();
    }

    @Test
    public void testResolveRoleByMention(){
        ASTCallArtifact call = parse("example.role <@&12345>");
        CommandSymbol symbol = commandScope.<CommandSymbol>resolve(call.getQualifiedName(), CommandSymbol.KIND).get();

        String var = symbol.getParameters().get(0).getVar();
        ASTEntityType parameter = call.getParameterList().get(0);

        assertThat(parameter.getEnclosingScope().resolve(var, RoleSymbol.KIND)).isPresent();
    }
}
