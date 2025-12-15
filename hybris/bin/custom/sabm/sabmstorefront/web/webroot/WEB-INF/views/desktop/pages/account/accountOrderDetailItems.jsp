<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="order" tagdir="/WEB-INF/tags/desktop/order" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<c:if test="${not empty orderData.unconsignedEntries}">
	<order:orderUnconsignedEntries order="${orderData}"/>
</c:if>
<c:set var="headingWasShown" value="false"/>
<c:forEach items="${orderData.consignments}" var="consignment">
	<c:if test="${consignment.status.code eq 'WAITING' or consignment.status.code eq 'PICKPACK' or consignment.status.code eq 'READY'}">
		<c:if test="${not headingWasShown}">
			<c:set var="headingWasShown" value="true"/>
			<h2>
				<spring:theme code="text.account.order.title.inProgressItems"/>
			</h2>
		</c:if>
		<div class="productItemListHolder fulfilment-states-${consignment.status.code}">
			<order:accountOrderDetailsItem order="${orderData}" consignment="${consignment}" inProgress="true"/>
		</div>
	</c:if>
</c:forEach>
<c:forEach items="${orderData.consignments}" var="consignment">
	<c:if test="${consignment.status.code ne 'WAITING' and consignment.status.code ne 'PICKPACK' and consignment.status.code ne 'READY'}">
		<div class="productItemListHolder fulfilment-states-${consignment.status.code}">
			<order:accountOrderDetailsItem order="${orderData}" consignment="${consignment}"/>
		</div>
	</c:if>
</c:forEach>
<div class="row">
	<div class="col-xs-12 col-md-3 col-md-offset-9 margin-top-20">
		<button id="order-detail-button-id2" class="btn btn-primary"><spring:theme code="basket.add.to.basket" text="Add to order"/></button>
	</div>
</div>