<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="entry" required="true" type="de.hybris.platform.commercefacades.order.data.OrderEntryData"%>
<%@ attribute name="count" required="true" type="java.lang.Integer"%>

<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format" %>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/desktop/product" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="cart" tagdir="/WEB-INF/tags/desktop/cart"%>

<c:url value="${entry.product.url}" var="productUrl"/>
<div class="cartRow clearfix">
	<div class="col-md-5">
		<div class="row">
			<div class="col-md-3 visible-md-block visible-lg-block cart-img">
				<a href="${productUrl}" class="js-track-product-link"
						data-currencycode="${entry.basePrice.currencyIso}"
						data-name="${fn:escapeXml(entry.product.name)}"
						data-id="${entry.product.code}"
						data-price="${entry.basePrice.value}"
						data-quantity="${entry.quantity}"
						data-brand="${fn:escapeXml(entry.product.brand)}"
						data-category="${fn:escapeXml(entry.product.categories[0].name)}"
						data-variant="${entry.unit.name}"
						data-position="${count}"
						data-url="${entry.product.url}"
						data-sku="${entry.product.code}"
						data-coupon="${entry.product.dealsFlag}"
						data-actionfield="${fn:escapeXml(requestOrigin)}">
					<product:productPrimaryImage product="${entry.product}" format="thumbnail" isCart="true"/>
				</a>
			</div>
			<div class="col-xs-7 col-sm-9 trim-left-5-lg cart-name">
				<div class="itemName">
					<a href="${productUrl}">
						<h4 class="checkoutClamp-2 offset-bottom-none">${entry.product.name}</h4>
						<h4 class="checkoutClamp-2">${entry.product.packConfiguration}</h4>
					</a>
				</div>
				<div class="visible-md-block visible-lg-block">
					<c:forEach items="${entry.dealTitle}" var="dealTitle" varStatus="dealsloop">
					<span class="bold text-blue">Deal&nbsp;</span><span class="deal-index">${dealTitle.dealSeqNo}</span>
				</c:forEach><span></span>

			</div>
		</div>
		<div class="col-xs-5 col-sm-3 visible-xs-block visible-sm-block text-right">
			<format:price priceData="${entry.totalPrice}" displayFreeForZero="${entry.isFreeGood}" />
			<c:if test="${entry.totalDiscountAmount.value > 0}">
			<br/><spring:theme code="basket.page.saving" />&nbsp;<format:price priceData="${entry.totalDiscountAmount}" />
		</c:if>
	</div>							
</div>
</div>
<div class="col-md-7">
	<div class="row">
		<div class="col-xs-12 col-md-3 visible-md-block visible-lg-block cart-itemPrice text-right">
			<div class="h4">
				<format:price priceData="${entry.basePrice}" displayFreeForZero="${entry.isFreeGood}" />
			</div>
			<c:if test="${entry.unitDiscountAmount.value > 0}">
			<spring:theme code="basket.page.save" />&nbsp;<format:price priceData="${entry.unitDiscountAmount}" />
		</c:if>
	</div>

	<div class="col-md-9">
		<div class="row">
			<div class="col-xs-6 col-md-4 trim-right-5-lg cart-qty addtocart-qty">
				<span class="visible-xs-block visible-sm-block">
					<c:if test="${not empty entry.dealTitle }">
					<span id="js-see-deal" class="see-deal-link inline"><span class="view-deal">View</span><span class="hide-deal">Hide</span> deal</span>
				</c:if>
			</span>

			<div class="visible-md-block visible-lg-block">
				<span class="base-quantity">${entry.quantity}&nbsp;</span>
			</div>
		</div>
		<div class="col-xs-6 col-md-4 trim-left-5-lg trim-right-5-lg">
			<div class="hidden-xs hidden-sm text-center">
				${entry.quantity > 1 ? entry.unit.pluralName : entry.unit.name}
			</div>
			<div class="hidden-md hidden-lg text-right">
				<span class="base-quantity">${entry.quantity}&nbsp;${entry.quantity > 1 ? entry.unit.pluralName : entry.unit.name}</span>
			</div>
		</div>

		<div class="col-xs-12 col-md-4 cart-total text-right margin-top-10-xs">
			<div class="visible-md-block visible-lg-block">
				<format:price priceData="${entry.totalPrice}" displayFreeForZero="${entry.isFreeGood}" />
				<c:if test="${entry.totalDiscountAmount.value > 0}">
				<span class="text-normal">
					<spring:theme code="basket.page.saving" />&nbsp;<format:price priceData="${entry.totalDiscountAmount}" />
				</span>
			</c:if>
		</div>
		<div class="visible-xs-block visible-sm-block">
			<div class="visible-xs-block visible-sm-block">
				<cart:cartItemDealTitle entry="${entry}"/>
			</div>
		</div>
	</div>
</div>
</div>
</div>
</div>
</div>