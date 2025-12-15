<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format"%>
<%@ attribute name="noPriceAvailable" required="false" type="java.lang.Boolean" %>
<%@ attribute name="displayFreeForZero" required="false" type="java.lang.Boolean" %>

<%@ attribute name="orderEntry" required="true"
	type="de.hybris.platform.commercefacades.order.data.OrderEntryData"%>

<%-- if product is multidimensional with different prices, show range, else, show unique price --%>
<c:choose>
	<c:when
		test="${not orderEntry.product.multidimensional or (orderEntry.product.priceRange.minPrice.value eq orderEntry.product.priceRange.maxPrice.value)}">
		<format:price priceData="${orderEntry.discountPrice}" displayFreeForZero="${displayFreeForZero}" noPriceAvailable="${noPriceAvailable}" />
	</c:when>
	<c:otherwise>
		<format:price priceData="${orderEntry.product.priceRange.minPrice}" displayFreeForZero="${displayFreeForZero}" noPriceAvailable="${noPriceAvailable}"/>
                    -
        <format:price priceData="${orderEntry.product.priceRange.maxPrice}" displayFreeForZero="${displayFreeForZero}" noPriceAvailable="${noPriceAvailable}" />
	</c:otherwise>
</c:choose>