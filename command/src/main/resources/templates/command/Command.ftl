${signature("package", "existsHandwrittenClass", "name")}
<#assign symbol = ast.getCommandSymbol()>
<#assign parameters = symbol.getParameters()>
package ${package};

import vartas.discord.bot.command.*;
import vartas.discord.bot.command.entity._ast.*;
import vartas.discord.bot.command.parameter._symboltable.*;
import vartas.discord.bot.io.rank.*;
import vartas.discord.bot.api.communicator.*;

import net.dv8tion.jda.core.*;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.utils.*;

import java.util.*;

public <#if existsHandwrittenClass>abstract </#if>class ${name} extends AbstractCommand{
    protected Message source;
    protected CommunicatorInterface communicator;

    ${includeArgs("command.VariableDeclaration", parameters)}

    public ${name}(Message source, CommunicatorInterface communicator, List<ASTEntityType> parameters) throws IllegalArgumentException, IllegalStateException
    {
        this.source = source;
        this.communicator = communicator;

        ${includeArgs("command.CheckGuild", symbol)}
        ${includeArgs("command.CheckPermission", symbol)}
        ${includeArgs("command.CheckRank", symbol)}
        ${includeArgs("command.CheckParameter", parameters)}

        ${includeArgs("command.VariableInitialization", parameters)}
    }

<#if !existsHandwrittenClass>
    @Override
    public void run(){}
</#if>
}