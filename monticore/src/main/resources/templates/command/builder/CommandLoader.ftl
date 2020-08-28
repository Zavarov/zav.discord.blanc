${signature("commands", "resolver", "command", "factory", "argumentTypes", "requiresGuild")}
        ${commands.getName()}.put("${command}", (_arguments, _flags) -> ${factory}.create(
        <#list argumentTypes as argumentType>
            ${resolver.getName()}.resolve${argumentType}(_arguments.get(${argumentType?index})),
        </#list>
            //The context is provided in the other builder methods.
            null, //Author
            null, //MessageChannel
        <#if requiresGuild>
            null, //Guild
        </#if>
            null, //ServerHookPoint
            null, //Message
            _flags
        ));