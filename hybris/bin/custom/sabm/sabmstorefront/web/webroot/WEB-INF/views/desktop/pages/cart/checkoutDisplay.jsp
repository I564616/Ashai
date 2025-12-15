<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="cart" tagdir="/WEB-INF/tags/desktop/cart" %>
<div class="col-md-12">
	<div class="row">
		<div class="col-md-6"></div>
		<div class="col-md-6 cart-totals cart-buttons-bottom">
				<div class="col-xs-12 col-sm-7 trim-left-lg trim-right-5-lg">
					<a class="inline clearCart regular-popup" href="#clearCartPopup"><spring:theme code="cart.page.clear.cart"/></a>
					<a class="inline" href="${continueShoppingUrl}"><spring:theme text="Continue Shopping" code="cart.page.continue"/></a>
				</div>
				<div class="col-xs-12 col-sm-5 trim-left-5-lg trim-right-lg text-right">
					<cart:checkoutButtons />
				</div>
		</div>
	</div>
</div>