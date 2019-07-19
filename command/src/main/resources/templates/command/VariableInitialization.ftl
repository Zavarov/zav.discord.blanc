${signature("parameters","symbol")}
<#if symbol.requiresGuild()>
        this.config = communicator.config(source.getGuild());
        this.guild = source.getGuild();
        this.member = source.getMember();
        this.channel = source.getTextChannel();
<#else>
        this.channel = source.getChannel();
</#if>
<#list parameters as parameter>
        <#assign var = parameter.getVar()>
        <#assign index = parameter?index>
        <#assign symbol = parameter.getSymbol().getClass().getSimpleName()>
        this.${var}Symbol = parameters
                    .get(${index})
                    .getEnclosingScope()
                    .<${symbol}>resolve("${var}", ${symbol}.KIND)
                    //index + 1 because we start from 0
                    .orElseThrow(() -> new IllegalArgumentException("The ${helper.formatAsOrdinal(index+1)} argument couldn't be resolved."));
</#list>