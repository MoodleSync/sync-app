<#-- template to create the options file for the jpackage tool to create the application image -->
<#if osName?upper_case?contains("WIN")>
	--win-console
<#elseif osName?upper_case?contains("MAC")>

<#else>

</#if>

--name ${appName}
--app-version "${appVersion}"
--vendor "${appVendor}"
--copyright "${appCopyright}"