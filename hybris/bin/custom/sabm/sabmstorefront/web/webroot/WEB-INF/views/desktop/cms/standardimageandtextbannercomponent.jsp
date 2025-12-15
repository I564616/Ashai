<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format"%>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/desktop/product"%>
<%@ taglib prefix="component" tagdir="/WEB-INF/tags/shared/component"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<c:url value="${textComponent.link}" var="encodedUrl" />
<div class="image-text-banner">
	<c:choose>
		<c:when test="${empty encodedUrl || encodedUrl eq '#'}">
		<c:if test="${not empty media && not empty media.url}">
			<div class="banner-image banner-top banner-image-desktop">
				<img src="${media.url}" alt="${media.altText}" title="${media.altText}"/>${textComponent.content }
			</div>
		</c:if>
		<c:if test="${not empty mediaForMobile && not empty mediaForMobile.url}">
			<div class="banner-image banner-top banner-image-mobile">
				<img src="${mediaForMobile.url}" alt="${mediaForMobile.altText}" title="${mediaForMobile.altText}"/>${textComponent.content }
			</div>
		</c:if>
		</c:when>
		<c:otherwise>
		<c:if test="${not empty media && not empty media.url}">
			<div class="banner-image banner-top banner-image-desktop">
				<a tabindex="-1" href="${encodedUrl}"><img src="${media.url}" alt="${media.altText}" title="${media.altText}"/>${textComponent.content }</a>
			</div>
		</c:if>
		<c:if test="${not empty mediaForMobile && not empty mediaForMobile.url}">
			<div class="banner-image banner-top banner-image-mobile">
				<a tabindex="-1" href="${encodedUrl}"><img src="${mediaForMobile.url}" alt="${mediaForMobile.altText}" title="${mediaForMobile.altText}"/>${textComponent.content }</a>
			</div>
		</c:if>
		</c:otherwise>
	</c:choose>
</div>


