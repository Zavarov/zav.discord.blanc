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

package vartas.discord.bot.command.call._symboltable;

import de.monticore.ast.ASTNode;
import de.monticore.expressions.commonexpressions._ast.ASTNameExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.symboltable.ResolvingConfiguration;
import de.monticore.symboltable.Scope;
import vartas.discord.bot.command.call._ast.ASTCallArtifact;
import vartas.discord.bot.command.command._symboltable.CommandSymbol;
import vartas.discord.bot.command.entity._ast.*;
import vartas.discord.bot.command.parameter._symboltable.*;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static vartas.arithmeticexpressions.calculator.ArithmeticExpressionsValueCalculator.valueOf;

public class CallSymbolTableCreator extends CallSymbolTableCreatorTOP{
    protected int index;
    protected CommandSymbol command;

    @SuppressWarnings("deprecation")
    public CallSymbolTableCreator(ResolvingConfiguration resolvingConfig, Scope enclosingScope) {
        super(resolvingConfig, enclosingScope);
    }

    public void setCommandSymbol(CommandSymbol command){
        this.command = command;
    }

    @Override
    public void handle(ASTCallArtifact node){
        node.setEnclosingScopeOpt(currentScope());

        //The call might have more parameters than what is required by the command
        for(index = 0 ; index < Math.min(node.getParameterList().size(), command.getParameters().size()) ; ++index){
            node.getParameter(index).accept(getRealThis());
        }
    }

    /*================================================================================================================*/
    /*============================================  Discord Entities  ================================================*/
    /*================================================================================================================*/

    @Override
    public void visit(ASTUserType node){
        UserSymbol user = new UserSymbol(command.getParameters().get(index).getVar());
        user.setValue(node.getUser().getId().getValue());
        addToScopeAndLinkWithNode(user, node);

        MemberSymbol member = new MemberSymbol(command.getParameters().get(index).getVar());
        member.setValue(node.getUser().getId().getValue());
        addToScopeAndLinkWithNode(member, node);
    }

    @Override
    public void visit(ASTTextChannelType node){
        TextChannelSymbol symbol = new TextChannelSymbol(command.getParameters().get(index).getVar());
        symbol.setValue(node.getTextChannel().getId().getValue());
        addToScopeAndLinkWithNode(symbol, node);
    }

    @Override
    public void visit(ASTRoleType node){
        RoleSymbol symbol = new RoleSymbol(command.getParameters().get(index).getVar());
        symbol.setValue(node.getRole().getId().getValue());
        addToScopeAndLinkWithNode(symbol, node);
    }

    /*================================================================================================================*/
    /*=================================================  Numbers  ====================================================*/
    /*================================================================================================================*/

    @Override
    public void visit(ASTExpressionType node){
        String var = command.getParameters().get(index).getVar();

        visit(var, () -> valueOf(node.getExpression()), node);
    }

    @Override
    public void visit(ASTDateType node){
        String var = command.getParameters().get(index).getVar();

        ASTExpression day = node.getDay();
        ASTExpression month = node.getMonth();
        ASTExpression year = node.getYear();

        DateSymbol date = new DateSymbol(var);
        date.setValue(day, month, year);
        addToScopeAndLinkWithNode(date, node);

        //value = day-month-year
        visit(var, () -> valueOf(day).subtract(valueOf(month)).subtract(valueOf(year)), node);
    }

    private void visit(String var, Supplier<BigDecimal> value, ASTNode node){
        ExpressionSymbol expression = new ExpressionSymbol(var);
        expression.setValue(value);
        addToScopeAndLinkWithNode(expression, node);

        MessageSymbol message = new MessageSymbol(var);
        message.setValue(value);
        addToScopeAndLinkWithNode(message, node);

        visitGuild(var, guild -> guild.setValue(value), node);
        visitMember(var, guild -> guild.setValue(value), node);
        visitUser(var, guild -> guild.setValue(value), node);
        visitRole(var, guild -> guild.setValue(value), node);
        visitTextChannel(var, guild -> guild.setValue(value), node);
    }

    /*================================================================================================================*/
    /*=================================================  Strings  ====================================================*/
    /*================================================================================================================*/
    @Override
    public void visit(ASTIntervalType node){
        String var = command.getParameters().get(index).getVar();
        String value = node.getInterval().getName();

        IntervalSymbol interval = new IntervalSymbol(var);
        interval.setValue(node.getInterval().getIntervalType());
        addToScopeAndLinkWithNode(interval, node);

        visit(var, value, node);
    }

    @Override
    public void visit(ASTOnlineStatusType node){
        String var = command.getParameters().get(index).getVar();
        String value = node.getOnlineStatus().getName();

        OnlineStatusSymbol status = new OnlineStatusSymbol(var);
        status.setValue(node.getOnlineStatus().getOnlineStatusType());
        addToScopeAndLinkWithNode(status, node);

        visit(var, value, node);
    }

    @Override
    public void visit(ASTStringType node){
        String var = command.getParameters().get(index).getVar();
        String value = node.getStringLiteral().getValue();

        visit(var, value, node);
    }

    @Override
    public void visit(ASTNameExpression node){
        String var = command.getParameters().get(index).getVar();
        String value = node.getName();

        visit(var, value, node);
    }

    private void visit(String var, String value, ASTNode node){
        StringSymbol string = new StringSymbol(var);
        string.setValue(value);
        addToScopeAndLinkWithNode(string, node);

        visitGuild(var, guild -> guild.setValue(value), node);
        visitMember(var, guild -> guild.setValue(value), node);
        visitUser(var, guild -> guild.setValue(value), node);
        visitRole(var, guild -> guild.setValue(value), node);
        visitTextChannel(var, guild -> guild.setValue(value), node);
    }

    private void visitGuild(String var, Consumer<GuildSymbol> setValue, ASTNode node){
        Optional<GuildSymbol> guild = node.getEnclosingScope().resolve(var, GuildSymbol.KIND);
        if(guild.isPresent()){
            GuildSymbol symbol = guild.get();
            setValue.accept(symbol);
        }else{
            GuildSymbol symbol = new GuildSymbol(var);
            setValue.accept(symbol);
            addToScopeAndLinkWithNode(symbol, node);
        }
    }

    private void visitMember(String var, Consumer<MemberSymbol> setValue, ASTNode node){
        Optional<MemberSymbol> member = node.getEnclosingScope().resolve(var, MemberSymbol.KIND);
        if(member.isPresent()){
            MemberSymbol symbol = member.get();
            setValue.accept(symbol);
        }else{
            MemberSymbol symbol = new MemberSymbol(var);
            setValue.accept(symbol);
            addToScopeAndLinkWithNode(symbol, node);
        }
    }

    private void visitUser(String var, Consumer<UserSymbol> setValue, ASTNode node){
        Optional<UserSymbol> user = node.getEnclosingScope().resolve(var, UserSymbol.KIND);
        if(user.isPresent()){
            UserSymbol symbol = user.get();
            setValue.accept(symbol);
        }else{
            UserSymbol symbol = new UserSymbol(var);
            setValue.accept(symbol);
            addToScopeAndLinkWithNode(symbol, node);
        }
    }

    private void visitTextChannel(String var, Consumer<TextChannelSymbol> setValue, ASTNode node){
        Optional<TextChannelSymbol> channel = node.getEnclosingScope().resolve(var, TextChannelSymbol.KIND);
        if(channel.isPresent()){
            TextChannelSymbol symbol = channel.get();
            setValue.accept(symbol);
        }else{
            TextChannelSymbol symbol = new TextChannelSymbol(var);
            setValue.accept(symbol);
            addToScopeAndLinkWithNode(symbol, node);
        }
    }

    private void visitRole(String var, Consumer<RoleSymbol> setValue, ASTNode node){
        Optional<RoleSymbol> role = node.getEnclosingScope().resolve(var, RoleSymbol.KIND);
        if(role.isPresent()){
            RoleSymbol symbol = role.get();
            setValue.accept(symbol);
        }else{
            RoleSymbol symbol = new RoleSymbol(var);
            setValue.accept(symbol);
            addToScopeAndLinkWithNode(symbol, node);
        }
    }
}
