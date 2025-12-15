<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="order" tagdir="/WEB-INF/tags/desktop/order" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<div class="orderBoxes clearfix">
	<order:deliveryAddressItem order="${orderData}"/>
	<order:deliveryMethodItem order="${orderData}"/>
	<div class="orderBox billing">
		<order:billingAddressItem order="${orderData}"/>
	</div>
	<c:if test="${not empty orderData.paymentInfo}">
		<div class="orderBox payment">
			<order:paymentDetailsItem order="${orderData}"/>
		</div>
	</c:if>
</div>

<div class="row">
	<div class="col-xs-12">
		<a class="link-cta pull-right" href="#"><spring:theme code="text.account.orderHistory.viewInvoices" text="View all invoices for this order"/>
		    <svg class="icon-arrow-right">
		        <use xlink:href="#icon-arrow-left"></use>    
		    </svg>
		</a>
		<div class="row">
			<div class="col-xs-12 col-md-3 col-md-offset-9 margin-top-20 offset-bottom-medium">
				<input id="order-detail-inputID" type="text" style="display: none" value="${orderData.code}">
				<button id="order-detail-button-id1" class="btn btn-primary"><spring:theme code="basket.add.to.basket" text="Add to cart"/></button>
			</div>
		</div>
	</div>
</div>