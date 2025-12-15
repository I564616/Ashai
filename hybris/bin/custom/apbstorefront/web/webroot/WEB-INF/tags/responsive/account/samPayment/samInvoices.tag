<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="formElement"
	tagdir="/WEB-INF/tags/responsive/formElement"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fn" uri="jakarta.tags.functions"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix = "fmt" uri = "http://java.sun.com/jsp/jstl/fmt" %>
	
<spring:url value="/my-account/saved-cards" var="savedCardsUrl"
	htmlEscape="false" />
<%@ taglib prefix="single-checkout"
	tagdir="/WEB-INF/tags/responsive/checkout/single"%>
	<spring:theme code="text.invoice.payment.reference.placeholder" var="payRefPlaceholder" />
<div class="row">
		<div class="col-md-7 col-sm-12">
			<div class="account-section account-section-no-border">
				<div class="account-section-content">
                    <div class="responsive-table">
                        <table class="responsive-table responsive-table-invoices">
                            <thead>
                                <tr class="responsive-table-head hidden-xs">
                                    <th class="payment-heading-1">
                                        <spring:theme code="text.invoice.payment.header.doc.no" />
                                    </th>
                                    <th class="right-align-label payment-heading-2 text-left">
                                        <spring:theme code="text.invoice.payment.header.remaining" />
                                    </th>
                                    <th class="payment-heading-3 text-left">
                                        <spring:theme code="text.invoice.payment.header.desc" />
                                    </th>
                                    <th class="right-align-label payment-heading-4 hidden">
                                        <spring:theme code="text.invoice.payment.header.pay.amt" />
                                    </th>
                                    <th class="payment-heading-5 hidden">
                                    </th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach items="${asahiSamPaymentForm.asahiSamInvoiceForm}" var="asahiSamInvoiceForm" varStatus="status">
                                    <tr class="responsive-table-item make-payment-table js-open-invoice${status.index}">
                                        <td class="hidden-sm hidden-md hidden-lg payments-table-row">
                                            <spring:theme code="text.invoice.payment.header.doc.no" />
                                        </td>
                                        <td headers="header1" id="header1" class="payment-heading-1 responsive-table-cell">${asahiSamInvoiceForm.docNumber}
                                            <form:hidden path="asahiSamInvoiceForm[${status.index}].docNumber" />
                                            <form:hidden path="asahiSamInvoiceForm[${status.index}].lineNumber" />
                                        </td>
                                        <td class="hidden-sm hidden-md hidden-lg payments-table-row">
                                            <spring:theme code="text.invoice.payment.header.remaining" />
                                        </td>
                                        <td class="payment-heading-2 responsive-table-cell right-align-label text-left">
                                            <c:set var="remainAmount"><fmt:formatNumber type="number" groupingUsed="false" minFractionDigits="2" value ="${asahiSamInvoiceForm.remainingAmount}" /></c:set>
                                            ${remainAmount}
                                            <form:hidden path="asahiSamInvoiceForm[${status.index}].remainingAmount" />
                                        </td>
                                        <td class="hidden-sm hidden-md hidden-lg payments-table-row">
                                            <spring:theme code="text.invoice.payment.header.desc" />
                                        </td>
                                        <td class="payment-heading-3 responsive-table-cell">${asahiSamInvoiceForm.documentType}
                                            <form:hidden path="asahiSamInvoiceForm[${status.index}].documentType" />
                                        </td>
                                        <td class="hidden-sm hidden-md hidden-lg payments-table-row hidden">
                                            <spring:theme code="text.invoice.payment.header.pay.amt" />
                                        </td>
                                        <td id="payableAmount" class="payments-table-amount-box payment-heading-4 responsive-table-cell right-align-label hidden">
                                            <input type="number" name="asahiSamInvoiceForm[${status.index}].paidAmount" class="payments-table-amount-input form-control js-amount-field" id="paidAmount${status.index}" type="text" rowNum="${status.index}" onclick="select()" doctype="${asahiSamInvoiceForm.documentType}" originalAmount="${asahiSamInvoiceForm.remainingAmount}" remainingAmount="${asahiSamInvoiceForm.remainingAmount}" value="${asahiSamInvoiceForm.remainingAmount}">
                                        </td>
                                        <td class="hidden-sm hidden-md hidden-lg payments-table-row hidden"></td>
                                        <td class="payment-heading-5 responsive-table-cell hidden"><a id="${status.index}" class="site-anchor-link js-amount-update disabled" href=""><spring:theme code="text.invoice.payment.reference.update" /></a></td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
				</div>
			</div>
		</div>
</div>
<div id="partialPaymentSection" class="hide">
	<div class="row">
		<div class="col-md-3">
			<formElement:formSelectBox idKey="partial-payment-droplist"
				labelKey="text.invoice.partial.payment.label"
				selectCSSClass="form-control" path="samPaymentReason" mandatory="false"
				skipBlank="false" skipBlankMessageKey="text.invoice.partial.reason.three.dots"
				items="${asahiSamPaymentReasonList}" />
			<div id="reqField" class="required-field hidden">
				<spring:theme code="text.invoice.mandatory.field" />
			</div>
		</div>
	</div>
</div>

<div id="paymentRefSection">
	<label class="control-label"><span><spring:theme code="text.invoice.payment.reference.label" /></span></label>
	<spring:theme code="text.invoice.payment.reference.label.optional" />
	<div class="ref-label"><spring:theme code="text.invoice.payment.reference.note" /></div>
	<div class="row">
		<div class="col-sm-6 col-xs-12 col-md-3 col-lg-3">
			<div class="form-group">
				<input class="form-control" id="paymentRefField" name="samPaymentReference" path="paymentReference" maxlength="30" placeholder="${payRefPlaceholder}" />
			</div>
		</div>
	</div>
</div>
