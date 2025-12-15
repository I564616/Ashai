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
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format"%>




<!-------------------- COMPANY INFORMATION --------------------------------------> 

		
<form:hidden idKey="enquiryType" path="enquiryType" value=""/>
<form:hidden idKey="enquirySubType" path="enquirySubType" value=""/>
<form:hidden idKey="message" path="message" value=""/>


<div class="row form-row-margin">


<div id="defaultFieldOne" class="user-register__body default-form-header">
	<spring:theme code="contactus.heading.content" />
</div>

<div id="defaultFieldTwo" class="user-register__body" style="margin-bottom:1em">
	<br>
	<spring:theme code="sga.contactus.fields.required" />
</div>

<div id="linkFieldOne" style="display:none" class="user-register__body default-form-header"   style="margin-bottom:1em">
	<spring:theme code="linkform.registration.message" />
</div>

<div id="linkFieldTwoAlb" style="display:none" class="user-register__body" style="margin-bottom:1em">
	<br>
	<spring:theme code="linkform.albregistration.link" />
</div>

<div id="linkFieldTwoAsahi" style="display:none" class="user-register__body" style="margin-bottom:1em">
	<br>
	<spring:theme code="linkform.asahiregistration.link" />
</div>

<div id="linkFieldThree" style="display:none" class="user-register__body"  style="margin-bottom:1em">
	<br>
	<spring:theme code="linkform.registration.assistance" />
</div>

<div id="linkFieldFour" style="display:none" class="user-register__body last-link-child"  style="margin-bottom:1em">
	<br>
	<spring:theme code="linkform.registration.required" />
	<br>
</div>

<!---- Customer Details Line 1 ---->
<div class="row" >
	<!---- Account Number field ---->
	<div class="col-lg-3 col-md-6 col-sm-6 col-xs-12 discrepancy-field-length">
		<div class="form-group">
			<formElement:formInputBox idKey="accountNo"
				labelKey="contactus.account.number" path="accountNumber"
				inputCSS="form-control" mandatory="false" maxlength="${inputACCNoMaxSize}" />
		</div>
	</div>

	<!---- Company Name field ---->

	<div class="col-lg-3 col-md-6 col-sm-6 col-xs-12 discrepancy-field-length">
		<div class="form-group">
			<formElement:formInputBox idKey="companyName"
				labelKey="contactus.company.name" path="companyName" 
				inputCSS="form-control" mandatory="false" maxlength="${inputCompNameMaxSize}" />
		</div>
	</div>
</div>
	
<!---- Customer Details Line 2 ---->
<div class="row" id = "inputRowTwo" >

	<!---- Contact Name field ---->

	<div class="col-lg-3 col-md-6 col-sm-6 col-xs-12 discrepancy-field-length">
		<div class="form-group">
			<formElement:formInputBox idKey="name" labelKey="contactus.name" 
				path="name" inputCSS="form-control" mandatory="true" maxlength="${inputNameMaxSize}" />
		</div>
		<div id="nameError" class="delivery-instruction-padding log-in-error-text error-field">
			<spring:theme code="discrepancy.name.error.message" />
		</div>
	</div>

	<!---- Email Address field ---->

	<div class="col-lg-3 col-md-6 col-sm-6 col-xs-12 discrepancy-field-length">
		<div class="form-group">
			<formElement:formInputBox idKey="emailAddress"
				labelKey="contactus.email.address" path="emailAddress"
				maxlength="${emailMaxSize}" inputCSS="form-control" mandatory="true" />
		</div>
		<div id="emailAddressError" class="delivery-instruction-padding log-in-error-text error-field">
			<spring:theme code="discrepancy.email.error.message" />
		</div>
	</div>
</div>

<!---- Customer Details Line 3 ---->
<div class="row" id = "inputRowThree">
	<div class="col-lg-3 col-md-6 col-sm-6 col-xs-12 discrepancy-field-length">	<div class="form-group">
		<formElement:formInputBox idKey="contactNumber" 
			labelKey="contactus.contact.number" path="contactNumber" 
			maxlength="${inputMaxSize}" inputCSS="form-control" mandatory="true" />

		<div id="contactNumberError" class="delivery-instruction-padding log-in-error-text error-field">
			<spring:theme code="discrepancy.number.error.message" />
		</div>
	</div>
</div>
</div>

<div id="delivery-wrapper" style="display:none">

	<div class="row">
		<!---- Delivery Number field ---->

		<div class="col-lg-4 col-md-4 col-sm-4 col-xs-12 delivery-field-length ">
			<div class="form-group">
				<formElement:formInputBox idKey="deliveryNumber" 
					labelKey="contactus.deliverynumber.field" path="deliveryNumber" 
					maxlength="10" inputCSS="form-control" mandatory="true" />
			</div>
			<div class="delivery-instruction-padding">
				<spring:theme code="discrepancy.delivery.message" />
			</div>
			<div id="deliveryError" class="delivery-instruction-padding log-in-error-text error-field">
				<spring:theme code="discrepancy.delivery.error.message" />
			</div>
		</div>



		<!---- Delivery Number Browse button ---->

		<div id="delivery-browse" class="col-lg-1 col-md-4 col-sm-6 col-xs-12 delivery-browse-button-padding delivery-button-width delivery-number-search">
			<div class="form-group">
				<button id="deliverybrowsebutton" style="width:100%" class="delivery-browse-wrapper btn btn-primary btn-upload btn-block "type="button">
					<spring:theme code="import.pdf.request.register.chooseFile" />
				</button>	
			</div>
		</div>

		<div id="delivery-browse-responsive" class="col-lg-1 col-md-4 col-sm-6 col-xs-12 delivery-browse-button-padding delivery-button-width delivery-number-search-responsive">
			<div class="form-group">
				<button id="deliverybrowsebuttonresponsive" style="width:100%" class="delivery-browse-wrapper btn btn-primary btn-upload btn-block "type="button">
					<spring:theme code="contactus.browsepdf.message" />
				</button>	
			</div>
		</div>

		<c:if test="${payAccess}">
			<div class="col-lg-3 col-md-4 col-sm-6 col-xs-12 delivery-browse-info-padding">
				<div class="user-register__body">
					<spring:theme code="contactus.deliverynumbersearch.prompt" />
				</div>
			</div>
		</c:if>
	</div>

		<div class="col-lg-6 col-md-4 col-sm-6 col-xs-12 discrepancy-instructions"> 
		<div class="user-register__body">
			<spring:theme code="deliveryissue.instruction.message" />
		</div>
	</div>

	<!---- Invoice Discrepancy Header ---->
	<div class=" col-lg-3 col-md-3 col-sm-6 col-xs-12 discrepancy-field-length" style="display:none">
		<div id="discrepancyHeader" class="row discrepancy-div-padding">
			<spring:theme code="contactus.discrepancyinst.header" />
		</div>
	</div>
	
<!---- TBP: Invoice Discrepancy PDF ---->

	<div id="pdfcontentframe" class="discrepancy-order-form" style="display:none">
		<iframe src="" id="pdfcontent"" style="width: 80%; height: 95%;"></iframe>
	</div>


	<div id = "mobile-pdf" class="col-md-4 col-sm-6 mobile-pdf">
		<div class="form-actions clearfix pdf-display">
			<button type="button" class="btn btn-default btn-primary btn-block" id="additional-address">
				<spring:theme code='company.detail.add.delivery.address' />
			</button>
		</div>
	</div>




<!---- Discrepancy fields ---->

	<div class="row mobile-discrepancy-padding" id="clone-row">
		<div class="discrepancy-forms-wrapper">
			<div id="discrepancy-form-0" class="discrepancy-form">
				<div class="row form-row-margin">
					<div class="checkout_subheading row-margin-fix">
						<div id="span_" class="add-additional-address-text">
							<span> <b> PRODUCT # </b> </span>
							<span class="removeAddressLink">
								<a id="delete-report-row-responsive" class="company-remove-link" href="javascript:void(0)" ><spring:theme code="company.detail.remove.delivery.address"/> </a>
							 </span>
						</div>                
					
					</div>
				</div>
				<div id="row1field1" class="col-lg-3 col-md-3 col-sm-3 col-xs-12">
					<div class="form-group">
						<formElement:formInputBox idKey="materialNumber"
						labelKey="discrepancygrid.materialno.header" path="discrepancies[0].materialNumber"
						maxlength="${inputMaxSize}" inputCSS="form-control" mandatory="true" />
					</div>
				</div>
	
				<div id="row1field2" class="col-lg-3 col-md-3 col-sm-3 col-xs-12">
					<div class="form-group">
						<formElement:formInputBox idKey="qtyWithDelIssue"
						labelKey="discrepancygrid.qtywithissue.header" path="discrepancies[0].qtyWithDelIssue"
						maxlength="${inputMaxSize}" inputCSS="form-control" mandatory="true" />
					</div>
				</div>

				<div id="row1field4" style="display:none" class="col-lg-3 col-md-3 col-sm-3 col-xs-12">
					<div class="form-group">
						<formElement:formInputBox idKey="expectedQty"
						labelKey="discrepancygrid.expectedquantity.header" path="discrepancies[0].expectedQty"
						maxlength="${inputMaxSize}" inputCSS="form-control" mandatory="true" />
					</div>
				</div>
	
				<div id="row1field3" class="col-lg-3 col-md-3 col-sm-3 col-xs-12">
					<div class="form-group">
						<formElement:formInputBox idKey="expectedTotalPay"
						labelKey="discrepancygrid.expectedcharge.header" path="discrepancies[0].expectedTotalPay"
						maxlength="${inputMaxSize}" inputCSS="form-control" mandatory="true" />
					</div>
				</div>

				<div id="row1field5" class="col-lg-3 col-md-3 col-sm-3 col-xs-12">
					<div class="form-group">
						<formElement:formInputBox idKey="qtyReceived"
						labelKey="discrepancygrid.actualqty.header" path="discrepancies[0].qtyReceived"
						maxlength="${inputMaxSize}" inputCSS="form-control" mandatory="true" />
					</div>
				</div>

				<div id="row1field6" class="col-lg-3 col-md-3 col-sm-3 col-xs-12">
					<div class="form-group">
						<formElement:formInputBox idKey="actualTotalPay"
						labelKey="discrepancygrid.actualcharge.header" path="discrepancies[0].amtCharged"
						maxlength="${inputMaxSize}" inputCSS="form-control" mandatory="true" />
					</div>
				</div>

				<div id="addReportRowButton" class="input-group discrepancy-report-number plus-button">
					<span id="add-report-row" class="input-group-btn">
						<button id="add-report-row-button" class="btn btn-default discrepancy-button-spacing" type="button">
							<span class="glyphicon glyphicon-plus"></span>
						</button>
					</span>
				</div>

				<div id="delete-button-spawner" class="hidden">
					<div class="qty-selector input-group discrepancy-report-number">
						<span id="delete-report-row" class="input-group-btn">
							<button id="delete-report-row-button" class="btn btn-default discrepancy-button-spacing" type="button">
								<span class="glyphicon glyphicon-minus"></span>
							</button>
						</span>
					</div>
				</div>
				
			</div>
		</div>
				<div class="col-lg-1 col-md-4 col-sm-6 col-xs-12 delivery-browse-button-padding delivery-button-width responsive-add-button">
					<div class="form-group">
						<button id="add-report-row-button-responsive" style="width:100%" class="delivery-browse-wrapper btn btn-primary btn-upload btn-block responsive-add-button" type="button">
							<spring:theme code="contactus.additionaldiscrepancy.message" />
						</button>	
					</div>
				</div>
				
	</div>

		<c:if test="${discrepancyRowError}">
			<div class="col-lg-3 col-md-6 col-sm-6 col-xs-12 discrepancy-field-length log-in-error-text" style="margin-bottom:1em; width:100%; padding-left:0px;">
				<div class="user-register__body"
					<spring:theme code="discrepancy.instruction.message" />
				</div>
			</div>
		</c:if>


		<div id="materialNoError" class="log-in-error-text" style="display:none">
			<spring:theme code="discrepancy.material.error.message" />
		</div>

		<div id="delIssueError" class="log-in-error-text" style="display:none">
			<spring:theme code="discrepancy.delivery.issue.error.message" />
		</div> 

		<div id="qtyExpectedError" class="log-in-error-text" style="display:none">
			<spring:theme code="discrepancy.expected.quantity.error.message" />
		</div>

		<div id="qtyReceivedError" class="log-in-error-text" style="display:none">
			<spring:theme code="discrepancy.received.quantity.error.message" />
		</div>

		<div id="expectedChargeError" class="log-in-error-text" style="display:none">
			<spring:theme code="discrepancy.expected.charge.error.message" />
		</div>

		<div id="actualChargeError" class="log-in-error-text" style="display:none">
			<spring:theme code="discrepancy.actual.charge.error.message" />
		</div>


	<!---- Additional Details ---->

	<div>
		<div id="additional-information" class="form-group add-info-padding" style="margin-left:1.7em margin-top:6em; display=none;">
			<formElement:formTextArea idKey="addInfo" 
				labelKey="contactus.additionalinformation.field" path="addInfo"
				areaCSS="textarea form-control" mandatory="false" maxlength="${furtherDetailsMaxSize}" />
		</div>
	</div>

</div>






        
<div class="row">
	<div id="further-detail" class="col-lg-12 col-md-6 col-sm-12 col-xs-12">
		<div class="form-group">
			<formElement:formTextArea idKey="furtherdetail"
				labelKey="contactus.further.detail" path="furtherDetail"
				areaCSS="textarea form-control" mandatory="true" maxlength="${furtherDetailsMaxSize}" />

			<div id="furtherDetailError" class="delivery-instruction-padding log-in-error-text error-field">
				<spring:theme code="contactus.furtherdetail.error.message" />
			</div>

		</div>
	</div>
</div>

<div class="user-register__body attachment-padding">
	<spring:theme code="contactus.inline.attachments" />
</div>
<pdf:importPDFContactUsPage/>

<div id="fileTooBigError" class="delivery-instruction-padding log-in-error-text-pdf error-field-pdf">
	<spring:theme code="import.pdf.file.fileMaxSizeExceeded" />
</div>

<div id="invalidFileError" class="delivery-instruction-padding log-in-error-text-pdf error-field-pdf">
	<spring:theme code="import.pdf.file.not.matched" />
</div>

<div class="row">
	<div class="col-lg-3 col-md- col-sm-4 col-xs-12">
		<div class="form-actions clearfix">
			<button id="formSubmitButton" type="submit"
				class="btn btn-default btn-block btn-vd-primary">
				<spring:theme code="contactus.send.button" />
			</button>
		</div>
	</div>
</div>







