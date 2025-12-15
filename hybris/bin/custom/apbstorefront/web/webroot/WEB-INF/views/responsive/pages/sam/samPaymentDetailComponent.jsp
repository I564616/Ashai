<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="label" uri="/WEB-INF/tld/message.tld" %>
<%@ taglib prefix="b2b-order" tagdir="/WEB-INF/tags/addons/apbb2baccaddon/responsive/order" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="sam-payment" tagdir="/WEB-INF/tags/responsive/account/samPayment"%>
<%@ taglib prefix="single-checkout" tagdir="/WEB-INF/tags/responsive/checkout/single"%>
<%@ taglib prefix = "fmt" uri = "http://java.sun.com/jsp/jstl/fmt" %>
	
<c:set var="samPaymentDetailsUrl" value="${request.contextPath}/invoice/payment" scope="session" />

<div class="col-md-12 no-padding">
	<div class="row make-a-payment-section">
		<div class="col-xs-12 col-sm-12 col-md-12 col-lg-12">
			<div class="login-page__headline">
				<spring:theme code="text.invoices.payments.details.heading" />
			</div>

			<div class="account-label">
				<spring:theme code="text.invoices.payments.details.message" />
			</div>
		</div>
	</div>

	<form:form method="POST" action="${samPaymentDetailsUrl}" id="asahiSamPaymentForm" modelAttribute="asahiSamPaymentForm">
		<div class="row summary-due-row">
			<div class="col-xs-12 col-sm-6 col-md-6 summary-section no-padding">
				<div class="col-xs-7 col-sm-7 col-md-6 no-padding">
					<div class="summary-label">
						<c:choose>
							<c:when test="${asahiSamPaymentForm.totalInvoiceCount eq 1}">
								<spring:theme code="text.invoices.payments.total.invoice" arguments="${asahiSamPaymentForm.totalInvoiceCount}"/>
							</c:when>
							<c:otherwise>
								<spring:theme code="text.invoices.payments.total.invoices" arguments="${asahiSamPaymentForm.totalInvoiceCount}"/>
							</c:otherwise>
						</c:choose>
					</div>
				</div>
				<div class="col-xs-5 col-sm-5 col-md-6 no-padding">
					<div class="summary-amount">
						<spring:theme code="text.invoice.payment.amount.currency" /><span id="totalAmountSection">${asahiSamPaymentForm.initialTotalAmount}</span>
						<form:hidden path="totalPayableAmount" value="${asahiSamPaymentForm.initialTotalAmount}" />
						<input hidden id="originalPayableAmount" initVal="${asahiSamPaymentForm.initialTotalAmount}" value="${asahiSamPaymentForm.initialTotalAmount}"/>
					</div>
				</div>
			</div>
			<div class="col-xs-12 col-sm-6 col-md-6">
				<div class="hidden-xs col-sm-2 col-md-6">
				</div>
				<div class="col-xs-12 col-sm-10 col-md-6 no-padding" id="payNowBtn">
					<fmt:parseNumber var="totalAmount" type="number" value="${asahiSamPaymentForm.initialTotalAmount}" />
					<button id="makeSamPayment" type="submit" class="btn btn-primary btn-vd-primary btn-block link-margin js-make-payment-btn" <c:if test="${totalAmount lt 0}">disabled="disabled"</c:if>>
					<spring:theme code="text.invoices.payments.payment.paynow.btn" /></button>
					
					<c:url value="/invoice" var="backToInvoicePage" />
					<a class="site-anchor-link" href="${backToInvoicePage}">
						<spring:theme code="text.invoice.back.to.invoices.link" /> <c:out value = "${i}" />
					</a> 
				</div>
			</div>
		</div>
		
		<sam-payment:samInvoices/>

		<sam-payment:samPaymentForm/>

		<div class="row summary-due-row no-margin extra-top-margin">
			<div class="col-xs-12 col-sm-6 col-md-offset-6 col-md-6">
				<div class="hidden-xs col-sm-2 col-md-6">
				</div>
				<div class="col-xs-12 col-sm-10 col-md-6 no-padding" id="payNowBtn">
					<button type="submit" class="btn btn-primary btn-vd-primary btn-block js-make-payment-btn"><spring:theme code="text.invoices.payments.payment.paynow.btn" /></button>
				</div>
			</div>
		</div>

	</form:form>
</div>