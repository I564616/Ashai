<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="compressible" tagdir="/WEB-INF/tags/responsive/template/compressible" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="cms" tagdir="/WEB-INF/tags/responsive/template/cms" %>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template" %>

<c:url value="/" var="siteRootUrl"/>

<template:javaScriptVariables/>

<%-- <c:choose>
	<c:when test="${granuleEnabled}">
			<compressible:js/>
	</c:when>
	<c:otherwise>
		<compressible:js/>
	</c:otherwise>
</c:choose> --%>

<c:choose>
	<c:when test="${wro4jEnabled}">
	  	<script type="text/javascript" src="${contextPath}/wro/all_responsive.js"></script>
	  	<script type="text/javascript" src="${contextPath}/wro/addons_responsive.js"></script>
	</c:when>
	<c:otherwise>
        <%-- jquery --%>
        <script type="text/javascript" src="${commonResourcePath}/js-sga/jquery-3.5.1.min.js"></script>
		<script type="text/javascript" src="${commonResourcePath}/js-sga/jquery-migrate-3.0.0.min.js"></script>
	

        <%-- bootstrap --%>
        <script type="text/javascript" src="${commonResourcePath}/bootstrap/dist/js/bootstrap.min.js"></script>
        <script type="text/javascript" src="${commonResourcePath}/bootstrap/js/affix.js"></script>
        <script type="text/javascript" src="${commonResourcePath}/bootstrap/js/alert.js"></script>
        <script type="text/javascript" src="${commonResourcePath}/bootstrap/js/button.js"></script>
        <script type="text/javascript" src="${commonResourcePath}/bootstrap/js/carousel.js"></script>
        <script type="text/javascript" src="${commonResourcePath}/bootstrap/js/collapse.js"></script>
        <script type="text/javascript" src="${commonResourcePath}/bootstrap/js/dropdown.js"></script>
        <script type="text/javascript" src="${commonResourcePath}/bootstrap/js/modal.js"></script>
        <script type="text/javascript" src="${commonResourcePath}/bootstrap/js/popover.js"></script>
        <script type="text/javascript" src="${commonResourcePath}/bootstrap/js/scrollspy.js"></script>
        <script type="text/javascript" src="${commonResourcePath}/bootstrap/js/tab.js"></script>
        <script type="text/javascript" src="${commonResourcePath}/bootstrap/js/tooltip.js"></script>
        <script type="text/javascript" src="${commonResourcePath}/bootstrap/js/transition.js"></script>

        <%-- plugins --%>
        <script type="text/javascript" src="${commonResourcePath}/js-sga/enquire.min.js"></script>
        <script type="text/javascript" src="${commonResourcePath}/js-sga/Imager.min.js"></script>
        <script type="text/javascript" src="${commonResourcePath}/js-sga/jquery.blockUI-2.66.js"></script>
        <script type="text/javascript" src="${commonResourcePath}/js-sga/spinningloader.js"></script>
        <%-- <script type="text/javascript" src="${commonResourcePath}/js-sga/jquery.colorbox-min.js"></script> --%>
        <script type="text/javascript" src="${commonResourcePath}/js-sga/jquery.form.min.js"></script>
        <script type="text/javascript" src="${commonResourcePath}/js-sga/jquery.hoverIntent.js"></script>
        <script type="text/javascript" src="${commonResourcePath}/js-sga/jquery.pstrength.custom-1.2.0.js"></script>
        <script type="text/javascript" src="${commonResourcePath}/js-sga/jquery.syncheight.custom.js"></script>
        <script type="text/javascript" src="${commonResourcePath}/js-sga/jquery.tabs.custom.js"></script>
        <script type="text/javascript" src="${commonResourcePath}/js-sga/jquery-ui-1.11.2.min.js"></script>
        <%-- <script type="text/javascript" src="${commonResourcePath}/js-sga/jquery.zoom.custom.js"></script>
        <script type="text/javascript" src="${commonResourcePath}/js-sga/owl.carousel.custom.js"></script> --%>
        <script type="text/javascript" src="${commonResourcePath}/js-sga/jquery.tmpl-1.0.0pre.min.js"></script>
        <script type="text/javascript" src="${commonResourcePath}/js-sga/jquery.currencies.min.js"></script>
        <script type="text/javascript" src="${commonResourcePath}/js-sga/jquery.waitforimages.min.js"></script>
        <script type="text/javascript" src="${commonResourcePath}/js-sga/jquery.slideviewer.custom.1.2.js"></script>
        <script type="text/javascript" src="${commonResourcePath}/js-sga/waypoints.min.1.1.5.js"></script>
        <script type="text/javascript" src="${commonResourcePath}/js-sga/footable.all.min.js"></script>
        <script type="text/javascript" src="${commonResourcePath}/js-sga/validate.min.js"></script>

        <%-- angular --%>
        <script type="text/javascript" src="${commonResourcePath}/js-sga/angular.min.js"></script>
        <script type="text/javascript" src="${commonResourcePath}/js-sga/angular-animate.min.js"></script>
        <script type="text/javascript" src="${commonResourcePath}/js-sga/angular-sanitize.min.js"></script>
        <script type="text/javascript" src="${commonResourcePath}/js-sga/angular-touch.min.js"></script>
        <script type="text/javascript" src="${commonResourcePath}/js-sga/angular-ui-bootstrap.min.js"></script>

        <%-- Custom SGA ACC JS --%>
        <script type="text/javascript" src="${commonResourcePath}/js-sga/acc.common.js"></script>
        <script type="text/javascript" src="${commonResourcePath}/js-sga/acc.sgalogin.js"></script>
        <script type="text/javascript" src="${commonResourcePath}/js-sga/acc.deals.js"></script>

        <!-- ALB Staff portal -->
        <script type="text/javascript" src="${commonResourcePath}/js-sga/acc.customersearch.js"></script>
        <script type="text/javascript" src="${commonResourcePath}/js-sga/_autoload.js"></script>

        <%-- Cms Action JavaScript files --%>
        <c:forEach items="${cmsActionsJsFiles}" var="actionJsFile">
            <script type="text/javascript" src="${commonResourcePath}/js-sga/cms/${actionJsFile}"></script>
        </c:forEach>

        <%-- AddOn JavaScript files --%>
        <c:forEach items="${addOnJavaScriptPaths}" var="addOnJavaScript">
            <script type="text/javascript" src="${addOnJavaScript}"></script>
        </c:forEach>
	</c:otherwise>
</c:choose>

<cms:previewJS cmsPageRequestContextData="${cmsPageRequestContextData}" />
