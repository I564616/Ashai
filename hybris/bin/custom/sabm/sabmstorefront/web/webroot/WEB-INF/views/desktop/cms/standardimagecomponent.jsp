<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:url value="${urlLink}" var="encodedUrl" />
<div class="image-well">
	<c:choose>
		<c:when test="${empty encodedUrl || encodedUrl eq '#'}">
			<c:if test="${not empty media && not empty media.url}">
				<div class="banner-image banner-top banner-image-desktop">
				    <img src="${media.url}" title="${media.altText}" alt="${media.altText}" class="img-responsive">
				</div>
			</c:if>
			<c:if test="${not empty mediaForMobile && not empty mediaForMobile.url}">
				<div class="banner-image banner-top banner-image-mobile">
				    <img src="${mediaForMobile.url}" title="${mediaForMobile.altText}" alt="${mediaForMobile.altText}" class="img-responsive"/>
				</div>
			</c:if>
		</c:when>
		<c:otherwise>
			<c:if test="${not empty media && not empty media.url}">
				<div class="banner-image banner-top banner-image-desktop">
				    <a href="${encodedUrl}"><img src="${media.url}" title="${media.altText}" alt="${media.altText}" class="img-responsive"/></a>
				</div>
			</c:if>
			<c:if test="${not empty mediaForMobile && not empty mediaForMobile.url}">
				<div class="banner-image banner-top banner-image-mobile">
				    <a href="${encodedUrl}"><img src="${mediaForMobile.url}" title="${mediaForMobile.altText}" alt="${mediaForMobile.altText}" class="img-responsive"/></a>
				</div>
			</c:if>
		</c:otherwise>
	</c:choose>
</div>