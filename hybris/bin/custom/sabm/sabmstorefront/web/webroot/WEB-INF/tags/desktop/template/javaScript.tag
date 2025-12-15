<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="compressible" tagdir="/WEB-INF/tags/desktop/template/compressible" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="cms" tagdir="/WEB-INF/tags/desktop/template/cms" %>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template" %>

<c:url value="/" var="siteRootUrl"/>

<template:javaScriptVariables/>

<c:choose>
	<c:when test="${granuleEnabled}">
			<compressible:js/>
	</c:when>
	<c:otherwise>
		<compressible:js/>
	</c:otherwise>
</c:choose>

<cms:previewJS cmsPageRequestContextData="${cmsPageRequestContextData}" />
