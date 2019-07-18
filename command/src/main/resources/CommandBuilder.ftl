${signature("asts", "package")}
package ${package};

import net.dv8tion.jda.core.entities.*;
import vartas.discord.bot.api.communicator.*;
import vartas.discord.bot.command.*;
import vartas.discord.bot.command.call._ast.*;
import vartas.discord.bot.command.command._symboltable.*;

import java.util.*;
import java.util.function.*;

import static com.google.common.base.Preconditions.checkNotNull;

public class CommandBuilder{

    protected ASTCallArtifact source;
    protected CommunicatorInterface communicator;
    protected Message context;

    protected Map<String, Supplier<? extends AbstractCommand>> commands;

    public CommandBuilder(){
        commands = new HashMap<>();
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

    public CommandBuilder setSource(ASTCallArtifact source){
        this.source = source;
        return this;
    }

    public CommandBuilder setCommunicator(CommunicatorInterface communicator){
        this.communicator = communicator;
        return this;
    }

    public CommandBuilder setContext(Message context){
        this.context = context;
        return this;
    }

    public AbstractCommand build() throws IllegalArgumentException, IllegalStateException{
        checkNotNull(source);
        checkNotNull(communicator);
        checkNotNull(context);

        return commands.get(source.getQualifiedName()).get();
    }
}