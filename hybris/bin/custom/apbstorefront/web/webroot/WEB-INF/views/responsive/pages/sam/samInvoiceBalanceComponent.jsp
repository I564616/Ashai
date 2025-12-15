<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="label" uri="/WEB-INF/tld/message.tld" %>
<%@ taglib prefix="b2b-order" tagdir="/WEB-INF/tags/addons/apbb2baccaddon/responsive/order" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="account" tagdir="/WEB-INF/tags/responsive/account"%>

<c:set value="${cmsSite.uid eq 'sga'}" var="isSga" />
<c:set value="Your credit limit is the amount that Asahi will grant you as line of credit upon having an approved account established" var="limit" />
<c:set value="The Credit Available is the balance of your credit line minus any open invoices" var="available" />


<c:if test="${accessType ne 'ORDER_ONLY' && pendingApproval eq false}">												
<div class="row summary-total-row no-margin">
	<c:if test="${isSga}">
        <div class="col-xs-12 col-sm-6 col-md-6 mb-10 summary-section no-padding">
            <div class="col-xs-6 col-sm-6 col-md-6 no-padding">
                <div class="summary-label inline-block">Credit Limit</div>
                <span class="payment-info-icon inline-block" title="${limit}">i</span>
            </div>
            <div class="col-xs-6 col-sm-6 col-md-6 no-padding"><div class="summary-amount">${creditLimit}</div></div>
        </div>
        <div class="clear"></div>
        <div class="col-xs-12 col-sm-6 col-md-6 mb-10 summary-section no-padding">
            <div class="col-xs-6 col-sm-6 col-md-6 no-padding">
                <div class="summary-label inline-block">Credit Available</div>
                <span class="payment-info-icon inline-block" title="${available}">i</span>
            </div>
            <div class="col-xs-6 col-sm-6 col-md-6 no-padding"><div class="summary-amount">${deltaToLimit}</div></div>
        </div>
        <div class="clear"></div>
    </c:if>
	<div class="col-xs-12 col-sm-6 col-md-6 mt-10 summary-section no-padding">
		<div class="col-xs-6 col-sm-6 col-md-6 no-padding">
			<div class="summary-label">
				<c:choose>
					<c:when test="${totalInvoiceCount eq 1}">
						<spring:theme code="text.invoices.payments.total.invoice" arguments="${totalInvoiceCount}"/>
					</c:when>
					<c:otherwise>
						<spring:theme code="text.invoices.payments.total.invoices" arguments="${totalInvoiceCount}"/>
					</c:otherwise>
				</c:choose>
			</div>
		</div>
		<div class="col-xs-6 col-sm-6 col-md-6 no-padding">
			<div class="summary-amount">
			<strong><span id="totalBalance">${totalBalance}</span></strong>
			</div>
		</div>
	</div>
</div>

<div class="row summary-due-row no-margin" <c:if test="${dueNowCount lt 1}"> hidden</c:if>>
	<div class="col-xs-12 col-sm-6 col-md-6 summary-section no-padding">
		<div class="col-xs-7 col-sm-7 col-md-6 no-padding">
			<div class="summary-label">
				<c:choose>
					<c:when test="${dueNowCount eq 1}">
						<spring:theme code="text.invoices.payments.summary.duenow.invoice" arguments="${dueNowCount}"/>
					</c:when>
					<c:otherwise>
						<spring:theme code="text.invoices.payments.summary.duenow.invoices" arguments="${dueNowCount}"/>
					</c:otherwise>
				</c:choose>
			</div>
		</div>
		<div class="col-xs-5 col-sm-5 col-md-6 no-padding">
			<div class="summary-amount">
				<span id="dueNowBalance">${dueNowBalance}</span>
			</div>
		</div>
	</div>
	<div class="col-xs-12 col-sm-6 col-md-6">
		<div class="hidden-xs col-sm-2 col-md-6">
		</div>
		<div class="col-xs-12 col-sm-10 col-md-6 no-padding" id="payNowBtn">
		<c:url value="/invoice/payment" var="samPaymentDetailsUrl" />
		<form action="${samPaymentDetailsUrl}" method="get">
			<button class="btn btn-primary btn-vd-primary btn-block" <c:if test="${dueNowCredit}"> disabled='disabled'</c:if>><spring:theme code="text.invoices.payments.summary.paynow.btn" /></button>
		</form>
		</div>
	</div>
</div>
	
<div class="invoice-payments-pages hidden-xs">
	<div class="yCmsComponent span-24 links-container invoice-payments-pages-link">
		<a href="${pageContext.request.contextPath}/invoice" title="Invoices &amp; Credit">
			<spring:theme code="text.invoices.payments.summary.link.invoice" />
		</a>
	</div>
	<div class="yCmsComponent span-24 links-container invoice-payments-pages-link">
		<a href="${pageContext.request.contextPath}/statement" title="Statements">
			<spring:theme code="text.invoices.payments.summary.link.statements" />
		</a>
	</div>
	<div class="yCmsComponent span-24 links-container invoice-payments-pages-link">
		<a href="${pageContext.request.contextPath}/paymentHistory" title="Payment History">
			<spring:theme code="text.invoices.payments.summary.link.payment.history" />
		</a>
	</div>
	<c:if test="${isDirectDebitEnabled}">	
	<div class="yCmsComponent span-24 links-container invoice-payments-pages-link">
		<a href="${pageContext.request.contextPath}/directdebit" title="Direct Debit">
			<spring:theme code="text.invoices.payments.summary.link.direct.debit" />
		</a>
	</div>
	</c:if>
</div>
	
<select id="invoice-payments-pages" class="invoice-payments-pages form-control hidden-sm hidden-md hidden-lg"> 
	<option value="invoicesCredits"><spring:theme code="text.invoices.payments.summary.link.invoice" /></option> 
	<option value="statements"><spring:theme code="text.invoices.payments.summary.link.statements" /></option> 
	<option value="paymentHistory"><spring:theme code="text.invoices.payments.summary.link.payment.history" /></option> 
	<option value="directdebit"><spring:theme code="text.invoices.payments.summary.link.direct.debit" /></option>
</select>
</c:if>