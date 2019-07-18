${signature("parameters")}
<#list parameters as parameter>
    protected ${parameter.getSymbol().getClass().getSimpleName()} ${parameter.getVar()};
</#list>