<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template" %>
<%@ taglib prefix="cart" tagdir="/WEB-INF/tags/desktop/cart" %>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/desktop/product" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common" %>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/desktop/nav" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<spring:theme text="Your Shopping Cart" var="title" code="cart.page.title"/>
<template:page pageTitle="${pageTitle}">

<div id="cartCtrl" ng-controller="cartCtrl" ng-init="init(true)" data-calculation="${requiresCalculation}" ng-cloak>

	<script id="cartDealsData" type="text/json">${ycommerce:generateJson(cartDealsData)}</script>
    <%-- <cart:dummyData/> --%>
	<spring:theme code="basket.add.to.cart" var="basketAddToCart"/>
	<spring:theme code="cart.page.checkout" var="checkoutText"/>
	<c:if test="${param.cartTimeout=='1'}">
		<div class="alert server-error">
			<spring:theme code="basket.page.checkout.timeout.error.message" />
		</div>
	</c:if>
    <div id="simulationErrors">
		<common:globalMessages/>
	</div>
	
    <div id="globalMessage">
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
        <c:if test="${not empty param.generalError}">
            <div class="alert server-error">
                <spring:theme code="checkout.multi.paymentMethod.addPaymentDetails.generalError" />

            </div>
        </c:if>
        <c:if test="${not empty param.declined and empty param.checkoutTimeoutError}">
            <div class="alert server-error">
                <spring:theme code="checkout.error.payment.not.accepted" />
            </div>
        </c:if>
        <c:if test="${not empty param.declined and not empty param.checkoutTimeoutError}">
            <div class="alert server-error">
                <spring:theme code="checkout.payment.declined.cutoff.exceeded" />
            </div>
        </c:if>
        <c:if test="${not empty param.checkoutTimeoutError and empty param.declined}">
            <div class="alert server-error">
                <spring:theme code="basket.page.checkout.timeout.error.message" />
            </div>
        </c:if>
        <c:if test="${not empty param.authfailure}">
            <div class="alert server-error">
                <spring:theme code="checkout.error.authorization.failed" arguments="${param.authfailure}"/>
            </div>
        </c:if>
        <c:if test="${not empty param.tokenError}">
            <div class="alert server-error">
                <spring:theme code="checkout.error.westpac.tokenError" arguments="${param.tokenError}"/>
            </div>
        </c:if>
        <c:if test="${not empty param.calculationError}">
            <div class="alert server-error">
                <spring:theme code="basket.page.salesordersimulate.error.message" />
            </div>
        </c:if>
        <c:if test="${not empty param.invalidCart}">
            <div class="alert server-error">
                <spring:theme code="basket.page.salesordersimulate.message" />
            </div>
        </c:if>
        <c:if test="${not empty param.cutoffTimeoutError}">
            <div class="alert server-error">
                <spring:theme code="basket.page.cutofftime.error.message" />
            </div>
        </c:if>
        <c:if test="${not empty selectdefaultShippingCarrierErrorMsg}">
            <div class="alert neutral">
                <spring:theme code="basket.page.selectdefaultShippingCarrier.info.message" />
            </div>
        </c:if>
    </div>
	<div id="deal-notification-bad" class="<c:if test="${not empty rejectedDeals and fn:length(rejectedDeals) > 0}">well deal-notification-bad</c:if>">
		<c:if test="${not empty rejectedDeals and fn:length(rejectedDeals) > 0}">
			<div class="h4"><spring:theme code="basket.page.rejected.deal.message" /></div>
			<c:forEach items="${rejectedDeals}" var="rejectedDeal">
				<div class="deal-title">
					${rejectedDeal}
				</div>
			</c:forEach>
		</c:if>
	</div>
	<div id="checkoutNotAllowed">
    	<div class="checkoutNotAllowed alert negative" style="display:none; text-align:left;"><spring:theme code="text.checkout.notallowed.error" /></div>
    </div>
	<c:url value="/cart" var="cartUrl" scope="session"/>
	<c:url value="/cart/remove" var="cartRemoveUrl" scope="session"/>
	<c:url value="/cart/clear" var="cartClearUrl" scope="session"/>
	<c:url value="/checkout" var="checkoutUrl" scope="session"/>
	<c:url value="/cart/updateQuantity" var="cartUpdateQuantityUrl" scope="session"/>
	<c:url value="/Beer/c/10" var="continueShoppingUrl" scope="session"/>
	
	<cms:pageSlot position="TopContent" var="feature" element="div">
		<cms:component component="${feature}"/>		
	</cms:pageSlot>

	<%-- <div id="deal-notification-auto" class="<c:if test="${not empty cartData.autoAppliedDeals and fn:length(cartData.autoAppliedDeals) > 0}">well deal-notification</c:if>">
		<c:if test="${not empty cartData.autoAppliedDeals and fn:length(cartData.autoAppliedDeals) > 0}">
			<c:choose>
				<c:when test="${fn:length(cartData.autoAppliedDeals) >1}">
					 <div class="h4"><spring:theme code="basket.page.applied.multiple.deals.message" /></div>
				</c:when>
				<c:otherwise>
					 <div class="h4"><spring:theme code="basket.page.applied.one.deal.message" /></div>
				</c:otherwise>
			</c:choose>
			<c:forEach items="${cartData.autoAppliedDeals}" var="autoAppliedDeal">
				<div class="deal-title">
					${autoAppliedDeal.title}
				</div>
			</c:forEach>
		</c:if>
	</div> --%>
	
	<div id="deal-notification-auto" class="<c:if test="${not empty cartData.autoAppliedAllDealsToCart and fn:length(cartData.autoAppliedAllDealsToCart) > 0}">well deal-notification</c:if>">	
		<c:if test="${not empty cartData.autoAppliedAllDealsToCart and fn:length(cartData.autoAppliedAllDealsToCart) > 0}">
			<c:choose>
				<c:when test="${fn:length(cartData.autoAppliedAllDealsToCart) >1}">
					 <div class="h4"><spring:theme code="basket.page.applied.multiple.deals.message" /></div>
				</c:when>
				<c:otherwise>
					 <div class="h4"><spring:theme code="basket.page.applied.one.deal.message" /></div>
				</c:otherwise>
			</c:choose>
			<c:forEach items="${cartData.autoAppliedAllDealsToCart}" var="autoAppliedDeal">
				<div class="deal-title">
					${autoAppliedDeal.title}
				</div>
			</c:forEach>
		</c:if>		
	</div>
	
	<c:if test="${not empty cartData.entries}">
		<div class="row">
			<div class="col-md-6">
				<cart:cartPromoCode />
			</div>
			<div id="totals-section" class="col-md-6 cart-totals">
				<cms:pageSlot position="CenterRightContentSlot" var="feature">
					<cms:component component="${feature}"/>
				</cms:pageSlot>
			</div>
		</div>

		<cms:pageSlot position="BottomContentSlot" var="feature" element="div" class="row">
			<cms:component component="${feature}"/>
		</cms:pageSlot>
	</c:if>
	<cart:popupChooseFree />
	<cart:popupPartiallyQualified />
	<cart:popupDealsConflict />
	<cart:popupLoseDeal />
	<cart:popupClearCart />
	<product:productPricePopup />
	<input type="hidden" id="checkoutStep" name="checkoutStep" value="${checkoutStep}"/>
</div>
</template:page>
