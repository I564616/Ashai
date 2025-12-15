<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="checkout" tagdir="/WEB-INF/tags/desktop/checkout" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<div class="row">
	<div class="col-xs-12">
		<h2><spring:theme code="text.cart.delivery.date"/></h2>
		<div class="date">
			<svg class="icon-calendar">
			  <use xlink:href="#icon-calendar"></use>    
			 </svg>
			 <a href="#" class="inline"><span class="h3"><fmt:formatDate value="${cartData.requestedDeliveryDate}" pattern="EEEE dd/MM/yyyy"/></span></a>
		</div>
		<p><spring:theme code="text.checkout.delivery.cutoff.message"/></p>
	</div>
</div>
<c:url value="/cart" var="cartUrl"/>
<a href="${cartUrl}?checkoutStep=${checkoutStep}" class="btn btn-secondary">Update</a>
<input type="hidden" id="checkoutStep" name="checkoutStep" value="${checkoutStep}"/>
<hr class="visible-xs-block visible-sm-block">