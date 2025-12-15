<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>

<div id="invoiceDiscrepancyAlreadyRaisedPopup" class="invoice-discrepancy-popup mfp-hide">
	<div class="legend">
		<h2 class="h1 offset-bottom-small"><spring:theme code="text.invoicediscrepancy.alreadyRaised" /></h2>
		<p class="offset-bottom-small"><spring:theme code="text.invoicediscrepancy.alreadyRaised.desc" arguments="{{invoiceNumber}}, ${user.firstName} ${user.lastName}" />
		<a href="/your-business/raisedinvoicediscrepancy"><spring:theme code="text.invoicediscrepancy.link1" /></a>.</p>

		<p class="offset-bottom-small"><spring:theme code="text.invoicediscrepancy.alreadyRaised.areyousure" /></p>

		<button class="btn btn-primary btn-link margin-top-10" ng-hide="widthSize < mobileSize" onclick="$.magnificPopup.close()"><spring:theme code="text.invoicediscrepancy.popup.back" /></button>
		<button class="btn btn-primary btn-goBack margin-top-10" data-title="<spring:theme code="text.invoicediscrepancy.step2.process.title" arguments="{{typeOfIssue}}" />" ng-click="getInvoiceItemDataFromPopup($event)"><spring:theme code="text.invoicediscrepancy.popup.continutonextstep" /></button>
		<button class="btn btn-primary btn-link margin-top-10" ng-show="widthSize < mobileSize" onclick="$.magnificPopup.close()"><spring:theme code="text.invoicediscrepancy.popup.back" /></button>
		<div class="clearfix"></div>
	</div>
</div>