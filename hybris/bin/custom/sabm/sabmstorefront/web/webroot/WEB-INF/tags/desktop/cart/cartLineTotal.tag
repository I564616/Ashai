<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format"%>
<%@ attribute name="index" required="true" type="java.lang.Integer"%>
<%@ attribute name="entry" required="true" type="de.hybris.platform.commercefacades.order.data.OrderEntryData"%>
<spring:theme code="text.iconCartRemove" var="iconCartRemove" />
<c:if test="${entry.updateable and entry.isFreeGood}">
<span class="visible-xs-inline-block visible-sm-inline-block base-quantity">${entry.baseQuantity}&nbsp;<span class="visible-md-block visible-lg-block">${entry.baseQuantity > 1 ? entry.baseUnit.pluralName : entry.baseUnit.name}</span></span>
	<span class="pull-right"><format:price priceData="${entry.totalPrice}" displayFreeForZero="${entry.isFreeGood}" /></span>
</c:if>
<%-- <c:if test="${entry.updateable and not entry.isFreeGood and (empty entry.isChange or not entry.isChange)}"> --%>
<c:if test="${entry.updateable and not entry.isFreeGood}">
	<span class="total"><format:price priceData="${entry.totalPrice}" displayFreeForZero="${entry.isFreeGood}" /></span>
</c:if>
<%-- <c:if test="${not empty entry.isChange and entry.isChange}">
	<span class="emdash">
		&mdash;
	</span>
</c:if> --%>
<c:if test="${entry.totalDiscountAmount.value > 0 and not entry.isFreeGood and (empty entry.isChange or not entry.isChange)}">
	<span class="text-normal block">
		<spring:theme code="basket.page.saving" />&nbsp;<format:price priceData="${entry.totalDiscountAmount}" />
	</span>
</c:if>
<div class="h3_price visible-xs-block visible-sm-block">
	<c:if test="${ entry.basePrice.value > 0 && entry.product.basePrice.value > 0}">
		<span><format:price priceData="${entry.product.basePrice}" displayFreeForZero="true" /></span><br/>
		<spring:theme code="basket.page.save" />
		<format:price priceData="${entry.product.savingsPrice }" displayFreeForZero="true" />
	</c:if>
</div>