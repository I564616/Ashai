<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="fn" uri="jakarta.tags.functions"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template"%>
<%@ taglib prefix="user" tagdir="/WEB-INF/tags/responsive/user"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="formElement"
	tagdir="/WEB-INF/tags/responsive/formElement"%>

<template:page pageTitle="${pageTitle}">

	<input type="hidden" name="mobileRegexPattern" value="${mobileRegexPattern}" />
	<input type="hidden" name="emailRegexPattern" value="${emailRegexPattern}" />
	<input type="hidden" name="abnRegexPattern" value="${abnRegexPattern}" />
	
	<p id="topErrorMsg" class="alert alert-danger alert-dismissable" style="display: none"><spring:theme code="form.global.error" /></p>
	<div class="user-register__headline">
		<spring:theme code="company.detail.heading" />

	</div>
	<p>
		<c:choose>
			<c:when test="${cmsSite.uid ne 'sga'}">
				<spring:theme code="company.detail.heading.content" />
			</c:when>
			<c:otherwise>
				<spring:theme code="sga.company.detail.heading.content" />
			</c:otherwise>
		</c:choose>
	</p>
	<br>
	<p>
		<c:if test="${cmsSite.uid ne 'sga'}">
		<spring:theme code="company.details.page.mandatory.field" />
		</c:if>
	</p>
	 	
	<!-------------------- ACCOUNT INFORMATION -------------------------------------->
	<div class="row form-row-margin">
        <div class="checkout_subheading row-margin-fix">
            <spring:theme code="company.detail.information" />
        </div>
    </div>
    
	<c:url value="/company/company-details" var="accountDetails" />
	<form:form modelAttribute="apbCompanyDetailsForm" method="post" 
		action="${accountDetails}">
<c:choose>
	<c:when test="${cmsSite.uid ne 'sga'}">
		<div class="row">
			<div class="col-md-4 col-sm-6">
				<formElement:formInputBox idKey="accountNumber" maxlength="${inputMaxSize}" labelKey="company.detail.accountNumber" path="accountNumber" inputCSS="form-control" mandatory="true" />
				<span class="form-group has-error" id="accountNumberError"></span>
				<p class="accountNumber company-error">
					<spring:theme code="company.detail.accountNumber.invalid" />
				</p>
			</div>
		</div>

		<div class="row">
			<div class="col-md-4 col-sm-6">
				<formElement:formInputBox idKey="acccountName" maxlength="${inputMaxSize}" labelKey="company.detail.acccountName" path="acccountName" inputCSS="form-control" mandatory="true" />
				<span class="form-group has-error" id="acccountNameError"></span>
				<p class="acccountName company-error">
					<spring:theme code="company.detail.accountName.invalid" />
				</p>
			</div>
		</div>

		<div class="row">
			<div class="col-md-4 col-sm-6">
				<formElement:formInputBox idKey="tradingName" maxlength="${inputMaxSize}" labelKey="company.detail.tradingName" path="tradingName" inputCSS="form-control" mandatory="true" />
				<span class="form-group has-error" id="tradingNameError"></span>
				<p class="tradingName company-error">
					<spring:theme code="company.detail.tradingName.invalid" />
				</p>
			</div>

		</div>


		<div class="row">
			<div class="col-md-4 col-sm-6">
				<formElement:formInputBox idKey="abn" labelKey="company.detail.abn" path="abn" maxlength="${abnMaxSize}" inputCSS="form-control" mandatory="true" />
				<p class="abn company-error">
					<spring:theme code="company.detail.abn.invalid" />
				</p>
			</div>
		</div>

		<div class="row">
			<div class="col-md-4 col-sm-6">
				<formElement:formInputBox idKey="liquorLicense" labelKey="company.detail.liquorLicense" path="liquorLicense" maxlength="${llMaxSize}" inputCSS="form-control" mandatory="false" />
			</div>
		</div>

		<div class="row">
			<div class="col-md-4 col-sm-6">
				<formElement:formInputBox idKey="companyBillingAddress" labelKey="company.detail.companyBillingAddress" path="companyBillingAddress" inputCSS="form-control" maxlength="${inputMaxSize}" mandatory="true" />
				<p class="companyBillingAddress company-error">
					<spring:theme code="company.detail.companyBillingAddress.invalid" />
				</p>
			</div>
		</div>
		<!---------------------------- INVOICE ADDRESS ------------------------------------->
		<div class="row form-row-margin">
			<div class="checkout_subheading row-margin-fix">
				<spring:theme code="company.detail.contact.details" />
			</div>
		</div>

		<div class="row">
			<div class="col-md-4 col-sm-6">
				<formElement:formInputBox idKey="companyPhone" labelKey="company.detail.companyPhone" path="companyPhone" inputCSS="form-control" mandatory="false" maxlength="${phoneMaxSize}" />
				<p class="companyPhone company-error">
					<spring:theme code="register.request.phoneNoInvoice.invalid" />
				</p>
			</div>
		</div>

		<div class="row">
			<div class="col-md-4 col-sm-6">
				<formElement:formInputBox idKey="companyMobilePhone" labelKey="company.detail.companyMobilePhone" path="companyMobilePhone" maxlength="${phoneMaxSize}" inputCSS="form-control" mandatory="false" />
				<p class="mobilePhoneError company-error">
					<spring:theme code="register.request.phoneNoInvoice.invalid" />
				</p>

			</div>
		</div>

		<div class="row">
			<div class="col-md-4 col-sm-6">
				<formElement:formInputBox idKey="companyFax" labelKey="company.detail.companyFax" path="companyFax" inputCSS="form-control" mandatory="false" maxlength="${phoneMaxSize}" />
				<p class="companyFaxError company-error">
					<spring:theme code="register.request.phoneNoInvoice.invalid" />
				</p>
			</div>
		</div>
		<div class="row">
			<div class="col-md-4 col-sm-6">
			    <spring:message code="company.detail.companyEmailAddress" arguments="${apbValidEmailSeparator}" var="emailAddressMessage" htmlEscape="false" argumentSeparator="|" />
				<span class="company-label"><spring:theme code="company.detail.companyEmailAddress1" /></span>
				<span class="company-email"> <formElement:formInputBox idKey="companyEmailAddress"
						labelKey="${emailAddressMessage}"
						path="companyEmailAddress" maxlength="${emailMaxSize}"
						inputCSS="form-control" mandatory="true" />
						<input name="apbValidEmailSeparator" type="hidden" id="emailSeparator" value="${apbValidEmailSeparator}" />
                        <p class="invalid-separator hidden"><span class="error-message">Please enter valid separator. </span></p>
						<p class="errorCompanyEmailAddress company-error"><spring:theme code="register.request.emailAddress.invalid" /></p>
				</span>
			</div>
		</div>
		<!---------------------------- DEFAULT DELIVERY ADDRESS ------------------------------------->
		<c:set var="checkDelAddress" value="No" />
		<c:set var="isDefaultDeliveryAddress" value="No" />
		<c:forEach items="${apbCompanyDetailsForm.b2bUnitDeliveryAddressDataList}" var="company" varStatus="statusComp">
			<c:forEach items="${company.deliveryAddresses}" var="item" varStatus="status">
				<c:set var="checkDelAddress" value="Yes" />
				<c:if test="${item.defaultAddress}">
					<c:set var="isDefaultDeliveryAddress" value="Yes" />
				</c:if>
			</c:forEach>
		</c:forEach>
		<c:if test="${isDefaultDeliveryAddress eq 'Yes'}">

			<div class="row form-row-margin">
				<div class="checkout_subheading row-margin-fix">
					<spring:theme code="company.detail.default.delivery.address" />
				</div>
			</div>

			<div class="row">
				<div class="col-md-4 col-sm-6 company-remove-spacing">
					<formElement:formCheckbox idKey="sameasInvoiceAddress" labelKey="company.detail.sameasInvoiceAddress" path="sameasInvoiceAddress" />
				</div>

			</div>

		</c:if>


		<!-- If Delivery Address Existing -->
		<div class="add-delivery-address">
			<user:companyDeliveryAddress/>

			<!-- If Delivery Address Not Existing -->
			<c:set var="checkDelAddress" value="No" />
			<c:forEach items="${apbCompanyDetailsForm.b2bUnitDeliveryAddressDataList}" var="company" varStatus="statusComp">
				<c:forEach items="${company.deliveryAddresses}" var="item" varStatus="status">

					<c:set var="checkDelAddress" value="Yes" />

				</c:forEach>
			</c:forEach>
			<c:if test="${checkDelAddress eq 'No'}">
				<user:companyAdditionalDeliveryAddress/>
			</c:if>

		</div>



		<!---------------------------- SUBMIT BUTTONS ------------------------------------->

		<div class="row">
			<div class="col-md-4 col-sm-6">
				<div class="form-actions clearfix">
					<ycommerce:testId code="register_Register_button">
						<button type="button" class="btn btn-default btn-primary btn-block noSpinnerCls" id="additional-address">
							<spring:theme code='company.detail.add.delivery.address' />
						</button>
					</ycommerce:testId>
				</div>
			</div>
			<div class="col-md-4 col-sm-6">
				<div class="form-actions clearfix">
					<ycommerce:testId code="register_Register_button">
						<button type="submit" class="btn btn-default btn-block btn-primary company-submit-btn btn-vd-primary" id="submit-change-button">
							<spring:theme code='company.detail.add.submit.changes' />
						</button>
					</ycommerce:testId>
				</div>
			</div>
		</div>
	</c:when>
	<c:otherwise>
		<div class="row label-section">
			<div class="col-md-4 col-sm-6">
				<div class="label-heading">
					<spring:theme code="company.detail.accountNumber" />
				</div>
				<c:choose>
					<c:when test="${not empty apbCompanyDetailsForm.accountNumber}">  
						${apbCompanyDetailsForm.accountNumber}
					</c:when>
					<c:otherwise>
						<spring:theme code="sga.null.value.found" />						
					</c:otherwise>
				</c:choose>
			</div>
		</div>
		
		<div class="row label-section">
			<div class="col-md-4 col-sm-6">
				<div class="label-heading">
					<spring:theme code="company.detail.acccountName" />
				</div>
				<c:choose>
					<c:when test="${not empty apbCompanyDetailsForm.acccountName}">  
						${apbCompanyDetailsForm.acccountName}
					</c:when>
					<c:otherwise>
						<spring:theme code="sga.null.value.found" />						
					</c:otherwise>
				</c:choose>
			</div>
		</div>
		
		<div class="row label-section">
			<div class="col-md-4 col-sm-6">
				<div class="label-heading">
					<spring:theme code="company.detail.tradingName" />
				</div>
				<c:choose>
					<c:when test="${not empty apbCompanyDetailsForm.tradingName}">  
						${apbCompanyDetailsForm.tradingName}
					</c:when>
					<c:otherwise>
						<spring:theme code="sga.null.value.found" />						
					</c:otherwise>
				</c:choose>
			</div>
		</div>

		<div class="row label-section">
			<div class="col-md-4 col-sm-6">
				<div class="label-heading">
					<spring:theme code="company.detail.abn" />
				</div>
				<c:choose>
					<c:when test="${not empty apbCompanyDetailsForm.abn}">  
						${apbCompanyDetailsForm.abn}
					</c:when>
					<c:otherwise>
						<spring:theme code="sga.null.value.found" />						
					</c:otherwise>
				</c:choose>
			</div>
		</div>

		<div class="row label-section">
			<div class="col-md-4 col-sm-6">
				<div class="label-heading">
					<spring:theme code="company.detail.companyBillingAddress" />
				</div>
				<c:choose>
					<c:when test="${apbCompanyDetailsForm.companyBillingAddress}">  
						${apbCompanyDetailsForm.companyBillingAddress}
					</c:when>
					<c:otherwise>
						<spring:theme code="sga.null.value.found" />						
					</c:otherwise>
				</c:choose>
			</div>
		</div>
		
		<!---------------------------- INVOICE ADDRESS ------------------------------------->
		<div class="row form-row-margin">
			<div class="checkout_subheading row-margin-fix">
				<spring:theme code="company.detail.contact.details" />
			</div>
		</div>
		
		<div class="row label-section">
			<div class="col-md-4 col-sm-6">
				<div class="label-heading">
					<spring:theme code="company.detail.companyPhone" />
				</div>
				<c:choose>
					<c:when test="${not empty apbCompanyDetailsForm.companyPhone}">  
						${apbCompanyDetailsForm.companyPhone}
					</c:when>
					<c:otherwise>
						<spring:theme code="sga.null.value.found" />						
					</c:otherwise>
				</c:choose>
			</div>
		</div>

		<div class="row label-section">
			<div class="col-md-4 col-sm-6">
				<div class="label-heading">
					<spring:theme code="company.detail.companyMobilePhone" />
				</div>
				<c:choose>
					<c:when test="${not empty apbCompanyDetailsForm.companyMobilePhone}">  
						${apbCompanyDetailsForm.companyMobilePhone}
					</c:when>
					<c:otherwise>
						<spring:theme code="sga.null.value.found" />						
					</c:otherwise>
				</c:choose>
			</div>
		</div>
		
		<div class="row label-section">
			<div class="col-md-4 col-sm-6">
				<div class="label-heading">
					<spring:theme code="company.detail.companyFax" />
				</div>
				<c:choose>
					<c:when test="${not empty apbCompanyDetailsForm.companyFax}">  
						${apbCompanyDetailsForm.companyFax}
					</c:when>
					<c:otherwise>
						<spring:theme code="sga.null.value.found" />						
					</c:otherwise>
				</c:choose>
			</div>
		</div>		
		
		<!----------- Hidden for ALB Connect currently. @SM SCP-2414
		<div class="row label-section">
			<div class="col-md-4 col-sm-6">
				<div class="label-heading">
					<spring:theme code="company.detail.companyEmailAddress1" /><br>
					<span class="label-subheading"><i><spring:theme code="company.detail.companyEmailAddress" /></i></span>
				</div>
				<c:choose>
					<c:when test="${not empty apbCompanyDetailsForm.companyEmailAddress}">  
						${apbCompanyDetailsForm.companyEmailAddress}
					</c:when>
					<c:otherwise>
						<spring:theme code="sga.null.value.found" />						
					</c:otherwise>
				</c:choose>
			</div>
		</div>
		---------------->
		
		<!---------------------------- DEFAULT DELIVERY ADDRESS ------------------------------------->
		<c:set var="checkDelAddress" value="No" />
		<c:set var="isDefaultDeliveryAddress" value="No" />
		<c:forEach items="${apbCompanyDetailsForm.b2bUnitDeliveryAddressDataList}" var="company" varStatus="statusComp">
			<c:forEach items="${company.deliveryAddresses}" var="item" varStatus="status">
				<c:set var="checkDelAddress" value="Yes" />
				<c:if test="${item.defaultAddress}">
					<c:set var="isDefaultDeliveryAddress" value="Yes" />
				</c:if>
			</c:forEach>
		</c:forEach>
		<c:if test="${isDefaultDeliveryAddress eq 'Yes'}">

			<div class="row form-row-margin">
				<div class="checkout_subheading row-margin-fix">
					<spring:theme code="company.detail.default.delivery.address" />
				</div>
			</div>

			<div class="row">
				<div class="col-md-4 col-sm-6 company-remove-spacing">
					<c:if test="${apbCompanyDetailsForm.sameasInvoiceAddress}">
						<spring:theme code="company.detail.sameasInvoiceAddress" />
					</c:if>
				</div>
			</div>

		</c:if>


		<!-- If Delivery Address Existing -->
		<div class="add-delivery-address">
			<user:companyDeliveryAddress/>

			<!-- If Delivery Address Not Existing -->
			<c:set var="checkDelAddress" value="No" />
			<c:forEach items="${apbCompanyDetailsForm.b2bUnitDeliveryAddressDataList}" var="company" varStatus="statusComp">
				<c:forEach items="${company.deliveryAddresses}" var="item" varStatus="status">

					<c:set var="checkDelAddress" value="Yes" />

				</c:forEach>
			</c:forEach>
			<c:if test="${checkDelAddress eq 'No'}">
				<user:companyAdditionalDeliveryAddress/>
			</c:if>

		</div>

	</c:otherwise>
</c:choose>
	</form:form>
</template:page>