<%@ page trimDirectiveWhitespaces="true" contentType="application/json" %>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format" %>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/desktop/product" %>
<%@ taglib prefix="cart" tagdir="/WEB-INF/tags/desktop/cart" %>
<c:url value="/cart/" var="cartUrl"/>
{
"addToCartLayer":"<spring:escapeBody javaScriptEscape="true">
<spring:theme code="text.addToCart" var="addToCartText"/>
<div id="addToCartLayer" class="addToCartPopup">
	<span class="hidden"><spring:theme code="basket.items.added.to.oder"/></span>
	<span class="hidden" id="recommendationsCount">${recommendationsCount}</span>

	<c:set value="${fn:length(modifications)}" var="modificationsLen"/>
	<c:forEach items="${cartData.entries}" var="entry" varStatus="status">
		<c:url value="${entry.product.url}" var="entryProductUrl"/>
		<span class="<c:if test="${modificationsLen eq 0 && pageType ne 'DEAL'}"> js-track-product-addtocartpopup</c:if>"
					<c:if test="${modificationsLen eq 0 && pageType ne 'DEAL'}">
						id="js-track-product-addtocartpopup"
						data-currencycode="${product.price.currencyIso}"
						data-name="${fn:escapeXml(product.name)}"
						data-id="${product.code}"
	                    data-url="${product.url}"
						data-price="${product.price.value}"
						data-brand="${fn:escapeXml(product.brand)}"
						data-category=<c:choose>
				                       		<c:when test="${not empty product.categories}">
				                       			"${fn:escapeXml(product.categories[fn:length(product.categories) - 1].name)}"
				                       		</c:when>
				                       		<c:otherwise>
				                       			""
				                       		</c:otherwise>
				                       </c:choose>
						data-variant=<c:choose>
										<c:when test="${empty product.uomList}">
							 				"${product.unit}"
							 			</c:when>
							 			<c:otherwise>
							 				"${product.uomList[0].name}"
							 			</c:otherwise>
								   </c:choose>
						data-position="${listOriginPos}"
						data-dealsflag="${product.dealsFlag}"
						data-quantity="${quantity}"
						data-actionfield="${fn:escapeXml(requestOrigin)}"</c:if>></span>
	</c:forEach>
	<c:if test="${modificationsLen gt 0}">
	<c:forEach items="${orderData.entries}" var="entry" varStatus="status">
	
		<span class="js-track-order-addtocartpopup" id="js-track-order-addtocartpopup${status.index}"
				data-currencycode="${entry.product.price.currencyIso}"
				data-name="${fn:escapeXml(entry.product.name)}"
				data-id="${entry.product.code}"
				data-sku="${entry.product.code}"
                data-url="${entry.product.url}"
				data-price="${entry.product.price.value}"
				data-brand="${fn:escapeXml(entry.product.brand)}"
				data-category=<c:choose>
		                       		<c:when test="${not empty entry.product.categories}">
		                       			"${fn:escapeXml(entry.product.categories[fn:length(entry.product.categories) - 1].name)}"
		                       		</c:when>
		                       		<c:otherwise>
		                       			""
		                       		</c:otherwise>
		                       </c:choose>
		        <%--
				data-variant=<c:choose>
									<c:when test="${empty entry.product.uomList}">
						 				"${entry.product.unit}"
						 			</c:when>
						 			<c:otherwise>
						 				"${entry.product.uomList[0].name}"
						 			</c:otherwise>
							   </c:choose>--%>
				data-variant="${entry.unit.name}"
				data-position="${status.count}"
				data-dealsflag="${entry.product.dealsFlag}"
				data-quantity="${entry.quantity}"
				data-actionfield="${fn:escapeXml(requestOrigin)}"
				data-isSuggested="false"
				data-isReOrder="true"
				data-isPromotion="${entry.product.dealsFlag}"></span>
	</c:forEach>
	</c:if>
</div>
</spring:escapeBody>",
"totalItemCount":"${cartData.totalUnitCount}",
"excludedError":"<spring:escapeBody javaScriptEscape="true"><c:if test="${fn:length(excludedProductTitles) > 0}"><c:choose><c:when test="${fn:length(excludedProductTitles) > 1}"><spring:theme code="basket.home.order.products.notadded.tocart" arguments="s were;they" htmlEscape="false" argumentSeparator=";"/></c:when><c:otherwise><spring:theme code="basket.home.order.products.notadded.tocart"  arguments=" was;it" htmlEscape="false" argumentSeparator=";"/></c:otherwise></c:choose><br/><br/><c:forEach items="${excludedProductTitles}" var="excludeproductTitle">${excludeproductTitle}<br/></c:forEach></c:if></spring:escapeBody>"
<c:if test="${not empty product.code}">,"maxOrderError": {"id": "${product.code}", "message": "<c:if test="${errorMsg ne ''}"><c:set var="errorMessage" value="${fn:split(errorMsg, ':')}" /><c:out value="${errorMessage[1]}" /></c:if>"}</c:if>
<c:if test="${modificationsLen gt 0}">,"orderTemplateMaxOrderError": [<c:forEach items="${modifications}" var="m" varStatus="loop">
    {
        <c:set value="${fn:split(m.entry.product.url, '/')}" var="arr" />
        "${arr[fn:length(arr) - 1]}": "<c:set var="errorText" value="${fn:split(m.statusCode, ':')}" /><c:out value="${errorText[1]}" />"
    }<c:if test="${modificationsLen - 1 != loop.index}">,</c:if>
</c:forEach>]</c:if>
}