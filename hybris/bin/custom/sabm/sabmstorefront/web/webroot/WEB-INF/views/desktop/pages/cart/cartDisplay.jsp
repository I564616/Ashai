<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="cart" tagdir="/WEB-INF/tags/desktop/cart" %>
<%@ taglib prefix="templatesOrder" tagdir="/WEB-INF/tags/desktop/templatesOrder"%>

<div class="row">
	<div class="col-md-10"><h1 style="margin-bottom:15px;"><spring:theme code="basket.page.title.yourItems"/></h1>
	<p style="color:#002f5f;font-size:16px;margin-bottom:25px;"><spring:theme code="basket.page.cartText"/></p>
	</div>
</div>

<cart:cartTopOptions />

<cart:cartRecommendations />

<c:if test="${not empty cartData.entries}">


<div class="row clearfix">
	<div class="col-md-4">
		<h2><spring:theme code="cart.page.review.title"/></h2>
	</div>
	<div class="col-md-8 cart-links">
			<input type="hidden" id="cartClearConfirmText" value="<spring:theme code="cart.clear.confirm.text"/>"/>
			<input type="hidden" id="cartClearUrl" value="${cartClearUrl}"> 
			<%-- <input type="hidden" id="cartdDeliveryInstructions" value="${cartData.deliveryInstructions}">  --%>
			<c:url value="" var="emptyUrl" scope="session"/>
			<ul class="list-inline">
				<li><a class="inline" href="${continueShoppingUrl}"><spring:theme code="general.continue.shopping"/></a></li>
				<li><a class="inline clearCart regular-popup" href="#clearCartPopup"><spring:theme code="cart.page.clear.cart"/></a></li>
				<li class="magnific-template-order"><a class="inline" href="#save-as-template"><spring:theme code="cart.page.save.as.template"/></a></li>
			</ul>
	</div>
</div>

	<cart:cartItems cartData="${cartData}"/>
</c:if>

<templatesOrder:templateOrderPopup/>