${signature("permissions", "ranks")}
<#list permissions as permission>
    ${tc.include("command.validate.CheckPermission", permission)}
</#list>
<#list ranks as rank>
    ${tc.include("command.validate.CheckRank", rank)}
</#list>