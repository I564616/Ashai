<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="compressible" tagdir="/WEB-INF/tags/mobile/template/compressible" %>
<%@ taglib prefix="cms" tagdir="/WEB-INF/tags/mobile/template/cms" %>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/mobile/template" %>

<c:url value="/" var="siteRootUrl"/>

<template:javaScriptVariables />

<c:choose>
	<c:when test="${granuleEnabled}">
			<compressible:js/>
	</c:when>
	<c:otherwise>
		<compressible:js/>
	</c:otherwise>
</c:choose>

<cms:previewJS cmsPageRequestContextData="${cmsPageRequestContextData}" />