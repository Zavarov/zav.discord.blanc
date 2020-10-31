${signature("commands", "resolver", "command", "factory", "parameters", "requiresGuild")}
<#assign arguments = "$arguments">
<#assign flags = "$flags">
        ${commands.getName()}.put("${command}", (${arguments}, ${flags}) -> ${factory}.create(
        <#list parameters as parameter, parameterName>
            ${
                tc.includeArgs(
                    "command.builder.resolve.Resolve",
                    parameter,
                    [resolver.getName(), parameterName, arguments, parameter?index]
                )
            }<#t>
            ,<#lt>
        </#list>
            //The context is provided in the other builder methods.
            null, //Author
            null, //MessageChannel
        <#if requiresGuild>
            null, //Guild
        </#if>
            null, //ServerHookPoint
            null, //Message
            ${flags}
        ));