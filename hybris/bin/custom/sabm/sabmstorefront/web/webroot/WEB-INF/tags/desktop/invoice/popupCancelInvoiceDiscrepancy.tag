<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>

<div id="cancelInvoiceDiscrepancyPopup" class="invoice-discrepancy-popup mfp-hide">
	<div class="legend">
		<h2 class="h1 offset-bottom-small"><spring:theme code="text.invoicediscrepancy.cancel" /></h2>
		<p class="offset-bottom-small"><spring:theme code="text.invoicediscrepancy.cancel.desc" /></p>
		
		<a href="javascript:void(0)" ng-hide="widthSize < mobileSize" class="btn btn-primary btn-link margin-top-30" onclick="$.magnificPopup.close()"><spring:theme code="text.invoicediscrepancy.popup.back" /></a>
		<button class="btn btn-primary btn-goBack margin-top-30" ng-click="cancelForm()"><spring:theme code="text.invoicediscrepancy.popup.leavethispage" /></button>
		<a href="javascript:void(0)" ng-show="widthSize < mobileSize" class="btn btn-primary btn-link margin-top-30" onclick="$.magnificPopup.close()"><spring:theme code="text.invoicediscrepancy.popup.back" /></a>
		<div class="clearfix"></div>
	</div>
</div>