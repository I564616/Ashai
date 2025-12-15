<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ attribute name="product" required="true" type="de.hybris.platform.commercefacades.product.data.ProductData"%>
<%@ attribute name="format" required="true" type="java.lang.String"%>
<%@ attribute name="isCart" required="false" type="java.lang.String"%>
<%@ attribute name="fromPage" required="false" type="java.lang.String"%>
<%@ taglib prefix="fn" uri="jakarta.tags.functions"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<c:set value="${ycommerce:productImage(product, format)}" var="primaryImage" />
<c:set value="badge-small" var="badgeCss" />
<c:if test="${fromPage eq 'listing'}">
    <c:set value="badge-med" var="badgeCss" />
</c:if>
<c:choose>
	<c:when test="${not empty primaryImage}">
		<c:choose>
			<c:when test="${not empty primaryImage.altText}">
				<div class="list-item-img">
					<img src="${primaryImage.url}" alt="${fn:escapeXml(primaryImage.altText)}" title="${fn:escapeXml(primaryImage.altText)}" />
					<c:if test="${isCart != 'true'}">
                        <c:choose>
                            <c:when test="${product.dealsFlag eq true}">
                                <div id="dealBadge" class="badge ${badgeCss} badge-postion badge-red" data-toggle="tooltip" data-container="body" data-original-title="<c:forEach items="${product.dealsTitle}" var='deal'>
                                        <p><span class='deal-title'><spring:theme code='text.product.title.deal'/>: </span>${deal}
                                    </p></c:forEach>"><spring:theme code="text.product.title.deal"/>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <c:if test="${product.newProductFlag eq true}">
                                    <div class="badge ${badgeCss} badge-postion badge-green"><spring:theme code="text.product.title.new"/></div>
                                </c:if>
                            </c:otherwise>
                        </c:choose>
					</c:if>
				</div>
			</c:when>
			<c:otherwise>
				<div class="list-item-img">
					<img src="${primaryImage.url}" alt="${fn:escapeXml(product.name)}" title="${fn:escapeXml(product.name)}" />

					<c:if test="${isCart != 'true'}">
					     <c:choose>
                            <c:when test="${product.dealsFlag eq true}">
                                <div id="dealBadge" class="badge ${badgeCss} badge-postion badge-red" data-toggle="tooltip" data-container="body" data-original-title="<c:forEach items="${product.dealsTitle}" var='deal'>
                                   <span class='deal-title'><spring:theme code='text.product.title.deal'/>: </span>${deal}
                                     <br></c:forEach>"><spring:theme code="text.product.title.deal"/>
                                </div>
                            </c:when>

                            <c:otherwise>
                                <c:if test="${product.newProductFlag eq true}">
                                    <div class="badge ${badgeCss} badge-postion badge-green"><spring:theme code="text.product.title.new"/></div>
                                </c:if>
                            </c:otherwise>
                        </c:choose>
					</c:if>
				</div>
			</c:otherwise>
		</c:choose>
	</c:when>
	<c:otherwise>
		<theme:image code="img.missingProductImage.${format}" alt="${fn:escapeXml(product.name)}" title="${fn:escapeXml(product.name)}" />
	</c:otherwise>
</c:choose>