<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="formElement" tagdir="/WEB-INF/tags/desktop/formElement" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="invoice" tagdir="/WEB-INF/tags/desktop/invoice"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<c:url var="invoiceValidationUrl" value="/your-business/validateInvoice" />
<c:url var="invoiceItemDataUrl" value="/your-business/getInvoiceItemData" />

<input type="hidden" id="invoiceValidationUrl" value="${invoiceValidationUrl}" />
<input type="hidden" id="invoiceItemDataUrl" value="${invoiceItemDataUrl}" />
<input type="hidden" id="currentB2BUnitUID" value="${user.currentB2BUnit.uid }">

<input type="hidden" value="<spring:theme code="text.invoicediscrepancy.step1.process.title"/>" id="stepOneProcessTitle" />
<c:choose>
	<c:when test="${bdeUser}">
		<c:set var="notificationListUrl" value="getCustomerListForB2BUnitToReceiveInvoiceDiscrepancyNotification_BDE" />
	</c:when>
	<c:otherwise>
		<c:set var="notificationListUrl" value="getCustomerListForB2BUnitToReceiveInvoiceDiscrepancyNotification" />
	</c:otherwise>
</c:choose>

<input type="hidden" id="isBDEUserEnabled" value="${bdeUser}" />

<input type="hidden" id="notificationListUrl" value="${notificationListUrl}" />

<%--<script id="invoiceList">${invoiceList.invoices}</script>--%>

<script id="invoiceList" type="text/json">${ycommerce:generateJson(invoiceList.invoices)}</script>


<div id="invoice-discrepancy" class="invoice-discrepancy" ng-controller="invoiceCtrl" ng-cloak>
	<section ng-show="!isInvoiceNumberDiscrepancySent">
		<h1 class="invoice-discrepancy-header"><spring:theme code="text.invoicediscrepancy.header" /></h1>
		<p class="invoice-discrepancy-desc">
			<spring:theme code="text.invoicediscrepancy.description" /> 
		</p>
		
		<div id="follow-steps-container">
			<div id="follow-steps-header">
				<h3><spring:theme code="text.invoicediscrepancy.followsteps" /></h3>
				<div class="follow-steps-track">
					<div class="step-1" ng-class="{'current': currentStep == 1, 'active': currentStep > 1 }">
						<span class="circle">1</span>
						<p class="text"><spring:theme code="text.invoicediscrepancy.discrepancytype" /></p>
						<span class="hr-line"></span>
					</div>
					<div class="step-2" ng-class="{'current': currentStep == 2, 'active': currentStep > 2 }">
						<span class="circle">2</span>
						<p class="text"><spring:theme code="text.invoicediscrepancy.details" /></p>
						<span class="hr-line"></span>
					</div>
					<div class="step-3"  ng-class="{'current': currentStep == 3, 'active': currentStep > 3}">
						<span class="circle">3</span>
						<p class="text"><spring:theme code="text.invoicediscrepancy.notification" /></p>
					</div>
				</div>
			</div>
			<div id="follow-steps-body">
				<section ng-class="{'active': currentStep == 1}">
					<h3><spring:theme code="text.invoicediscrepancy.followsteps.step1"/></h3>
					<p class="follow-step-desc"><spring:theme code="text.invoicediscrepancy.followsteps.step1.desc"/></p>
					
					<invoice:stepOneForm/>
				</section>
				<section ng-class="{'noMargin': widthSize < mobileSize, 'active': currentStep == 2}">
					<h3><spring:theme code="text.invoicediscrepancy.followsteps.step2"/></h3>
					<p  class="follow-step-desc" ng-show="typeOfIssue == 'Price Discrepancy'"><spring:theme code="text.invoicediscrepancy.followsteps.step2.price.desc"/></p>
					<p  class="follow-step-desc" ng-show="typeOfIssue == 'Freight Discrepancy'"><spring:theme code="text.invoicediscrepancy.followsteps.step2.freight.desc"/></p>
					
					<invoice:stepTwoForm/>
				</section>
				<section ng-class="{'noMargin': widthSize < mobileSize, 'active': currentStep == 3}">
					<h3><spring:theme code="text.invoicediscrepancy.followsteps.step3"/></h3>
					
					<c:if test="${bdeUser}">
						<p class="follow-step-desc"><spring:theme code="text.invoicediscrepancy.followsteps.step3.desc"/></p>
					</c:if>

					<div class="alert alert-danger" ng-show="showInvoiceNumberDiscrepancySentError"><spring:theme code="text.invoicediscrepancy.error"/></div>

					<p class="h3 select-users-below">
						<spring:theme code="text.invoicediscrepancy.selectusersbelow" /> 
						<i class="icon icon-price-info" rel="tooltip" title="<spring:theme code='text.invoicediscrepancy.selectusersbelow.tooltip' />"></i>
					</p>
					<invoice:stepThreeForm/>
				</section>
			</div>
			
			<div id="follow-steps-footer">
				<div id="button-component">
					<button class="btn btn-primary" ng-click="goToPrevStep()" ng-hide="currentStep == 1"><spring:theme code="text.invoicediscrepancy.btn.previousstep" /></button>
					<button class="btn btn-primary" data-title="<spring:theme code="text.invoicediscrepancy.step2.process.title" arguments="{{typeOfIssue}}" />" ng-disabled="!isBtn1Enabled" ng-hide="currentStep != 1" ng-click="goToNextStep($event);"><spring:theme code="text.invoicediscrepancy.btn.nextstep" /></button>
					<button class="btn btn-primary" data-title="<spring:theme code="text.invoicediscrepancy.step3.process.title" arguments="{{typeOfIssue}}" />" ng-disabled="!isBtn2Enabled" ng-hide="currentStep != 2" ng-click="goToNextStep($event);"><spring:theme code="text.invoicediscrepancy.btn.nextstep" /></button>
					<button class="btn btn-primary" data-title="<spring:theme code="text.invoicediscrepancy.confirmation.process.title" arguments="{{typeOfIssue}}" />" ng-disabled="!isBtn3Enabled" ng-hide="currentStep != 3" ng-click="goToNextStep($event);"><spring:theme code="text.invoicediscrepancy.btn.send" /></button>
				</div>
				
				<a href="/your-business/billing" class="cancel-invoice-discrepancy" ng-click="showCancelInvoiceDiscrepancyPopup($event)"><spring:theme code="text.invoicediscrepancy.cancel" /></a>
			</div>		
		</div>
		
		<invoice:popupCancelInvoiceDiscrepancy/>
		<invoice:popupDiscrepancyAlreadyRaised/>
	</section>
	
	<section ng-show="isInvoiceNumberDiscrepancySent">
		<invoice:invoiceDiscrepancySent/>
	</section>
</div>
