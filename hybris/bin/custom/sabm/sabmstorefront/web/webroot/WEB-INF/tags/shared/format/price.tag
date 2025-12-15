<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="priceData" required="true" type="de.hybris.platform.commercefacades.product.data.PriceData" %>
<%@ attribute name="displayFreeForZero" required="false" type="java.lang.Boolean" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%--
 Tag to render a currency formatted price.
 Includes the currency symbol for the specific currency.
--%>
<c:choose>
	<c:when test="${displayFreeForZero}">
		<spring:theme code="text.free" text="BONUS"/>
	</c:when>
	<c:when test="${priceData.value > 0}">
		${priceData.formattedValue}
	</c:when>
</c:choose>