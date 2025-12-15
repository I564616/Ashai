<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common" %>
<%@ taglib prefix="checkout" tagdir="/WEB-INF/tags/desktop/checkout" %>
<%@ taglib prefix="multi-checkout" tagdir="/WEB-INF/tags/desktop/checkout/multi" %>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags" %>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>


<sec:authorize access="hasAnyRole('ROLE_BDEVIEWONLYGROUP')">
   <c:set var="placeOrderDisable" value="${!isBdeOrderingEnabled}" scope="request"/>
</sec:authorize>

<template:page pageTitle="${pageTitle}" hideHeaderLinks="true">
	<div ng-controller="formsCtrl" ng-init="ccCheckoutFormInit()">
	
	<cms:pageSlot position="TopContent" var="feature" element="div" limit="1">
		<cms:component component="${feature}"/>
	</cms:pageSlot>
	<div class="row">
		<div class="col-md-10"><h1><spring:theme code="basket.page.checkout.checkout"/></h1></div>
	</div>
	<div id="globalMessages">
		<common:globalMessages/>
		<c:if test="${not empty param.paymentDeclined}">
			<div class="alert server-error">
				<spring:theme code="checkout.error.merchant.suite.declined"/>
			</div>
		</c:if>
		<c:if test="${not empty param.invalidCard}">
			<div class="alert server-error">
				<spring:theme code="checkout.error.merchant.suite.invalid.card"/>
			</div>
		</c:if>
		<c:if test="${not empty param.expiredCard}">
			<div class="alert server-error">
				<spring:theme code="checkout.error.merchant.suite.expired.card"/>
			</div>
		</c:if>
		<c:if test="${not empty param.noFunds}">
			<div class="alert server-error">
				<spring:theme code="checkout.error.merchant.suite.no.funds"/>
			</div>
		</c:if>
		<c:if test="${not empty param.gatewayError}">
			<div class="alert server-error">
				<spring:theme code="checkout.error.merchant.suite.gateway.error"/>
			</div>
		</c:if>
		<c:if test="${not empty param.invalidExpiry}">
			<div class="alert server-error">
				<spring:theme code="checkout.error.merchant.suite.invalid.expiry"/>
			</div>
		</c:if>
		<c:if test="${not empty param.paymentError}">
			<div class="alert server-error">
				<spring:theme code="checkout.error.merchant.suite.gateway.error"/>
			</div>
		</c:if>
		<c:if test="${not empty param.paymentWaitError}">
			<div class="alert server-error">
				<spring:theme code="basket.page.checkout.paymentwait.error.message" arguments="${param.paymentWaitError}"/>
			</div>
		</c:if>
		<c:if test="${not empty param.declined}">
			<div class="alert server-error">
				<spring:theme code="checkout.error.payment.not.accepted" />
			</div>
		</c:if>
        <c:if test="${not empty param.authfailure}">
            <div class="alert server-error">
                <spring:theme code="checkout.error.authorization.failed" arguments="${param.authfailure}"/>
            </div>
        </c:if>
        <c:if test="${not empty param.generalError}">
            <div class="alert server-error">
                <spring:theme code="checkout.multi.paymentMethod.addPaymentDetails.generalError" />
            </div>
        </c:if>
		<c:if test="${not empty param.orderError}">
			<div class="alert server-error">
				<spring:theme code="checkout.placeOrder.failed2" />
			</div>
		</c:if>
	</div>
	<input type="hidden" class="page-multiStepCheckoutSummaryPage">
	<multi-checkout:checkoutOrderDetails cartData="${cartData}" showShipDeliveryEntries="true" showPickupDeliveryEntries="true" showTax="false"/>
	
	
	<c:if test="${bdeUser && isBdeOrderingEnabled}">
	<checkout:bdeOrderEmailCapture />
	</c:if>
	</div>
	
</template:page>