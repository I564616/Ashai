<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>

<div id="clearCartPopup" class="clear-cart-popup mfp-hide">
	<h2 class="h1 offset-bottom-small"><spring:theme code="cart.clear.cart.title" /></h2>
	<p class="offset-bottom-small"><spring:theme code="cart.clear.cart.confirm" /></p>
	
	<button class="btn btn-primary margin-top-10"><spring:theme code="cart.clear.cart.ok" /></button>
	<span onclick="$.magnificPopup.close()" class="inline"><spring:theme code="cart.clear.cart.cancel" /></span>
	<div class="clearfix"></div>
</div>