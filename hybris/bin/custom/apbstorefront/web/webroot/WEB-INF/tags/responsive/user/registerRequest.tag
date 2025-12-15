<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ attribute name="actionNameKey" required="true"
	type="java.lang.String"%>
<%@ attribute name="action" required="true" type="java.lang.String"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="formElement"
	tagdir="/WEB-INF/tags/responsive/formElement"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="pdf" tagdir="/WEB-INF/tags/responsive/pdf"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format" %>

<div class="user-register__headline">
	<spring:theme code="register.become.customer" />

</div>
<p>
	<spring:theme code="register.become.customer.notification" />
	<br><br>
	<spring:theme code="register.request.mandatory.field" />
</p>
 

<!-------------------- ACCOUNT INFORMATION -------------------------------------->
    
<div class="row form-row-margin">
    <div class="checkout_subheading row-margin-fix">
        <spring:theme code="register.request.account.information" />
    </div> 
</div>

<form:form modelAttribute="apbRequestRegisterForm"  method="post" enctype="multipart/form-data"
	action="${action}" >
	<div style="display: none">
		<formElement:formCheckbox idKey="requestRegisterCustomerTypeYes"
			labelKey="requestRegisterCustomerTypeYes" path="requestCustomerType" />
	</div>
	<div class="row">
		<div class="col-md-3 col-sm-6">
			<formElement:formInputBox idKey="register.request.outletName"
				labelKey="register.request.outletName" path="outletName"
				maxlength="${inputMaxSize}" inputCSS="form-control"  mandatory="true" />
		</div>
	</div>

	<div class="row">
		<div class="col-md-3 col-sm-6">
			<formElement:formInputBox idKey="register.request.tradingName"
				labelKey="register.request.tradingName" path="tradingName" maxlength="${inputMaxSize}"
				inputCSS="form-control" mandatory="true" />
		</div>
	</div>

	<div class="row">
		<div class="col-md-3 col-sm-6">
			<formElement:formInputBox idKey="register.request.companyName"
				labelKey="register.request.companyName" path="companyName"
				maxlength="${inputMaxSize}" inputCSS="form-control"  mandatory="true" />
		</div>
	</div>
	<!---------------------------- INVOICE ADDRESS ------------------------------------->
    
    <div class="row form-row-margin">
        <div class="checkout_subheading row-margin-fix">
            <spring:theme code="register.request.invoice.address" />
        </div> 
    </div>

	<div class="row">
		<div class="col-md-3 col-sm-6">
			<div class="col-md-6 col-sm-6 no-padding">
				<formElement:formInputBox idKey="streetNumber"
					labelKey="register.request.streetNumber" path="streetNumber"
					maxlength="${inputMaxSize}" inputCSS="form-control-1"  mandatory="true" />
			</div>
		</div>

		<div class="col-md-3 col-sm-6">
			<formElement:formInputBox idKey="streetName"
				labelKey="register.request.streetName" path="streetName"
				maxlength="${inputMaxSize}" inputCSS="form-control-1"  mandatory="true" />

		</div>
	</div>

	<div class="row">
		<div class="col-md-3 col-sm-6">
			<formElement:formInputBox idKey="streetAbreviation"
				labelKey="register.request.streetAbreviation"
				path="streetAbreviation" maxlength="${inputMaxSize}" inputCSS="form-control-1"  mandatory="true" />
		</div>
        
        <div class="col-md-3 col-sm-6">
            <div class="col-md-7 col-sm-7 col-xs-7 remove-padding-left">
                <formElement:formInputBox idKey="unitNoShopNo"
                    labelKey="register.request.unitNoShopNo" path="unitNoShopNo"
                    maxlength="${inputMaxSize}" inputCSS="form-control-1"  mandatory="false" />
            </div>

            <div class="col-md-5 col-sm-5 col-xs-5 remove-padding-right">
                <formElement:formInputBox idKey="level"
                    labelKey="register.request.level" path="level"
                    maxlength="${inputMaxSize}" inputCSS="form-control-1"  mandatory="false" />
            </div>
        </div>

	</div>

	<div class="row">
		<div class="col-md-3 col-sm-6">
			<formElement:formInputBox idKey="suburb"
				labelKey="register.request.suburb" path="suburb"
				maxlength="${inputMaxSize}" inputCSS="form-control"  mandatory="true" />
		</div>
        
        <div class="col-md-3 col-sm-6">
            <div class="col-md-7 col-sm-7 col-xs-7 remove-padding-left">
               <%--  <formElement:formInputBox idKey="stateInvoice"
                    labelKey="register.request.stateInvoice" path="stateInvoice"
                    maxlength="${inputMaxSize}" inputCSS="form-control"  mandatory="true" /> --%>
               <formElement:formSelectBox idKey="stateInvoice"
						labelKey="register.request.stateInvoice" selectCSSClass="form-control"
						path="stateInvoice" mandatory="true" skipBlank="false"
						skipBlankMessageKey="form.select.empty" items="${region}" />
            </div>

            <div class="col-md-5 col-sm-5 col-xs-5 remove-padding-right">
                <formElement:formInputBox idKey="postcodeInvoice"
                    labelKey="register.request.postcodeInvoice" path="postcodeInvoice"
                    maxlength="4" inputCSS="form-control"  mandatory="true" />
            </div>
        </div>
	</div>

	<div class="row">

		<div class="col-md-3 col-sm-6">
			<formElement:formInputBox idKey="register.request.contactName"
				labelKey="register.request.contactName" path="contactName"
				maxlength="${inputMaxSize}" inputCSS="form-control"  mandatory="true" />
		</div>

		<div class="col-md-3 col-sm-6">
			<formElement:formInputBox idKey="register.request.alternateContact"
				labelKey="register.request.alternateContact" path="alternateContact"
				maxlength="${inputMaxSize}" inputCSS="form-control"  mandatory="false" />
		</div>
	</div>

	<div class="row">
		<div class="col-md-3 col-sm-6">
			<formElement:formInputBox idKey="register.request.phoneNoInvoice"
				labelKey="register.request.phoneNoInvoice" path="phoneNoInvoice"
				maxlength="${phoneMaxSize}" inputCSS="form-control"  mandatory="true" />
		</div>

		<%-- Hide the Customer Type drop down list ACP-1404
		 <div class="col-md-3 col-sm-6">
			<formElement:formSelectBox idKey="register.request.customerType"
				labelKey="register.request.customerType"
				selectCSSClass="form-control" path="customerType" mandatory="false"
				skipBlank="true" skipBlankMessageKey="form.select.empty"
				items="${customerType}" />

		</div> --%>
	</div>

	<div class="row">

		<div class="col-md-3 col-sm-6">
			<formElement:formInputBox idKey="register.request.alternativePhoneNo"
				labelKey="register.request.alternativePhoneNo"
				path="alternativePhoneNo" maxlength="${phoneMaxSize}" inputCSS="form-control"  mandatory="false" />
		</div>

		<div class="col-md-3 col-sm-6">
			<formElement:formInputBox idKey="register.request.warehouseNo"
				labelKey="register.request.warehouseNo" path="warehouseNo"
				maxlength="${inputMaxSize}" inputCSS="form-control"  mandatory="false" />
		</div>
	</div>

	<div class="row">
		<div class="col-md-3 col-sm-6">
			<formElement:formInputBox idKey="register.request.emailAddress"
				labelKey="register.request.emailAddress" path="emailAddress"
				maxlength="${emailMaxSize}" inputCSS="form-control"  mandatory="true" />
		</div>
	</div>

	<div class="row">
		<div class="col-md-3 col-sm-6">
			<formElement:formInputBox idKey="register.request.abn"
				labelKey="register.request.abn" path="abn" inputCSS="form-control" maxlength="${abnMaxSize}"
				mandatory="true" />
		</div>

		<div class="col-md-3 col-sm-6">
			<formElement:formInputBox idKey="register.request.liquorLicense"
				labelKey="register.request.liquorLicense" path="liquorLicense"
				maxlength="${llMaxSize}" inputCSS="form-control"  mandatory="false" />
		</div>
	</div>

	<div class="row">

		<div class="col-md-3 col-sm-6">
			<formElement:formInputBox idKey="register.request.acn"
				labelKey="register.request.acn" path="acn" inputCSS="form-control"
				maxlength="${acnMaxSize}" mandatory="true" />
		</div>
	</div>

	<!----------------- DELIVERY ADDRESS ---------------------->

    <div class="row form-row-margin">
        <div class="checkout_subheading row-margin-fix">
            <spring:theme code="register.request.delivery.address" />
        </div> 
    </div>

	<div class="row">
		<div class="col-md-3 col-sm-6">
			<p>
				<formElement:formCheckbox
					idKey="sameasInvoiceAddress"
					labelKey="register.request.sameasInvoiceAddress"
					path="sameasInvoiceAddress" />
				<%-- <spring:theme
					code="register.request.sameasInvoiceAddress.description" /> --%>
			</p>
		</div>
	</div>

	
	<div class="row">
		<div class="col-md-3 col-sm-6">
			<formElement:formInputBox idKey="shippingStreet"
				labelKey="register.request.shippingStreet" path="shippingStreet"
				maxlength="${inputMaxSize}" inputCSS="form-control2"  mandatory="true" />
		</div>

		<div class="col-md-3 col-sm-6">
			 <formElement:formInputBox idKey="shippingSuburb"
				labelKey="register.request.shippingSuburb" path="shippingSuburb" 
				maxlength="${inputMaxSize}" inputCSS="form-control"  mandatory="true" />
		</div>
	</div>
	
	<div class="row">
		<div class="col-md-3 col-sm-6">
			<formElement:formSelectBox idKey="stateDelivery"
				labelKey="register.request.stateDelivery" selectCSSClass="form-control"
				path="stateDelivery" mandatory="true" skipBlank="false"
				skipBlankMessageKey="form.select.empty" items="${region}" />
		</div>

		<div class="col-md-3 col-sm-6">
			 <formElement:formInputBox idKey="postcodeDelivery"
                    labelKey="register.request.postcodeDelivery" path="postcodeDelivery"
                    maxlength="4" inputCSS="form-control"  mandatory="true" />
		</div>
	</div>
	
	<div class="row">
		<div class="col-md-6 col-sm-6 col-xs-12">
				<formElement:formTextArea 
					idKey="deliveryInstructions"
					labelKey="register.request.deliveryInstructions"
					path="deliveryInstructions" areaCSS="form-control" mandatory="false" />
		</div>
	</div>

	<!--------------------------- APPLICATION FOR TRADING ACCOUNT SECTION 1 --------------------------->
    <div class="row form-row-margin">
        <div class="checkout_subheading row-margin-fix">
            <spring:theme code="register.request.application" />
        </div> 
    </div>

	<div class="row">
		<div class="col-md-12 col-sm-12">
			<p>
				<spring:theme code="register.request.applicantCarry" />
				<span id="trading-account-radio-btn">
					<form:radiobutton path="applicantCarry" id="applicantCarry"
						value="true" label="Yes" />
					&nbsp;&nbsp;
					<form:radiobutton path="applicantCarry" id="applicantCarry"
						value="false" label="No" />
				</span>
			</p>
		</div>
	</div>
	<!--  Credit Application Pdf File  -->
	<div id="applicantCarryDiv" style="display: none;">
		<div class="row">
			<div class="col-md-3 col-sm-6">
				<formElement:formInputBox idKey="register.request.trustName" labelKey="register.request.trustName" path="trustName" maxlength="${inputMaxSize}" inputCSS="form-control" mandatory="true" />
			</div>
			<div class="col-md-3 col-sm-6">
				<span class="request-control-label"><spring:theme	code="register.request.trustDeed" />	</span>
				<spring:theme code="import.pdf.file.max.size.bytes" />
				<format:bytes bytes="${pdfFileMaxSize}" />)
				<pdf:importPDFRequestRegistrationPage/>
			</div>
		</div>
		
		<div class="row">
			<div class="col-md-3 col-sm-6">
					<formElement:formInputBox idKey="register.request.trustAbn"
						labelKey="register.request.trustAbn" path="trustAbn"
						maxlength="${inputMaxSize}" inputCSS="form-control"  mandatory="true" />
			</div>
				<div class="col-md-3 col-sm-6">
						&nbsp;
			</div>
		</div>
	</div>
	<div class="row">
		<div class="col-md-3 col-sm-6">
			<formElement:formSelectBox idKey="register.request.typeofEntity"
				labelKey="register.request.typeofEntity"
				selectCSSClass="form-control" path="typeofEntity" mandatory="false"
				skipBlank="true" skipBlankMessageKey="form.select.empty"
				items="${typeofEntity}" />
		</div>

		<div class="col-md-3 col-sm-6">
			<formElement:formSelectBox idKey="register.request.typeofBusiness"
				labelKey="register.request.typeofBusiness"
				selectCSSClass="form-control" path="typeofBusiness"
				mandatory="false" skipBlank="true"
				skipBlankMessageKey="form.select.empty" items="${typeofBusiness}" />
		</div>
	</div>

	<div class="row">
		<div class="col-md-3 col-sm-6">
		<label class="control-label" for="register.request.licensedPremisesAddress">
				<spring:theme code="register.request.licensedPremisesAddress"/>
			</label>
			<span id="optional-text"><spring:theme code="register.request.licensedPremisesAddress.optional"/></span>
			<div>
				<formElement:formCheckbox idKey="sameasDeliveryAddressLPA"
					labelKey="register.request.same.as.delivery.address"
					path="sameasDeliveryAddressLPA" inputCSS="register-sameasinvoice"/>
			
			<formElement:formInputBox
				idKey="licensedPremisesAddress"
				labelKey=""
				path="licensedPremisesAddress" inputCSS="form-control register-lpa"
				maxlength="${inputMaxSize}" mandatory="true" />
				</div>
		</div>
	</div>


	<div class="row">
		<div class="col-md-3 col-sm-6">
			<formElement:formInputBox
				idKey="register.request.dateBusinessEstablished"
				labelKey="register.request.dateBusinessEstablished"
				path="dateBusinessEstablished" maxlength="${dateMaxSize}" inputCSS="form-control"  placeholder="dd/mm/yyyy"
				mandatory="false" />
		</div>
		<div class="col-md-3 col-sm-6">
			<formElement:formInputBox idKey="register.request.licensee"
				labelKey="register.request.licensee" path="licensee"
				maxlength="${inputMaxSize}" inputCSS="form-control"  mandatory="false" />
		</div>  
	</div>

	<div class="row">
		<div class="col-md-3 col-sm-6">
			<formElement:formInputBox
				idKey="register.request.dateandExpiryofLiquorLicense"
				labelKey="register.request.dateandExpiryofLiquorLicense"
				path="dateandExpiryofLiquorLicense" inputCSS="form-control" placeholder="dd/mm/yyyy"
				maxlength="${dateMaxSize}" mandatory="false" />
		</div>
		<div class="col-md-3 col-sm-6">
			<formElement:formInputBox idKey="register.request.bannerGroup"
				labelKey="register.request.bannerGroup" path="bannerGroup"
				maxlength="${inputMaxSize}" inputCSS="form-control"  mandatory="false" />
		</div>
	</div>

	<div class="row">
		<div class="col-md-3 col-sm-6">
			<formElement:formInputBox idKey="register.request.purchasingOfficer"
				labelKey="register.request.purchasingOfficer"
				path="purchasingOfficer" maxlength="${inputMaxSize}" inputCSS="form-control"  mandatory="true" />

		</div>

		<div class="col-md-3 col-sm-6">
			<formElement:formInputBox idKey="register.request.accountsContact	"
				labelKey="register.request.accountsContact" path="accountsContact"
				maxlength="${inputMaxSize}" inputCSS="form-control"  mandatory="true" />
		</div>
	</div>

	<!--------------------------- INDIVIDUALS ASSOCIATED WITH THE APPLICANT  SECTION  --------------------------->
    
    <div class="row form-row-margin">
        <div class="checkout_subheading row-margin-fix">
            <spring:theme code="register.request.individuals" />
        </div> 
    </div>

	<div class="row">
		<div class="col-md-12 col-sm-12">
			<p>
				<spring:theme code="register.request.individuals.associated" />
			</p>
		</div>
	</div>

	<div class="row">
		<div class="col-md-3 col-sm-6">
			<formElement:formInputBox idKey="register.request.name"
				labelKey="register.request.name" path="name" inputCSS="form-control"
				maxlength="${inputMaxSize}"  mandatory="true" />
		</div>

		<div class="col-md-3 col-sm-6">
			<formElement:formInputBox idKey="register.request.position"
				labelKey="register.request.position" path="position"
				maxlength="${inputMaxSize}" inputCSS="form-control"  mandatory="true" />
		</div>
	</div>

	<div class="row">
		<div class="col-md-3 col-sm-6">
			<formElement:formInputBox idKey="address"
				labelKey="register.request.address" path="address"
				maxlength="${inputAddressMaxSize}" inputCSS="form-control" mandatory="true" />
		</div>

		<div class="col-md-3 col-sm-6">
			<formElement:formInputBox idKey="phoneNo"
				labelKey="register.request.phoneNo" path="phoneNo"
				maxlength="${phoneMaxSize}" inputCSS="form-control" mandatory="true" />
		</div>
	</div>
	<div class="row">
		<div class="col-md-3 col-sm-6">
			<formElement:formInputBox idKey="register.request.dateofBirth"
				labelKey="register.request.dateofBirth" path="dateofBirth"
				inputCSS="form-control"  maxlength="${dateMaxSize}" placeholder="dd/mm/yyyy" mandatory="true" />
		</div>

		<div class="col-md-3 col-sm-6"></div>
	</div>
	
	<input type="hidden" value="${addAnother}" id="addAnotherValue"/>
	<div style="display:none">
	<formElement:formCheckbox idKey="addAnotherLink"
			labelKey="addAnotherLink" path="addAnother" />
	</div>
			
	<!-- Add Another Section -->
	<div style="display: none" id="addAnotherForm">
        <hr>
            <p><spring:theme
						code="customer.request.register.addanother.headline" /></p>
		<div class="row">
			<div class="col-md-3 col-sm-6">
				<formElement:formInputBox idKey="register.request.name1"
					labelKey="register.request.name1" path="name1"
					maxlength="${inputMaxSize}" inputCSS="form-control"  mandatory="true" />
			</div>

			<div class="col-md-3 col-sm-6">
				<formElement:formInputBox idKey="register.request.position1"
					labelKey="register.request.position" path="position1"
					maxlength="${inputMaxSize}" inputCSS="form-control"  mandatory="true" />
			</div>
		</div>

		<div class="row">
			<div class="col-md-3 col-sm-6">
				<formElement:formInputBox idKey="address1"
					labelKey="register.request.address" path="address1"
					maxlength="${inputAddressMaxSize}"  inputCSS="form-control" mandatory="true" />
			</div>

			<div class="col-md-3 col-sm-6">
				<formElement:formInputBox idKey="phoneNo1"
					labelKey="register.request.phoneNo" path="phoneNo1"
					maxlength="${phoneMaxSize}"  inputCSS="form-control" mandatory="true" />
			</div>
		</div>
		
		<div class="row">
		<div class="col-md-3 col-sm-6">
			<formElement:formInputBox idKey="register.request.dateofBirth1"
				labelKey="register.request.dateofBirth" path="dateofBirth1"
				inputCSS="form-control"  maxlength="${dateMaxSize}" placeholder="dd/mm/yyyy" mandatory="true" />
		</div>

		<div class="col-md-3 col-sm-6"></div>
	</div>
		
		
	</div>
	<div class="row">
		<div class="col-md-3 col-sm-6">
			<p>
				<span id="addAnother" class="site-anchor-link">
				<spring:theme
						code="register.request.add.another" /><!-- </a> -->
						</span>
			</p>
		</div>
	</div>
      <!-- ---------------- REFERENCES - SECTION 3 ------------------------ -->  
    <div class="row form-row-margin">
        <div class="checkout_subheading row-margin-fix">
            <spring:theme code="register.request.references" />
        </div> 
    </div>

	<div class="row">
		<div class="col-md-3 col-sm-6">
			<formElement:formInputBox idKey="register.request.bankBranch"
				labelKey="register.request.bankBranch" path="bankBranch"
				maxlength="${inputMaxSize}" inputCSS="form-control"  mandatory="false" />
		</div>

		<div class="col-md-3 col-sm-6">
			<formElement:formInputBox idKey="register.request.contact"
				labelKey="register.request.contact" path="contact"
				maxlength="${inputMaxSize}" inputCSS="form-control"  mandatory="false" />
		</div>
	</div>

	<div class="row">
		<div class="col-md-3 col-sm-6">
			<formElement:formInputBox idKey="register.request.phoneNoReference"
				labelKey="register.request.phoneNoReference" path="phoneNoReference"
				maxlength="${inputMaxSize}" inputCSS="form-control"  mandatory="false" />
		</div>

		<div class="col-md-3 col-sm-6"></div>
	</div>
    
    <div class="row form-row-margin">
        <div class="checkout_subheading row-margin-fix">
            <spring:theme code="register.terms.and.conditions.heading" />
        </div> 
    </div>

	<div class="row">
		<div class="col-md-3 col-sm-6">
			<p>
				<formElement:formCheckboxRegistration
					idKey="register.request.requestTermsConditions"
					labelKey="register.request.requestTermsConditions"
                    inputCSS="site-anchor-link"
					path="requestTermsConditions" />
			</p>
		</div>
	</div>


	<div class="row">
        <div class="col-md-3 col-sm-6">
            <div class="form-actions clearfix">
                <ycommerce:testId code="register_Register_button">
                    <button type="submit" class="btn btn-default btn-block btn-vd-primary">
                        <spring:theme code='${actionNameKey}' />
                    </button>
                </ycommerce:testId>
            </div>
        </div>
    </div>
</form:form>
