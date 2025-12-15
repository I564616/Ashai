<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template" %>

<%-- JS configuration --%>
	<script type="text/javascript">
		/*<![CDATA[*/
		<%-- Define a javascript variable to hold the content path --%>
		var ACC = { config: {} };
			ACC.config.contextPath = "${contextPath}";
			ACC.config.encodedContextPath = "${encodedContextPath}";
			ACC.config.commonResourcePath = "${commonResourcePath}";
			ACC.config.themeResourcePath = "${themeResourcePath}";
			ACC.config.siteResourcePath = "${siteResourcePath}";
			ACC.config.rootPath = "${siteRootUrl}";	
			ACC.config.CSRFToken = "${CSRFToken.token}";
			ACC.pwdStrengthVeryWeak = '<spring:theme code="password.strength.veryweak" />';
			ACC.pwdStrengthWeak = '<spring:theme code="password.strength.weak" />';
			ACC.pwdStrengthMedium = '<spring:theme code="password.strength.medium" />';
			ACC.pwdStrengthStrong = '<spring:theme code="password.strength.strong" />';
			ACC.pwdStrengthVeryStrong = '<spring:theme code="password.strength.verystrong" />';
			ACC.pwdStrengthUnsafePwd = '<spring:theme code="password.strength.unsafepwd" />';
			ACC.pwdStrengthTooShortPwd = '<spring:theme code="password.strength.tooshortpwd" />';
			ACC.pwdStrengthMinCharText = '<spring:theme code="password.strength.minchartext"/>';
			ACC.accessibilityLoading = '<spring:theme code="aria.pickupinstore.loading"/>';
			ACC.accessibilityStoresLoaded = '<spring:theme code="aria.pickupinstore.storesloaded"/>';
			ACC.config.googleApiKey="${googleApiKey}";
			ACC.config.googleApiVersion="${googleApiVersion}";
			ACC.formValidationMsg 			= '<spring:theme code="text.invoice.payment.error.general.msg"/>';
			ACC.invoiceAmtValidationMsg    = '<spring:theme code="text.invoice.payment.error.invoice.amount.msg"/>';
			ACC.creditAmtValidationMsg     = '<spring:theme code="text.invoice.payment.error.credit.amount.msg"/>';
			ACC.invoiceCreditOnlyMsg     	= '<spring:theme code="text.invoice.payment.error.invoice.credit.only.msg"/>';
			ACC.invoiceTotalMsg     	= '<spring:theme code="text.invoice.payment.error.total.amount.msg"/>';
			ACC.noInvoicesReturnedMsg  = '<spring:theme code="text.invoice.no.invoices.available"/>';
			ACC.invoiceTabledocNoMobile = '<spring:theme code="text.invoices.credits.table.heading.docno.mobile" />';
			ACC.invoiceTabledelNoMobile = '<spring:theme code="text.invoices.credits.table.heading.delno.mobile" />';
			ACC.invoiceTablesoldToMobile = '<spring:theme code="text.invoices.credits.table.heading.soldto.mobile" />';
			ACC.invoiceTabledueDateMobile = '<spring:theme code="text.invoices.credits.table.heading.duedate.mobile" />';
			ACC.invoiceTableoverDue = '<spring:theme code="text.invoices.credits.overdue" />';
			ACC.invoiceTablePaymentPending = '<spring:theme code="text.invoices.payment.update.pending.from.ecc" />';
			ACC.invoiceTabledocTypeMobile = '<spring:theme code="text.invoices.credits.table.heading.doctype.mobile" />';
			ACC.invoiceTableremainingMobile = '<spring:theme code="text.invoices.credits.table.heading.remaining.mobile" />';
			ACC.paymentHistoryTableDocNo = '<spring:theme code="text.payment.history.view.more.table.doc.no" />';
			ACC.paymentHistoryTableDocType = '<spring:theme code="text.payment.history.view.more.table.doc.type" />';
			ACC.paymentHistoryTableAmountPaid = '<spring:theme code="text.payment.history.view.more.table.doc.amount.paid" />';
			ACC.directDebitErrorMessageAccName = '<spring:theme code="text.invoices.direct.debit.form.accname.error.msg" />';
			ACC.directDebitErrorMessageAccName32Character= '<spring:theme code="text.invoices.direct.debit.form.accname.charactor.error.msg" />';
			ACC.directDebitErrorMessageBSB = '<spring:theme code="text.invoices.direct.debit.form.accbsb.error.msg" />';
			ACC.directDebitErrorMessageSuburb = '<spring:theme code="text.invoices.direct.debit.form.suburb.error.msg" />';
			ACC.directDebitErrorMessageState = '<spring:theme code="text.invoices.direct.debit.form.state.error.msg" />';
			ACC.directDebitErrorMessageName = '<spring:theme code="text.invoices.direct.debit.form.name.error.msg" />';
			ACC.directDebitErrorMessageTandC = '<spring:theme code="text.invoices.direct.debit.form.tandc.error.msg" />';
			ACC.directDebitErrorMessageAccNum = '<spring:theme code="text.invoices.direct.debit.form.accnumber.error.msg" />';
			ACC.checkoutNoDatesErrorMessage = '<spring:theme code="checkout.no.delivery.dates.error.message" />';
			ACC.cartCallCreditBlockErrorMessage = '<spring:theme code="cart.call.credit.block.error.message" />';
			ACC.cartCallFailureErrorMessage = '<spring:theme code="cart.call.failure.error.message" />';
			ACC.globalErrorMessage = '<spring:theme code="form.global.error" />';
            ACC.invoiceDocSelectionErrMsgPrefix = '<spring:theme code="text.invoice.documents.selection.error.msg.prefix" />';
            ACC.invoiceDocSelectionErrMsgSuffix = '<spring:theme code="text.invoice.documents.selection.error.msg.suffix" />';
            ACC.referencedDocNotExistsErrMsg = '<spring:theme code="text.reference.document.not.exist.error.msg" />';

			<c:if test="${request.secure}"><c:url value="/search/autocompleteSecure"  var="autocompleteUrl"/></c:if>
			<c:if test="${not request.secure}"><c:url value="/search/autocomplete"  var="autocompleteUrl"/></c:if>
			ACC.autocompleteUrl = '${autocompleteUrl}';

			<c:url value="/login" var="loginUrl"/>
			ACC.config.loginUrl = '${loginUrl}';

			<c:url value="/authentication/status" var="authenticationStatusUrl"/>
			ACC.config.authenticationStatusUrl = '${authenticationStatusUrl}';

			<c:forEach var="jsVar" items="${jsVariables}">
				<c:if test="${not empty jsVar.qualifier}" >
					ACC.${jsVar.qualifier} = '${jsVar.value}';
				</c:if>
			</c:forEach>
		/*]]>*/
	</script>
	<template:javaScriptAddOnsVariables/>
	
	<%-- generated variables from commonVariables.properties --%>
	<script type="text/javascript" src="${sharedResourcePath}/js/generatedVariables.js"></script>