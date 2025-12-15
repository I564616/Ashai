<%@ attribute name="cartData" required="true" type="de.hybris.platform.commercefacades.order.data.CartData" %>
<%@ attribute name="showShipDeliveryEntries" required="false" type="java.lang.Boolean" %>
<%@ attribute name="showPickupDeliveryEntries" required="false" type="java.lang.Boolean" %>
<%@ attribute name="showTax" required="false" type="java.lang.Boolean" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="checkout" tagdir="/WEB-INF/tags/desktop/checkout" %>
<%@ taglib prefix="multi-checkout" tagdir="/WEB-INF/tags/desktop/checkout/multi" %>
<%@ taglib prefix="cart" tagdir="/WEB-INF/tags/desktop/cart" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>



<checkout:checkoutTopOptions />
<div class="row clearfix">
<div class="col-md-4">
	<h2><spring:theme code="basket.page.checkout.confirmedOrder"/></h2>
</div>
<div class="col-md-8 cart-links">
		<ul class="list-inline">
			<li>
				<c:url value="/cart" var="backToCartUrl" scope="request"/>
				<a class="inline" href="${backToCartUrl }" id="backToCart"><spring:theme code="basket.page.checkout.backToCart"/></a>
			</li>
		</ul>
</div>

<div class="col-xs-12">
	<cart:dealTitleList cartData="${cartData}"/>
</div>


<c:if test="${not empty cartData}">
<div class="col-md-12">
	<div id="checkoutOrderDetails">
			
			<multi-checkout:deliveryCartItems cartData="${cartData}"/>

			<c:forEach items="${cartData.pickupOrderGroups}" var="groupData" varStatus="status">
					<multi-checkout:pickupCartItems cartData="${cartData}" groupData="${groupData}" index="${status.index}" showHead="true" />
			</c:forEach>

			<div class="row">
				<div class="col-md-6 checkout-promo hidden">
					<cart:cartPromotions cartData="${cartData}"/>
					<h4><spring:theme code="text.checkout.promotion.applied"/></h4>
					<%-- TODO: Replace the promo code --%>
					<p><spring:theme code="text.checkout.promotion.applied.message" arguments="223345"/></p>
				</div>
				<div class="col-md-6 checkout-promo">
				</div>
				<div class="col-md-6 cart-totals checkout-totals">
					<cart:cartTotals cartData="${cartData}" showTaxEstimate="false" showTax="${showTax}"/>

					<div>
						<div class="col-sm-4 trim-left trim-right-5-lg">
							<a class="doCheckoutBut continueCheckout inline" href="${backToCartUrl}">
								<spring:theme code="basket.page.checkout.backToCart" />
							</a>
						</div>
						<div class="col-sm-8 trim-left-5-lg trim-right text-right">
							<button name="placeOrderBut" class="doCheckoutBut btn btn-primary processButton" <c:if test="${placeOrderDisable}">disabled="disabled"</c:if>
							        <c:if test="${!placeOrderDisable}">ng-disabled="ccForm.$invalid && payBy === 'CREDIT_CARD'"</c:if> ng-click="ccForm.submitted = true"
							        type="button" data-checkout-po-url="${placeOrderPoUrl}"><spring:theme code="basket.page.checkout.placeOrder"/>
							</button>
						</div>
					</div>
				</div>

			</div>
	</div>
</div>
</c:if>

</div>