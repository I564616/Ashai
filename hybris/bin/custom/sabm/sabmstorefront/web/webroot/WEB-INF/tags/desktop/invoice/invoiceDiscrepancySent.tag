<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>

<div id="invoiceDiscrepancySent">

	<svg class="icon-email-confirmation">
	    <use xlink:href="#icon-email-confirmation"></use>    
	</svg>
	<h1><spring:theme code="text.invoicediscrepancy.sent" /></h1>
	
	<p><spring:theme code="text.invoicediscrepancy.process" /></p>
	
	<p>
		<spring:theme code="text.invoicediscrepancy.emailconfirmation1" />
		<a href="/your-business/raisedinvoicediscrepancy"><spring:theme code="text.invoicediscrepancy.link" /></a>
		<spring:theme code="text.invoicediscrepancy.emailconfirmation2" />
	</p>
	
	<p>
		<spring:theme code="text.invoicediscrepancy.sms.notification1" />
		<a href="/your-notifications"><spring:theme code="text.invoicediscrepancy.manageyournotifications" /></a>
		<spring:theme code="text.invoicediscrepancy.sms.notification2" />
	</p>
	
	<a href="/sabmStore/en" class="btn btn-primary"><spring:theme code="text.invoicediscrepancy.gobackhome" /></a>
</div>