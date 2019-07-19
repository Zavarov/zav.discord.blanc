${signature("parameters","symbol")}
<#if symbol.requiresGuild()>
    protected GuildConfiguration config;
    protected Guild guild;
    protected Member member;
    protected TextChannel channel;
<#else>
    protected MessageChannel channel;
</#if>
<#list parameters as parameter>
    protected ${parameter.getSymbol().getClass().getSimpleName()} ${parameter.getVar()}Symbol;
</#list>