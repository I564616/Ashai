<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<c:url value="${fn:escapeXml(urlLink)}" var="encodedUrl" />
<div class="banner__component banner">
	<c:choose>
		<c:when test="${empty encodedUrl || encodedUrl eq '#'}">
			<img title="${fn:escapeXml(media.altText)}" alt="${fn:escapeXml(media.altText)}"
				src="${media.url}">
		</c:when>
		<c:otherwise>
			<c:choose>
				<c:when test="${not empty cmsPage and cmsPage.uid eq 'multiAccount' }">
				<img title="${fn:escapeXml(media.altText)}"
				alt="${fn:escapeXml(media.altText)}" src="${media.url}">
				</c:when>
				<c:otherwise>
				<a href="${encodedUrl}"><img title="${fn:escapeXml(media.altText)}"
				alt="${fn:escapeXml(media.altText)}" src="${media.url}"></a>
				</c:otherwise>
			</c:choose>
			
		</c:otherwise>
	</c:choose>
</div>