<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="priceData" required="true" type="de.hybris.platform.commercefacades.product.data.PriceData" %>
<%@ attribute name="displayFreeForZero" required="false" type="java.lang.Boolean" %>
<%@ attribute name="displayNegationForDiscount" required="false" type="java.lang.Boolean" %>
<%@ attribute name="showNAIfPriceError" required="false" type="java.lang.Boolean" %>
<%@ attribute name="priceUpdated" required="false" type="java.lang.Boolean" %>
<%@ attribute name="noPriceAvailable" required="false" type="java.lang.Boolean" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions"  %>

<spring:htmlEscape defaultHtmlEscape="true" />

<%--
 Tag to render a currency formatted price.
 Includes the currency symbol for the specific currency.
--%>
<c:set value="${fn:escapeXml(priceData.formattedValue)}" var="formattedPrice"/>
<c:choose>
	<c:when test="${priceError}">
		<c:choose>
			<c:when test="${showNAIfPriceError}">
				NA
			</c:when>
			<c:otherwise>
				${formattedPrice}
			</c:otherwise>
		</c:choose>
	</c:when>
	<c:when test="${noPriceAvailable}">
		<spring:theme code="order.detail.no.price.available"/>
	</c:when>
	<c:when test="${not empty priceUpdated && not priceUpdated}">
		NA
	</c:when>
	<c:when test="${priceData.value > 0}">
		<c:if test="${displayNegationForDiscount}">
			-
		</c:if>
		${formattedPrice}
	</c:when>
	<c:otherwise>
		<c:if test="${displayFreeForZero}">
			<spring:theme code="text.free" text="FREE"/>
		</c:if>
		<c:if test="${not displayFreeForZero}">
			${formattedPrice}
		</c:if>
	</c:otherwise>
</c:choose>