<%@ tag body-content="scriptless" trimDirectiveWhitespaces="true" %>
<%@ attribute name="pageTitle" required="false" rtexprvalue="true" %>
<%@ attribute name="metaDescription" required="false" %>
<%@ attribute name="metaKeywords" required="false" %>
<%@ attribute name="pageCss" required="false" fragment="true" %>
<%@ attribute name="pageScripts" required="false" fragment="true" %>

<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template" %>
<%@ taglib prefix="analytics" tagdir="/WEB-INF/tags/shared/analytics" %>
<%@ taglib prefix="addonScripts" tagdir="/WEB-INF/tags/responsive/common/header" %>
<%@ taglib prefix="generatedVariables" tagdir="/WEB-INF/tags/shared/variables" %>
<%@ taglib prefix="debug" tagdir="/WEB-INF/tags/shared/debug" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="htmlmeta" uri="http://hybris.com/tld/htmlmeta"%>
<%@ taglib prefix="fn" uri="jakarta.tags.functions"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="livechat" tagdir="/WEB-INF/tags/responsive/common/livechat" %>


<spring:htmlEscape defaultHtmlEscape="true" />

<!DOCTYPE html>
<html lang="${fn:escapeXml(currentLanguage.isocode)}">
<head>
	<title>
		${not empty pageTitle ? pageTitle : not empty cmsPage.title ? fn:escapeXml(cmsPage.title) : 'Accelerator Title'}
	</title>
		
	<link rel="stylesheet" type="text/css" href="https://cloud.typography.com/6397796/6111192/css/fonts.css" />
	<%-- Meta Content --%>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<meta http-equiv="X-UA-Compatible" content="IE=edge">
	<meta charset="utf-8">
	<meta name="viewport" content="width=device-width, initial-scale=1, user-scalable=no">

	<%-- Additional meta tags --%>
	<htmlmeta:meta items="${metatags}"/>

	<%-- Favourite Icon --%>
	<c:if test="${cmsSite.uid eq 'sga'}">
		<link rel="shortcut icon" type="image/x-icon" media="all" href="${originalContextPath}/_ui/responsive/theme-lambda/images/ALB-favicon.ico" />
	</c:if>
	<c:if test="${cmsSite.uid eq 'apb'}">
		<spring:theme code="img.favIcon" text="/" var="favIconPath"/>
    	<link rel="shortcut icon" type="image/x-icon" media="all" href="${originalContextPath}${favIconPath}" />
	</c:if>
	
	<%-- Apple and Android Save to Homescreen Icons --%>
	<c:choose>
		<c:when test="${cmsSite.uid eq 'apb'}">
			<link rel="apple-touch-icon" href="${originalContextPath}/_ui/responsive/theme-lambda/images/apb-apple-touch-icon.png" />
			<link rel="apple-touch-icon" sizes="57x57" href="${originalContextPath}/_ui/responsive/theme-lambda/images/apb-apple-touch-icon-57x57.png" />
			<link rel="apple-touch-icon" sizes="72x72" href="${originalContextPath}/_ui/responsive/theme-lambda/images/apb-apple-touch-icon-72x72.png" />
			<link rel="apple-touch-icon" sizes="76x76" href="${originalContextPath}/_ui/responsive/theme-lambda/images/apb-apple-touch-icon-76x76.png" />
			<link rel="apple-touch-icon" sizes="114x114" href="${originalContextPath}/_ui/responsive/theme-lambda/images/apb-apple-touch-icon-114x114.png" />
			<link rel="apple-touch-icon" sizes="120x120" href="${originalContextPath}/_ui/responsive/theme-lambda/images/apb-apple-touch-icon-120x120.png" />
			<link rel="apple-touch-icon" sizes="144x144" href="${originalContextPath}/_ui/responsive/theme-lambda/images/apb-apple-touch-icon-144x144.png" />
			<link rel="apple-touch-icon" sizes="152x152" href="${originalContextPath}/_ui/responsive/theme-lambda/images/apb-apple-touch-icon-152x152.png" />
			<link rel="apple-touch-icon" sizes="180x180" href="${originalContextPath}/_ui/responsive/theme-lambda/images/apb-apple-touch-icon-180x180.png" />
			<link rel="icon" sizes="192x192" href="${originalContextPath}/_ui/responsive/theme-lambda/images/apb-apple-touch-icon-192x192.png" />
			<link rel="icon" sizes="128x128" href="${originalContextPath}/_ui/responsive/theme-lambda/images/apb-apple-touch-icon-128x128.png" />
		</c:when>
		<c:otherwise>
			<link rel="apple-touch-icon" href="${originalContextPath}/_ui/responsive/theme-lambda/images/apple-touch-icon.png" />
			<link rel="apple-touch-icon" sizes="57x57" href="${originalContextPath}/_ui/responsive/theme-lambda/images/apple-touch-icon-57x57.png" />
			<link rel="apple-touch-icon" sizes="72x72" href="${originalContextPath}/_ui/responsive/theme-lambda/images/apple-touch-icon-72x72.png" />
			<link rel="apple-touch-icon" sizes="76x76" href="${originalContextPath}/_ui/responsive/theme-lambda/images/apple-touch-icon-76x76.png" />
			<link rel="apple-touch-icon" sizes="114x114" href="${originalContextPath}/_ui/responsive/theme-lambda/images/apple-touch-icon-114x114.png" />
			<link rel="apple-touch-icon" sizes="120x120" href="${originalContextPath}/_ui/responsive/theme-lambda/images/apple-touch-icon-120x120.png" />
			<link rel="apple-touch-icon" sizes="144x144" href="${originalContextPath}/_ui/responsive/theme-lambda/images/apple-touch-icon-144x144.png" />
			<link rel="apple-touch-icon" sizes="152x152" href="${originalContextPath}/_ui/responsive/theme-lambda/images/apple-touch-icon-152x152.png" />
			<link rel="apple-touch-icon" sizes="180x180" href="${originalContextPath}/_ui/responsive/theme-lambda/images/apple-touch-icon-180x180.png" />
			<link rel="icon" sizes="192x192" href="${originalContextPath}/_ui/responsive/theme-lambda/images/apple-touch-icon-192x192.png" />
			<link rel="icon" sizes="128x128" href="${originalContextPath}/_ui/responsive/theme-lambda/images/apple-touch-icon-128x128.png" />
		</c:otherwise>
	</c:choose>
		
	<%-- CSS Files Are Loaded First as they can be downloaded in parallel --%>
	<template:styleSheets/>

	<%-- Inject any additional CSS required by the page --%>
	<jsp:invoke fragment="pageCss"/>
	<analytics:analytics/>
	<generatedVariables:generatedVariables/>
</head>

<body class="${pageBodyCssClasses} ${cmsPageRequestContextData.liveEdit ? ' yCmsLiveEdit' : ''} language-${fn:escapeXml(currentLanguage.isocode)}">

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

	<%-- Inject CMS Components from addons using the placeholder slot--%>
	<addonScripts:addonScripts/>
	<%-- Inject Spinner for PLP page--%>
	<%--<div id="showPLPSpinner" class="hide"></div>--%>
	<div id="pageLoadingSpinner" style="display: none;"></div>

    <%-- Livechat code starts--%>
      <livechat:livechat/>

     <script type="text/javascript">
         var scriptSrc = $('#livechatMiawJsFileURL').val();
         var scriptElement = document.createElement('script');
         scriptElement.type = 'text/javascript';
         scriptElement.src = scriptSrc;

        document.head.appendChild(scriptElement);
     </script>

     <%-- Livechat code ends--%>

</body>

<debug:debugFooter/>

</html>
