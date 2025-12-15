<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<%-- AddOn JavaScript files --%>
<c:forEach items="${addOnJavaScriptPaths}" var="addOnJavaScript">
    <script type="text/javascript" src="${addOnJavaScript}"></script>
</c:forEach>

<c:choose>
	<c:when test="${useMinified}">
		<script type="text/javascript" src="${staticHostPath}${siteStaticsContextPath}/js/scripts.min.js?${staticsVersion}"></script>
	</c:when>
	<c:otherwise>
		<script type="text/javascript" src="${staticHostPath}${siteStaticsContextPath}/js/libs.js?${staticsVersion}"></script>
		<script type="text/javascript" src="${staticHostPath}${siteStaticsContextPath}/js/plugins.js?${staticsVersion}"></script>
		<script type="text/javascript" src="${staticHostPath}${siteStaticsContextPath}/js/src.js?${staticsVersion}"></script>
		<script type="text/javascript" src="${staticHostPath}${siteStaticsContextPath}/js/main.js?${staticsVersion}"></script>
	</c:otherwise>
</c:choose>

<script type="text/javascript" src="${staticHostPath}${siteStaticsContextPath}/js/app.js?${staticsVersion}" defer></script>

<%-- Cms Action JavaScript files --%>
<c:forEach items="${cmsActionsJsFiles}" var="actionJsFile">
	<c:if test="${actionJsFile != 'vieworderaction.js' }">
    	<script type="text/javascript" src="${staticHostPath}${commonResourcePath}/js/cms/${actionJsFile}"></script>
    </c:if>
</c:forEach>