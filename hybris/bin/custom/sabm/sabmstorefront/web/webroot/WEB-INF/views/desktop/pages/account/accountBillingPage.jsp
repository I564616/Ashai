<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common"%>
<%@ taglib prefix="deals" tagdir="/WEB-INF/tags/desktop/deals"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/desktop/nav" %>
<%@ taglib prefix="popup" tagdir="/WEB-INF/tags/desktop/checkout"%>
<%@ taglib prefix="fn" uri="jakarta.tags.functions"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<script id="ccValidationData" type="text/json">${ccValidationData}</script>

<c:set var="tempImgPath" value="${staticHostPath}/_ui/desktop/SABMiller/img/" />

<template:page pageTitle="${pageTitle}">

	<script>
		var invoiceSelectedList = '<c:out value="${sessionScope.invoiceSelectedList}" />'
	</script>
	
	<div class="billing-payment">
	<cms:pageSlot position="TopContentSlot" var="feature" element="div">
		<cms:component component="${feature}"/>
	</cms:pageSlot>

	<div id="globalMessages">
		<common:globalMessages />
		<c:if test="${not empty param.paymentDeclined}">
			<div class="alert server-error">
				<spring:theme code="checkout.error.merchant.suite.declined"/>
			</div>
		</c:if>
		<c:if test="${not empty param.invalidCard}">
			<div class="alert server-error">
				<spring:theme code="account.error.merchant.suite.invalid.card"/>
			</div>
		</c:if>
		<c:if test="${not empty param.expiredCard}">
			<div class="alert server-error">
				<spring:theme code="account.error.merchant.suite.expired.card"/>
			</div>
		</c:if>
		<c:if test="${not empty param.noFunds}">
			<div class="alert server-error">
				<spring:theme code="account.error.merchant.suite.no.funds"/>
			</div>
		</c:if>
		<c:if test="${not empty param.gatewayError}">
			<div class="alert server-error">
				<spring:theme code="account.error.merchant.suite.gateway.error"/>
			</div>
		</c:if>
		<c:if test="${not empty param.invalidExpiry}">
			<div class="alert server-error">
				<spring:theme code="account.error.merchant.suite.invalid.expiry"/>
			</div>
		</c:if>
		<c:if test="${not empty param.invalidAccountNumber}">
			<div class="alert server-error">
				<spring:theme code="account.error.merchant.suite.invalid.account.number"/>
			</div>
		</c:if>
		<c:if test="${not empty param.invalidBsb}">
			<div class="alert server-error">
				<spring:theme code="account.error.merchant.suite.invalid.bsb"/>
			</div>
		</c:if>
		<c:if test="${not empty param.paymentError}">
			<div class="alert server-error">
				<spring:theme code="account.error.merchant.suite.gateway.error"/>
			</div>
		</c:if>


		<c:if test="${not empty param.declined}">
			<div class="alert server-error">
				<spring:theme code="account.invoice.payment.declined" />
			</div>
		</c:if>
		<c:if test="${not empty param.tokenError}">
			<div class="alert server-error">
				<spring:theme code="account.invoice.payment.tokenError" arguments="${param.tokenError}"/>
			</div>
		</c:if>
        <c:if test="${not empty param.authfailure}">
            <div class="alert server-error">
                <spring:theme code="account.invoice.payment.failed" arguments="${param.authfailure}"/>
            </div>
        </c:if>
        <c:if test="${not empty param.invalid}">
            <div class="alert server-error">
                <spring:theme code="account.invoice.payment.invalidData" />
            </div>
        </c:if>
        <c:if test="${not empty param.timeoutError}">
            <div class="alert server-error">
                <spring:theme code="account.invoice.payment.timeout" arguments="${param.timeoutError}"/>
            </div>
        </c:if>
		<div id="moreTransactions" style="display:none"><spring:theme code="text.more.transaction.message"/></div>
	</div>
	<div class="row offset-bottom-large">
		<div class="col-xs-12">
	  		<h1><spring:theme code="text.billing.payment.title" /></h1>
			<h2>
     			<c:if test="${not (customerData.isZadp and customerData.primaryAdmin)}"><spring:theme code="text.business.unit.prefix.name"/></c:if>
     			<spring:theme code="text.business.unit.name" arguments="${customerData.unit.name}"/>
			</h2>
		</div>
		
  		<div class="col-md-8 billing-options" <c:if test="${isNAPGroup}"> style="display:none" </c:if>>
				<div class="row">
					<div class="col-xs-6 col-sm-4 radio">
						<input id="openBalance" type="radio" name="amountToPay" value="openBalance" checked="checked">
						<label for="openBalance" class="h3"><spring:theme code="text.open.balance.title" /></label>
					</div>
					<div class="col-xs-6 col-sm-3  text-right">
						<span id="openBalanceValue">$0.00</span>
					</div>
				</div>
				<div class="row">
					<div class="col-xs-6 col-sm-4 radio">
						<input id="selectedAmount" type="radio" name="amountToPay" value="selectedAmount">
						<label for="selectedAmount" class="h3"><spring:theme code="text.selected.amount.title" /></label>
					</div>
					<div class="col-xs-6 col-sm-3 text-right">
						<span id="selectedAmountValue">$0.00</span>
					</div>
				</div>
				<div class="row">
					<div class="col-sm-4">
						<a href="#payment-modal" id="payment-modal-trigger" class="btn btn-primary bde-view-only"><spring:theme code="text.pay.amount" /></a>
					</div>
					<div class="col-sm-8">
						<ul class="list-inline">
							<li>
							<span><spring:theme code="text.we.accept" /></span></li>
							<li><img src="${tempImgPath}visa.png" alt="Visa"></li>
							<li><img src="${tempImgPath}mastercard.png" alt="Mastercard"></li>
							<li><img src="${tempImgPath}amex.png" alt="AMEX"></li>	
						</ul>
						<input type="hidden" id="billingCardType" value="{{cardType}}" />	
					</div>
				</div>
  			</div>
  			
		</div>
		
	
		<h2><spring:theme code="text.transactions.title" /></h2>
		<h3 style="font-weight: lighter"><spring:theme code="text.newinstruction.text"/></h3>
		<form>
			<div class="row r-row billing-filters billing-filters-well offset-bottom-medium">
				<%-- <div class="col-md-1">Show</div> --%>
				<div class="col-md-5">
					<div class="row offset-bottom-xsmall">
						<div class="col-md-2">
							<p class="label-standarized"><spring:theme code="text.dropdown.show"/></p>
					</div>
					<div class="col-md-10">
				 		<div id="billingDropdownFilter" class="select-list">
				     		<div data-value="" class="select-btn"><spring:theme code="text.open.transactions" /></div>
								<ul class="select-items">
						        <li data-value="A"><spring:theme code="text.open.transactions" /></li>
						        <li data-value="B"><spring:theme code="text.closed.transactions" /></li>
						        <li data-value="C"><spring:theme code="text.all.transactions" /></li>
			     			</ul>
						</div>
				</div>
			</div>
			<div class="row">
					<div class="col-md-2">
							<p  class="label-standarized"><spring:theme code="text.dropdown.type"/></p>
					</div>
					<div class="col-md-10 offset-bottom-xsmall">
						<div id="typeDropdownFilter" class="select-list">
				     		<div data-value="" class="select-btn" id="type"><spring:theme code="text.all.types" /></div>
							<ul class="select-items">
						        <li data-value=""><spring:theme code="text.all.types" /></li>
   						        <li data-value="<spring:theme code="text.taxinvoice.type" />"><spring:theme code="text.taxinvoice.type" /></li>
						        <li data-value="<spring:theme code="text.creditadjustment.type" />"><spring:theme code="text.creditadjustment.type.label" /></li>
			     			</ul>
						</div>
						</div>
					</div>
					
					<div class="row">
						<div class="col-md-2">
							<p  class="label-standarized"><spring:theme code="text.dropdown.for"/></p>
						</div>
						<div class="col-md-10 offset-bottom-small">
							<div id="unitDropdownFilter" class="select-list">
								<c:choose>
									<c:when test="${customerData.isZadp and customerData.primaryAdmin}">								
						     			<div data-value="" class="select-btn js-billingunit" id="forUnit"><spring:theme code="text.billing.forunit.all" /></div>
									</c:when>
									<c:otherwise>								
						     			<div data-value="${user.currentB2BUnit.uid}" class="select-btn js-billingunit" id="forUnit">${user.currentB2BUnit.name }</div>
									</c:otherwise>
								</c:choose>
								<ul class="select-items" style="max-height:900%;overflow-y:auto;">
									<c:if test="${customerData.isZadp and customerData.primaryAdmin}">

										<!-- only display the 'All' filter when there's 2 or more venue -->	
										<c:if test="${user.branches.size() > 1}">
											<li data-value="" data-text="${customerData.unit.name}"><spring:theme code="text.billing.forunit.all" /></li>
										</c:if>
									</c:if>									

									<c:forEach items="${user.branches}" var="group">
							        	<li data-value="${group.uid }" data-text="<c:if test="${customerData.isZadp and customerData.primaryAdmin}"><spring:theme code="text.business.unit.prefix.name"/></c:if>${group.name }">${group.name }</li>	
									</c:forEach>
				     			</ul>
							</div>
						</div>
					</div>
				</div>
				<div class="col-md-4 col-md-offset-1">
					  <div class="input-daterange billing-range clearfix" id="datepicker">
						<div class="row offset-bottom-xsmall">
							<div class="col-md-2">
									<label for="billingdate-start" class="label-standarized"><spring:theme code="text.from.datepicker"/></label>
							</div>
							<div class="col-md-7">
								<div class="input-icon-group white-background">
											<input type="text" class="form-control billingdate-start" data-value="" id="start" name="start" readonly="readonly" placeholder="<spring:theme code="text.from.datepicker.placeholder" />" />
											<svg class="icon-calendar icon-calendar-primary"><use xlink:href="#icon-calendar"></use></svg>
								</div>
						  </div>
					  </div>
						<div class="row">
							<div class="col-md-2">
									<label for="" class="label-standarized"><spring:theme code="text.to.datepicker"/></label>
							</div>
							<div class="col-md-7 offset-bottom-small clearfix">
				    		<div class="input-icon-group white-background">
							    <input type="text" class="form-control billingdate-end" readonly="readonly" data-value="" id="end" name="end" placeholder="<spring:theme code="text.to.datepicker.placeholder" />" />
							    <svg class="icon-calendar icon-calendar-primary">
							        <use xlink:href="#icon-calendar"></use>
							    </svg>
								</div>
							</div>
				    </div>
					</div>
					<input type="hidden" name="isCustomFilter" id="isCustomFilter" />
				</div>					
				<div class="col-md-2 ba-col clearfix offset-bottom-small">
					<a href="#" id="billingUpdateFilter" class="btn btn-primary btn-simple pull-right"><spring:theme code="text.account.profile.saveUpdates" /></a>
				</div>
			</div>
		</form>
		
		<div class="row billing-filters">
			<div class="col-md-4 col-md-offset-8 form-group">
				<div class="input-icon-group">
					<input id="billingFilter" type="text" class="form-control" placeholder="<spring:theme code="text.billing.transaction.filter" />" />
					<svg class="icon-search">
					    <use xlink:href="#icon-search"></use>
					</svg>
				</div>
			</div>
		</div>
		<div class="row">
			<div class="col-sm-8">
				<div class="pagination hide-if-no-paging"></div>
			</div>
			<div class="col-sm-4 num-rows">
				<ul class="list-inline">
					<li><spring:theme code="text.show.title" /></li>
					<li data-value="<spring:theme code="text.num.rows.min" />" class="option active"><spring:theme code="text.num.rows.min" /></li>
					<li data-value="<spring:theme code="text.num.rows.med" />" class="option"><spring:theme code="text.num.rows.med" /></li>
					<li data-value=<spring:theme code="text.num.rows.all.value" /> class="option"><spring:theme code="text.num.rows.all.label" /></li>
				</ul>
			</div>
		</div>
		<div class="row">
			<div class="col-sm-12 no-overflow-x">
				<c:url var="invoicesListUrl" value="/your-business/billing/invoices" />
				<table id="seller-table" class="table sortable sortable-json billing-table" data-page-navigation=".pagination" data-filter-text-only="true" data-filter="#billingFilter" data-filter-minimum="1" data-url='${invoicesListUrl}' data-page-size="25" data-limit-navigation="3">
					<thead>
						<tr>
							<th data-toggle="true" data-sort-ignore="true">
								<div class="checkbox">
									<input id="selectAllBilling" type="checkbox" name="billing" value="selectAllBilling">
									<label for="selectAllBilling"></label>
								</div>
							</th>
							<th data-toggle="true" data-hide="phone,tablet"><spring:theme code="text.transaction.table.doc.no" /></th>
							<th data-type="numeric" data-toggle="true"><spring:theme code="text.transaction.table.open.amount" /></th>
							<th data-toggle="true"><spring:theme code="text.transaction.table.po.no" /></th>
							<th data-toggle="true" data-sort-initial="true" data-type="numeric" data-hide="phone"><spring:theme code="text.transaction.table.date" /></th>
							<th data-toggle="true" data-type="numeric" data-hide="phone"><spring:theme code="text.transaction.table.due.date" /></th>
							<th data-toggle="true" data-hide="phone,tablet"><spring:theme code="text.transaction.table.branch" /></th>
							<th data-toggle="true" data-hide="phone,tablet"><spring:theme code="text.transaction.table.status" /></th>
							<th data-toggle="true" data-hide="phone,tablet"><spring:theme code="text.transaction.table.type" /></th>
							<th data-toggle="true" data-hide="phone,tablet"><spring:theme code="text.transaction.table.order.no" /></th>
							<th data-toggle="true" data-hide="phone,tablet" data-sort-ignore="true"><spring:theme code="text.transaction.table.actions" /></th>
						</tr>
					</thead>
					<tbody>
					</tbody>
				</table>
				<div class="pagination pagination-bottom hide-if-no-paging"></div>

				<div class="row">
			      	<div class="col-md-2">
			      	<c:url var="emailInvoicesUrl" value="/your-business/billing/invoices/email" />

			      		<a href="#" class="btn btn-primary disabled bde-view-only" id="email-invoices" data-url='${emailInvoicesUrl}'><spring:theme code="myaccount.invoices.email.me" /></a>
			      	</div>
			      	<div class="col-md-6 col-md-offset-4 footer-note">
			      		<span><spring:theme code="text.common.star" /></span><spring:theme code="text.billing.supporting.text2" />
			      	</div>
				</div>
			</div>
		</div>

 
	<div class="price-popup mfp-hide" id="mail-invoices-success">
    <h2 class="h1"><spring:theme code="myaccount.invoices.emailed.success.popup.title"/></h2>
    <p><spring:theme code="myaccount.invoices.emailed.success.popup.description"/></p>
   </div>



	<hr />

	<sec:authorize access="hasAnyRole('ROLE_B2BADMINGROUP,ROLE_B2BINVOICECUSTOMER')">
		<c:if test="${isInvoiceDiscrepancyEnabled}">
			<cms:pageSlot position="InvoiceDiscrepancyContentSlot" var="feature" element="div">
				<cms:component component="${feature}"/>
			</cms:pageSlot>
		</c:if>
	</sec:authorize>

	<cms:pageSlot position="BottomContentSlot" var="feature" element="div">
		<cms:component component="${feature}"/>
	</cms:pageSlot>
	</div>
	<nav:backToTop/>


    <popup:billingPaymentPopup />

	<popup:merchantServiceFeePopup isInvoice="true" paymentAmount="${paymentAmount}"/>
	<popup:MSFPopup isInvoice="true" />
</template:page>
