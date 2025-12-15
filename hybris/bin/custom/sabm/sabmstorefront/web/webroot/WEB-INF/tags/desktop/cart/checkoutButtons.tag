<%@ tag trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<form action="${checkoutUrl}" method="post" class="_sabmcheckoutForm">
	<input type="hidden" class="cartdDeliveryInstructions" name ="cartdDeliveryInstructions" value="${cartData.deliveryInstructions}">
	<input type="hidden" name="CSRFToken" value="${CSRFToken}">
</form>
<button class="checkoutButton continueCheckout btn btn-primary" data-sap-disable="${blockCheckout}" ng-disabled="${blockCheckout}"><spring:theme code="checkout.checkout" /></button>
<button class="recalculateButton doCartBut continueCheckout btn btn-primary cart-recalculate" type="button" data-cart-url="${cartUrl}"><spring:theme code="checkout.recalculate" /></button>