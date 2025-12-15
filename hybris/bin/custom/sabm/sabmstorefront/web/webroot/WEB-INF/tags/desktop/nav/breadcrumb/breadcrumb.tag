<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="breadcrumbs" required="true" type="java.util.List" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:url value="/" var="homeUrl"/>
<c:url value="/paSearch" var="paSearchUrl"/>
<ol class="clearfix breadcrumb">	
	<li>
		<a href="${homeUrl}"><span class="hidden-lg hidden-md hidden-sm"><spring:theme code="breadcrumb.return" /></span> <spring:theme code="breadcrumb.home"/></a>
	</li>		

	<c:forEach items="${breadcrumbs}" var="breadcrumb" varStatus="status">
		<li <c:if test="${not empty breadcrumb.linkClass}">class="${breadcrumb.linkClass} hidden-xs"</c:if>>
			<c:choose>
				<c:when test="${breadcrumb.url eq '#'}">
					<span <c:if test="${status.last}">class="last"</c:if>>${breadcrumb.name}</span>
				</c:when>
				<c:otherwise>
					<c:url value="${breadcrumb.url}" var="breadcrumbUrl"/>
					<a href="${breadcrumbUrl}" <c:if test="${status.last}">class="last"</c:if>>${breadcrumb.name}</a>
				</c:otherwise>
			</c:choose>
		</li>
	</c:forEach>
</ol>