<%@ tag body-content="scriptless" trimDirectiveWhitespaces="true" %>
<%@ attribute name="pageTitle" required="false" rtexprvalue="true" %>
<%@ attribute name="metaDescription" required="false" %>
<%@ attribute name="metaKeywords" required="false" %>
<%@ attribute name="pageCss" required="false" fragment="true" %>
<%@ attribute name="pageScripts" required="false" fragment="true" %>

<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template" %>
<%@ taglib prefix="addonScripts" tagdir="/WEB-INF/tags/desktop/common/header" %>
<%@ taglib prefix="analytics" tagdir="/WEB-INF/tags/shared/analytics" %>
<%@ taglib prefix="debug" tagdir="/WEB-INF/tags/shared/debug" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="htmlmeta" uri="http://hybris.com/tld/htmlmeta" %>



<!DOCTYPE html>
<html ng-app="CUB" lang="${currentLanguage.isocode}">
<head>
	<title>
		${not empty pageTitle ? pageTitle : not empty cmsPage.title ? cmsPage.title : 'Accelerator Title'}
	</title>


	<%-- Meta Content --%>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<meta http-equiv="X-UA-Compatible" content="IE=Edge" />
	<meta name="format-detection" content="telephone=no" />
	<meta name="viewport" content="width=device-width, initial-scale=1">

	<%-- Additional meta tags --%>
	<htmlmeta:meta items="${metatags}"/>

	<script>
        if(!Map){
            document.documentElement.className+=' notify-mode';
        }
    </script>

	<%-- Favourite Icon --%>
	<spring:theme code="img.favIcon" text="/" var="favIconPath"/>
    <link rel="shortcut icon" type="image/x-icon" media="all" href="${originalContextPath}${favIconPath}" />

	<%-- CSS Files Are Loaded First as they can be downloaded in parallel --%>
	<template:styleSheets/>

	<%-- Inject any additional CSS required by the page --%>
	<jsp:invoke fragment="pageCss"/>
	<analytics:analytics/>
</head>

<body class="bd-portal ${pageBodyCssClasses} ${cmsPageRequestContextData.liveEdit ? ' yCmsLiveEdit' : ''} language-${currentLanguage.isocode}">

	<%-- Inject SVG sprites here --%>
	<template:svg/>

	<noscript> 
		<div class="top-error active noscript">
			<div class="container">
				<p><spring:theme code="system.error.noscript" /></p>
			</div>
		</div> 
	</noscript>
	<div class="top-error browseup">
		<div class="container">
			<p><spring:theme code="system.error.browsehappy" /></p>
		</div> 
	</div>

	<%-- Inject the page body here --%>
	<jsp:doBody/>

	<form name="accessiblityForm">
		<input type="hidden" id="accesibility_refreshScreenReaderBufferField" name="accesibility_refreshScreenReaderBufferField" value=""/>
	</form>
	<div id="ariaStatusMsg" class="skip" role="status" aria-relevant="text" aria-live="polite"></div>

	<%-- Load JavaScript required by the site --%>
	<template:javaScript/>
	
	<%-- Inject any additional JavaScript required by the page --%>
	<jsp:invoke fragment="pageScripts"/>

	<addonScripts:addonScripts/>

</body>

<debug:debugFooter/>

</html>
