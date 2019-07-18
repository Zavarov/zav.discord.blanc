${signature("asts", "package")}
package ${package};

import net.dv8tion.jda.core.entities.*;
import vartas.discord.bot.api.communicator.*;
import vartas.discord.bot.command.*;
import vartas.discord.bot.command.call._ast.*;
import vartas.discord.bot.command.command._symboltable.*;
import vartas.discord.bot.exec.*;

import java.util.*;
import java.util.function.*;

import static com.google.common.base.Preconditions.checkNotNull;

public class CommandBuilder extends AbstractCommandBuilder{
    public CommandBuilder(){
        super();
<#list asts as ast>
    <#assign commandPackage = helper.getPackage(ast)>
    <#list ast.getCommandList() as command>
        <#assign symbol = command.getCommandSymbol()>
        <#assign name = symbol.getFullName()>
        <#assign className = symbol.getClassName()>
        commands.put("${name}", () -> new ${commandPackage}.${className}(context, communicator, source.getParameterList()));
    </#list>
</#list>
    }
}