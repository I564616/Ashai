<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%--  AddOn Common CSS files --%>
<c:forEach items="${addOnCommonCssPaths}" var="addOnCommonCss">
	<link rel="stylesheet" type="text/css" media="all" href="${addOnCommonCss}"/>
</c:forEach>
<%--  AddOn Theme CSS files --%>
<c:forEach items="${addOnThemeCssPaths}" var="addOnThemeCss">
	<link rel="stylesheet" type="text/css" media="all" href="${addOnThemeCss}"/>
</c:forEach>
<!-- Custom Stylesheets - SAB Miller -->
<!-- <link rel="stylesheet" type="text/css" media="all" href="${staticHostPath}${siteStaticsContextPath}/img/icons/icons.data.svg.css"/> -->
<c:choose>
	<c:when test="${useMinified}">
		<link rel="stylesheet" type="text/css" media="all" href="${staticHostPath}${siteStaticsContextPath}/css/style.min.css?${staticsVersion}"/>
	</c:when>
	<c:otherwise>
		<link rel="stylesheet" type="text/css" media="all" href="${staticHostPath}${siteStaticsContextPath}/css/style.css?${staticsVersion}"/>
	</c:otherwise>
</c:choose>