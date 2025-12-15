<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="breadcrumbs" required="true" type="java.util.List" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<c:url value="/" var="homeUrl"/>
	<div class="container container-breadcrumbs">
		<ul class="breadcrumb visible-sm-block visible-md-block visible-lg-block">
			<c:forEach items="${breadcrumbs}" var="breadcrumb" varStatus="status">
				<li <c:if test="${not empty breadcrumb.linkClass}">class="${breadcrumb.linkClass}"</c:if>>
					<c:choose>
						<c:when test="${breadcrumb.url eq '#'}">
							<c:if test="${status.last}"><font class="last">${fn:escapeXml(breadcrumb.name)}</font></c:if>
						</c:when>
						<c:otherwise>
							<c:url value="${breadcrumb.url}" var="breadcrumbUrl"/>
							<a href="${breadcrumbUrl}" <c:if test="${status.last}">class="last"</c:if>>${fn:escapeXml(breadcrumb.name)}</a>
						</c:otherwise>
					</c:choose>
				</li>
			</c:forEach>
		</ul>
		<div class="breadcrumb visible-xs-block">
			<c:url value="${backUrl}" var="mobileBackUrl"/>
			<a href="${mobileBackUrl}"><spring:theme code="breadcrumb.go.back" text="Go back"/></a>
		</div>
</div>
