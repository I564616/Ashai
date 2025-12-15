<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="categoryData" required="true" type="de.hybris.platform.commercefacades.product.data.CategoryData"%>
<%@ attribute name="format" required="true" type="java.lang.String" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<c:set value="${ycommerce:categoryImage(categoryData, format)}" var="bannerImage"/>
<c:if test="${not empty bannerImage}">
	<c:choose>
		<c:when test="${not empty bannerImage.altText}">
			<c:set var="imageTitle" value="${fn:escapeXml(bannerImage.altText)}" />
		</c:when>
		<c:otherwise>
			<c:set var="imageTitle" value="${fn:escapeXml(categoryData.name)}" />
		</c:otherwise>
	</c:choose>
	<img src="${bannerImage.url}" alt="${imageTitle}" title="${imageTitle}"/>
</c:if>
