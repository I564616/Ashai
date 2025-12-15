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
<c:set var="samPaymentDetailsUrl" value="${request.contextPath}/invoice/payment" scope="session" />
<c:url value="/paymentHistory" var="paymentHistoryLink" scope="session" />
<c:url value="/invoice" var="backToInvoicePage"/>	

<div class="row payment-confirmation-section">
	<div class="col-lg-6 col-md-6 col-sm-6 col-xs-12 confirmation-heading">
		<div class="confirmation-heading"><img src="${commonResourcePath}/images/done.png"  /> <spring:theme code ="text.invoices.payments.confirmation.message"/></div>
		<div class="confirmation-message">
			<spring:theme code="text.invoices.payments.processed.message" />
		</div>
		<div class="confirmation-message">
			<spring:theme code="text.invoices.payments.history.link1"/>&nbsp;<a class="site-anchor-link"  href="${paymentHistoryLink}"><spring:theme code="text.invoices.payments.history.link2"/></a>&nbsp;<spring:theme code="text.invoices.payments.history.link3"/>
		</div>
		<div class="confirmation-goback-link">
			<c:url value="/invoice" var="backToInvoicePage"/>
			<a class="site-anchor-link" href="${backToInvoicePage}"><spring:theme code="text.invoice.back.to.invoices.link"/></a>
		</div>
	</div>
	<div class="col-lg-6 col-md-6 col-sm-6 col-xs-12">
		<div class="row summary-due-row no-margin">
			<div class="payment-ref-section">
				<spring:theme code="text.invoices.payments.reference.no" />
				<span class="payment-ref-no">${asahiCaptureResponseData.paymentReference}</span>
			</div>
			
			<div class="payment-total-section">
				<c:choose>
					<c:when test="${asahiCaptureResponseData.invoiceCount eq 1}">
						<spring:theme code="text.invoices.payments.total.invoice" arguments="${asahiCaptureResponseData.invoiceCount}"/>
					</c:when>
					<c:otherwise>
						<spring:theme code="text.invoices.payments.total.invoices" arguments="${asahiCaptureResponseData.invoiceCount}"/>
					</c:otherwise>
				</c:choose>
				<span class="payment-ref-no" id="totalPaidAmount"><spring:theme code="text.invoice.payment.amount.currency"/>${asahiCaptureResponseData.totalPaidAmount}</span>
			</div>
		</div>
	</div>
</div>