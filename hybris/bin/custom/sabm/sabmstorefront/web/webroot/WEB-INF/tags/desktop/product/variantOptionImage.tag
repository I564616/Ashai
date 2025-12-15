<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="variantOptionData" required="true" type="de.hybris.platform.commercefacades.product.data.VariantOptionData"%>
<%@ attribute name="format" required="true" type="java.lang.String" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<c:set value="${ycommerce:variantImage(variantOptionData, format)}" var="variantOptionImage"/>
<div class="list-img-block">
    <c:if test="${not empty variantOptionImage}">
        <c:choose>
            <c:when test="${not empty variantOptionImage.altText}">
                <c:set var="imageTitle" value="${fn:escapeXml(variantOptionImage.altText)}" />
            </c:when>
            <c:otherwise>
                <c:set var="imageTitle" value="${fn:escapeXml(variantOptionData.name)}" />
            </c:otherwise>
        </c:choose>
        <img src="${variantOptionImage.url}" alt="${imageTitle}" title="${imageTitle}"/>
    </c:if>
</div>
