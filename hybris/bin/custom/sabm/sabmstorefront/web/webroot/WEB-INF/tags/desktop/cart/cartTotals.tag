<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ attribute name="cartData" required="true" type="de.hybris.platform.commercefacades.order.data.CartData"%>
<%@ attribute name="showTaxEstimate" required="false" type="java.lang.Boolean"%>
<%@ attribute name="showTax" required="false" type="java.lang.Boolean"%>
<%@ attribute name="isCart" required="false" type="java.lang.Boolean"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="cart" tagdir="/WEB-INF/tags/desktop/cart"%>
<c:set var="rowCssNoTrim" value="col-xs-8 padding-left-medium" />
<c:set var="rowCssTrim" value="col-xs-8 trim-right-5-lg padding-left-medium" />
<c:set var="rowCssTrimNoPadding" value="col-xs-8 trim-right-5-lg" />
<div id="orderTotals" class="offset-bottom-small">
	<div class="well cart-totals-inner">
		<c:if test="${ycommerce:displaySubTotal(cartData)}">
			<div class="row">
				<div class="col-xs-8">
					<spring:theme code="basket.page.totals.subtotal" />
				</div>
				<div class="col-xs-4">${cartData.netAmount.formattedValue}</div>
			</div>
			<c:if test="${isCart != true}">
			  <c:if test="${cartData.wet.value >0 or cartData.deposit.value >0}">
                <div class = "row col-xs-8">
                	    &nbsp;<spring:theme code="basket.page.totals.includes" />
                </div>
               </c:if>
                <cart:cartTotalsLine cssClass="${rowCssNoTrim}" label="basket.page.totals.wet" priceData="${cartData.wet}"/>
                <cart:cartTotalsLine cssClass="${rowCssNoTrim}" label="basket.page.totals.deposits" priceData="${cartData.deposit}" discount="false"/>
            </c:if>
			<hr>
			<cart:cartTotalsLine cssClass="${rowCssTrimNoPadding}" label="basket.page.totals.delivery" priceData="${cartData.deliveryCost}" discount="false"/>
			<cart:cartTotalsLine cssClass="${rowCssTrimNoPadding}" label="basket.page.totals.loyalty.fee" priceData="${cartData.totalLoyaltyFeePrice}" discount="false"/>
			<cart:cartTotalsLine cssClass="${rowCssTrimNoPadding}" label="basket.page.totals.auto.pay.advantage.discount" priceData="${cartData.autoPayAdvantageDiscount}" discount="true"/>
			<cart:cartTotalsLine cssClass="${rowCssTrimNoPadding}" label="basket.page.totals.auto.pay.advantage.plus.discount" priceData="${cartData.autoPayAdvantagePlusDiscount}" discount="true"/>
			<hr>
		</c:if>
		<cart:cartTotalsLine cssClass="col-xs-8" label="basket.page.totals.totalexgst" priceData="${cartData.subTotal}" forceShow="true" discount="false"/>
		<cart:cartTotalsLine cssClass="${rowCssTrim}" label="basket.page.totals.netTax" priceData="${cartData.gst}" forceShow="true" discount="false"/>
		<hr>
		<div class="row total">
			<div class="col-xs-8 trim-right-5-lg">
				<spring:theme code="basket.page.totals.totalinclgst" />
			</div>
			<div class="col-xs-4 cart-totals-total">
				<format:price priceData="${cartData.totalPrice}" />
			</div>
		</div>
		<c:if test="${cartData.totalDiscounts.value > 0}">
			<div class="row savings">
				<div class="col-xs-8 trim-right-5-lg"><spring:theme code="basket.page.totals.youwillsave" /></div>
				<div class="col-xs-4">${cartData.totalDiscounts.formattedValue}</div>
			</div>
		</c:if>
	</div>
</div>