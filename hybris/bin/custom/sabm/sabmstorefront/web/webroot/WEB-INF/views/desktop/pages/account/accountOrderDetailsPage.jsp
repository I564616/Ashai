<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common"%>
<%@ taglib prefix="deals" tagdir="/WEB-INF/tags/desktop/deals"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/desktop/nav" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<%@ taglib prefix="fn" uri="jakarta.tags.functions"%>
<%@ taglib prefix="order" tagdir="/WEB-INF/tags/desktop/order" %>
<%@ taglib prefix="templatesOrder" tagdir="/WEB-INF/tags/desktop/templatesOrder"%>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/desktop/product" %>


<c:set var="tempImgPath" value="/_ui/desktop/SABMiller/img/" />
<c:url value="/your-business/orderItemsAdd/" var="orderItemsAddToCartUrl" />
<c:url var="trackMyDeliveryPath" value="/trackorders?code=${orderData.sapSalesOrderNumber}"/>


<template:page pageTitle="${pageTitle}">
	<input id="order-detail-inputID" type="text" style="display: none" value="${orderData.code}">
	<input type="hidden" class="orderAddToCartUrl" value="${orderAddToCartUrl }">
	<input type="hidden" class="orderItemsAddToCartUrl" value="${orderItemsAddToCartUrl }">
	<div class="order-detail">
	<div id="globalMessages">
		<common:globalMessages />
	</div>
	
<div class="row">
	<div class="col-xs-12">
		<h1><spring:theme code="text.account.order.title.details"/></h1>
	</div>
</div>

<c:if test="${orderData.bdeOrder eq true}">
<div class="row">
	<div class="col-xs-12">
		<h2><spring:theme code="text.account.order.bdeorder.details"/></h2>
	</div>
</div>
</c:if>

<div class="row">
	<div class="col-md-6 col-sm-6 col-xs-12">
	    <h2><spring:theme code="text.account.order.orderNumber" arguments="${orderData.sapSalesOrderNumber}"/></h2>
	    <c:set var="count" value="0" scope="request" />
		<c:choose>
		<c:when test="${isTrackDeliveryOrderFeatureEnabled}">
			<c:forEach items="${orderData.consignments}" var="shipment">
				<fmt:parseNumber var="shipmentCount" value="${orderData.consignments.size()}" />
				<fmt:parseNumber var="total" value="1" />
				<c:choose>

					<c:when test="${shipmentCount gt total}">
						<c:set var="count" value="${count + 1}" scope="request" />

						<%-- <p><strong><spring:theme code="text.trackorder.multiple.shipment" arguments="${count}"/></strong>&nbsp;<spring:theme code="text.account.order.consignment.status.${shipment.status}"/></p> --%>
						<c:choose>
							<c:when test="${fn:toLowerCase(shipment.status) == 'shipped'}">								
								<p><strong><spring:theme code="text.trackorder.multiple.shipment" arguments="${count}"/></strong>&nbsp;<spring:theme code="text.account.order.status.display.beingDispatched" /></p>
							</c:when>								
							<c:otherwise>
								<p><strong><spring:theme code="text.trackorder.multiple.shipment" arguments="${count}"/></strong>&nbsp;<spring:theme code="text.account.order.consignment.status.${shipment.status}"/></p>								
							</c:otherwise>
						</c:choose>
					</c:when>
					<c:otherwise>
						<%-- <p><strong><spring:theme code="text.account.order.status"/></strong>&nbsp;<spring:theme code="text.account.order.consignment.status.${shipment.status}"/></p> --%>
						<c:choose>
							<c:when test="${fn:toLowerCase(shipment.status) == 'shipped'}">								
								<p><strong><spring:theme code="text.account.order.status"/></strong>&nbsp;<spring:theme code="text.account.order.status.display.beingDispatched" /></p>
							</c:when>								
							<c:otherwise>
								<p><strong><spring:theme code="text.account.order.status"/></strong>&nbsp;<spring:theme code="text.account.order.consignment.status.${shipment.status}"/></p>
							</c:otherwise>
						</c:choose>
					</c:otherwise>
				</c:choose>
			</c:forEach>

		</c:when>
		<c:otherwise>
			<p><strong><spring:theme code="text.account.order.status"/></strong>&nbsp;<spring:theme code="text.account.order.consignment.status.${orderData.statusToDisplay}"/></p>
		</c:otherwise>
		</c:choose>

		<c:choose>
		<c:when test="${orderData.salesApplication  eq 'SAP'}">		
		</c:when>
		<c:otherwise>
			<p><strong><spring:theme code="text.account.order.placedby"/></strong>&nbsp;${orderData.userDisplayName}</p>	
		</c:otherwise>
		</c:choose>
		<p><strong><spring:theme code="text.account.order.customer.number"/></strong>&nbsp;${orderData.b2bUnit.uid}</p>
		<p><strong><spring:theme code="text.account.order.payer.number"/></strong>&nbsp;${orderData.b2bUnit.payerId}</p>
		<p><strong><spring:theme code="text.account.order.sales.application"/></strong>&nbsp;${orderData.salesApplication}</p>
    	<c:choose>
			<c:when test="${orderData.isFreeGoodOrder}">
				<p><strong><spring:theme code="text.account.order.type"/></strong>&nbsp;<spring:theme code="text.account.order.type.freegoods"/></p>
			</c:when>
			<c:otherwise>
				<p><strong><spring:theme code="text.account.order.type"/></strong>&nbsp;<spring:theme code="text.account.order.type.standard"/></p>
			</c:otherwise>
		</c:choose>
		
	</div>
	<c:if test="${fn:containsIgnoreCase(orderData.statusDisplay,'Processing')}">
        <div class="col-md-6 col-sm-6 col-xs-12">
                <common:amendCancelMessage/>
         </div>
    </c:if>
</div>

<div class="row cart-delivery-payment">
	<c:if test="${isTrackDeliveryOrderFeatureEnabled}">
		<div class="col-md-4 cart-deliverydate">
			<div>
				<h2><spring:theme code="text.account.order.details.delivery.date"/></h2>
				<h3><fmt:formatDate value="${orderData.requestedDeliveryDate}" pattern="EEEE dd/MM/yyyy" type="date" /></h3>
				<div class="clearfix"></div>
				<br /><br />
				<a href="${trackMyDeliveryPath}" class="btn btn-primary">
					<spring:theme code="text.orderreceived.trackyourdelivery.header" />
				</a>
			</div>
			<hr class="visible-xs-block visible-sm-block">
		</div>
	</c:if>
	<div class="col-md-4 cart-deliverymethod">
		<div>
			<h2><spring:theme code="text.delivery.method.title"/></h2>

			<ul>
				<li>${orderData.deliveryAddress.title}${empty orderData.deliveryAddress.title ? '' : '&nbsp;'}${orderData.deliveryAddress.firstName}${empty orderData.deliveryAddress.firstName ? '' : '&nbsp;'}${orderData.deliveryAddress.lastName}</li>
				<li>${orderData.b2bUnit.name}</li>
				<li>${orderData.deliveryAddress.line2}${empty orderData.deliveryAddress.line2 ? '' : '&nbsp;'}${orderData.deliveryAddress.line1}</li>
				<li>${orderData.deliveryAddress.town}</li>
				<li>${orderData.deliveryAddress.region.name}${empty orderData.deliveryAddress.region.name ? '' : '&nbsp;'}${orderData.deliveryAddress.postalCode}</li>
			</ul>	
			<h3>${orderData.deliveryMode.name}</h3>
			<c:if test="${orderData.deliveryMode.code eq 'Customer-Arranged-Delivery'}">
<%--				<h4><spring:theme code="text.delivery.method.carrier"/></h4>--%>
				 <div class="select-list">
<%--					<div data-value="" class="select-btn sort"> --%>
					<div data-value="" class="sort">
					${orderData.deliveryShippingCarrier.description}</div>
				</div>
			</c:if>
		
		</div>
		<hr class="visible-xs-block visible-sm-block">
	</div>
	<div class="col-md-4 cart-paymentoptions">
		<div>
			<h2><spring:theme code="text.account.order.details.payment.method"/></h2>
		<c:choose>
		<c:when test="${orderData.salesApplication  eq 'SAP'}">	
		</c:when>
		<c:otherwise>
			<h3><spring:theme code="text.cart.payment.account"/></h3>		
		</c:otherwise>
		</c:choose>
			<h4><spring:theme code="text.cart.payment.po"/></h4>
<%-- 			TODO --%>

			<c:choose>
				<c:when test="${not empty orderData.purchaseOrderNumber}">
		     <span>${orderData.purchaseOrderNumber}</span>
				</c:when>
				<c:otherwise>
				<span>${orderData.user.uid}</span>
				</c:otherwise>
			</c:choose>
			<c:if test="${not empty orderData.paymentInfo.hideCardNumber}">
				<h4><spring:theme code="text.cart.payment.creditCard"/></h4>
				<span>${orderData.paymentInfo.hideCardNumber}</span>
			</c:if>
		</div>
	</div>
</div>
<c:if test="${!isNAPGroup}">
<div class="row">
	<div class="col-xs-12">
		<div class="row">
			<div class="col-xs-12 col-md-7 col-md-offset-5 margin-top-20 offset-bottom-medium">
				<div class="pull-right magnific-template-order">
					<a class="inline" href="#save-as-template"><spring:theme code="cart.page.save.as.template"/></a>

							<c:forEach items="${orderData.consignments}" var="consignment">
								<c:forEach items="${consignment.entries}" var="entry">
								<c:if test="${entry.orderEntry.product.cubStockStatus.code eq 'outOfStock'}">
									<c:set value="true" var="greyOutReorderButton" />
									<input type="hidden" id="isOutOfStockProductsPresent" value="true"/>
								</c:if>
								</c:forEach>
							</c:forEach>
														
							<button id="order-detail-button-id1" class="btn btn-primary bde-view-only"><spring:theme code="text.orderTemplate.table.actions.cart"/></button>
				</div>
			</div>
		</div>
	</div>
</div>
</c:if>

<templatesOrder:templateOrderPopup/>


<order:orderEntryDetailsItem />


<c:if test="${!isNAPGroup}">
<div class="row">
	<div class="col-xs-12 col-md-7 col-md-offset-5">
		<div class="pull-right magnific-template-order">
			<a class="inline" href="#save-as-template"><spring:theme code="cart.page.save.as.template"/></a>
			<button id="order-detail-button-id2" class="btn btn-primary bde-view-only"><spring:theme code="text.orderTemplate.table.actions.cart"/></button>
		</div>
	</div>
</div>
</c:if>
	<templatesOrder:templateOrderPopup/>
	<common:addItemsPopup/>

        <hr/>
        <cms:pageSlot position="BottomContent" var="feature" element="div">
			<cms:component component="${feature}"/>
		</cms:pageSlot>
	</div>
</template:page>