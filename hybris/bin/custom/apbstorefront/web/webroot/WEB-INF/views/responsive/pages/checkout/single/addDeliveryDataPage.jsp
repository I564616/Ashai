<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="user" tagdir="/WEB-INF/tags/responsive/user"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="fn" uri="jakarta.tags.functions"%>
<%@ taglib prefix="address" tagdir="/WEB-INF/tags/responsive/address"%>
<%@ taglib prefix="multi-checkout" tagdir="/WEB-INF/tags/responsive/checkout/multi"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="b2b-multi-checkout" tagdir="/WEB-INF/tags/addons/b2bacceleratoraddon/responsive/checkout/multi" %>
<%@ taglib prefix="single-checkout" tagdir="/WEB-INF/tags/responsive/checkout/single"%>
<%@ taglib prefix="staff-checkout" tagdir="/WEB-INF/tags/responsive/checkout/staff"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common" %>
<%@ taglib prefix="formElement" tagdir="/WEB-INF/tags/responsive/formElement"%>

<c:set var="addressIndex" value="0"></c:set>
<c:set var="sumbitCheckoutDetailsUrl" value="${request.contextPath}/checkout/single" scope="session"/>
<c:set var="subTotal" value="${cartData.subTotal}"/>

<input type="hidden" id="restrictedPattern" value="${restrictedPattern}"/>
<template:page pageTitle="${pageTitle}" hideHeaderLinks="true">
    <div id="showSpinner"></div>
    <div class="row">
        <div class="col-sm-12 col-lg-12">
            <cms:pageSlot position="BreadCrumbBar" var="feature" class="breadcrumb-bar">
                <cms:component component="${feature}" element="div" class="yComponentWrapper"/>
            </cms:pageSlot>
        </div>
        <div class="checkout-headline user-register__headline">
            <spring:theme code="checkout.multi.secure.checkout"/>
        </div>
        <div></div>
        <form:form method="post" modelAttribute="customerCheckoutForm" action="${sumbitCheckoutDetailsUrl}" id="checkoutDetailsForm">
            <div class="col-sm-12 col-lg-12">
                <cms:pageSlot position="TopContent" var="feature" class="carts-total">
                    <cms:component component="${feature}" element="div" class="yComponentWrapper"/>
                </cms:pageSlot>
            </div>
            <div class="col-sm-12 col-lg-12 optional-disclaimer">
                <spring:theme code="checkout.summary.optional.field.disclaimer"/>
            </div>
            <div class="col-sm-12 col-lg-12">
                <input type="hidden" id="isBDEFlow" value="${isBDEFlow}">
                <c:choose>
                   <c:when test="${isBDEFlow}">
                        <staff-checkout:staffCheckoutForm>
                        </staff-checkout:staffCheckoutForm>
                   </c:when>
            
                   <c:otherwise>
                        <multi-checkout:checkoutSteps checkoutSteps="${checkoutSteps}" progressBarId="${progressBarId}">
                            <jsp:body>
                                <ycommerce:testId code="checkoutStepOne">
                                    <div class="checkout-shipping">
                                        <div class="checkout-indent">
                                            <div class="payment-type">
                                                <div class="checkout_subheading"><spring:theme code="checkout.summary.payment"/></div>
                                                <p id="ptest">
                                                    <b><spring:theme code="checkout.summary.delivery.reference.number"/></b>
                                                    <span id="optional-text"><spring:theme code="checkout.summary.optional.field"/></span>
                                                </p>
                                                <div class="row">
                                                    <div class="col-md-3 col-sm-6">
                                                        <form:input class="form-control" type="text" path="poNumber" maxlength="20"/>
                                                    </div>
                                                </div>
                                                <p></p>
                                                <single-checkout:paymentTypeForm/>


                                            </div>
                                            <div class="checkout_subheading"><spring:theme code="checkout.summary.delivery"/></div>
                                            <single-checkout:addressDataForm deliveryAddresses="${deliveryAddresses}"/>
                                            <single-checkout:deliveryMethodForm/>
                                        </div>
                                    </div>
                                </ycommerce:testId>
                            </jsp:body>
                        </multi-checkout:checkoutSteps>
                   </c:otherwise>
                </c:choose>
            </div>
        </form:form>

        <div class="col-sm-12 col-lg-12 ">
            <cms:pageSlot position="BottomContent" var="feature" element="div" class="cart-display" id="cartSummary">
                <cms:component component="${feature}"/>
            </cms:pageSlot>
            <div class="row checkout_submit_btn ">
                <ycommerce:testId code="checkoutButton">
                    <div class="col-sm-7 col-md-9 col-lg-9"></div>
                    <div class="col-sm-5 col-md-3 col-lg-3">
                        <div class="checkout-btn">
                            <div class="bottom-checkout-btn">
                                <c:set var="isCheckoutDisabled" value="${cartData.priceUpdated eq false || subTotal.value eq '0.0' || empty deliveryAddresses || isExcluded || (cartData.allProductExcluded != null && cartData.allProductExcluded.booleanValue()) || cartData.outofStockItemAvailable}"/>
                                <button id="finalcheckoutButton" class="btn btn-primary btn-block btn-vd-primary btn--continue-checkout js-custom-checkout-button" ${isCheckoutDisabled ? 'disabled' : ''} >
                                    <spring:theme code="checkout.summary.add.details"/>
                                </button>
                                <div class="checkout-term-legal-bottom checkout-term-legal">
                                    <spring:url value="/termsAndLegal" var="getTermsAndConditionsUrl"/>
                                    <spring:theme code="checkout.summary.agree.terms.legal" arguments="${getTermsAndConditionsUrl}"/>
                                </div>
                            </div>
                        </div>
                    </div>
                </ycommerce:testId>
            </div>
        </div>
    </div>
</template:page>
