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


<!---- 'Send us a message' header ---->

<div class="row form-row-margin">
    <div class="discrepancy_sendus_subheading row-margin-fix">
        <spring:theme code="contactus.discrepancy.header" />
    </div>
</div>
 
<!---- Customer Details Line 1 ---->

<div class="row">

	<!---- Account Number field ---->

	<div class="col-lg-3 col-md-3 col-sm-6 col-xs-12 discrepancy-field-length">
		<div class="form-group">
			<formElement:formInputBox idKey="accountNo"
				labelKey="contactus.account.number" path="accountNumber"
				inputCSS="form-control" mandatory="false" maxlength="${inputACCNoMaxSize}" />
		</div>
	</div>

	<!---- Company Name field ---->

	<div class="col-lg-3 col-md-3 col-sm-6 col-xs-12 discrepancy-field-length">
		<div class="form-group">
			<formElement:formInputBox idKey="companyName"
				labelKey="contactus.company.name" path="companyName"
				inputCSS="form-control" mandatory="false" maxlength="${inputCompNameMaxSize}" />
		</div>
	</div>
</div>

<!---- Customer Details Line 2 ---->

<div class="row">

	<!---- Contact Name field ---->

	<div class="col-lg-3 col-md-3 col-sm-6 col-xs-12 discrepancy-field-length">
		<div class="form-group">
			<formElement:formInputBox idKey="name" labelKey="contactus.name"
				path="name" inputCSS="form-control" mandatory="true" maxlength="${inputNameMaxSize}" />
		</div>
	</div>

	<!---- Email Address field ---->

	<div class="col-lg-3 col-md-3 col-sm-6 col-xs-12 discrepancy-field-length">
		<div class="form-group">
			<formElement:formInputBox idKey="emailAddress"
				labelKey="contactus.email.address" path="emailAddress"
				maxlength="${emailMaxSize}" inputCSS="form-control" mandatory="true" />
		</div>
	</div>
</div>

<!---- Customer Details Line 3 ---->

<div class="row">

	<!---- Contact Number field ---->

	<div class="col-lg-3 col-md-3 col-sm-6 col-xs-12 discrepancy-field-length">
		<div class="form-group">
			<formElement:formInputBox idKey="contactNumber"
				labelKey="contactus.contact.number" path="contactNumber"
				maxlength="${inputMaxSize}" inputCSS="form-control" mandatory="true" />
		</div>
	</div>
</div>

<!---- Customer Details Line 4 ---->

<div class="row">

	<!---- Delivery Number field ---->

	<div class="col-lg-3 col-md-3 col-sm-6 col-xs-12 delivery-number-field-length">
		<div class="form-group">
			<formElement:formInputBox idKey="contactNumber"
				labelKey="contactus.deliverynumber.field" path="contactNumber"
				maxlength="${inputMaxSize}" inputCSS="form-control" mandatory="true" />
		</div>
	</div>

	<!---- Delivery Number Browse button ---->

	<div class="col-lg-3 col-md-3 col-sm-6 col-xs-12 delivery-browse-button-padding">
		<div class="form-group">
			<button class="delivery-browse-wrapper btn btn-primary btn-upload btn-block "type="submit">
				<spring:theme code="import.pdf.request.register.chooseFile" />
			</button>	
		</div>
	</div>

	<div class="col-lg-3 col-md-3 col-sm-6 col-xs-12 delivery-browse-info-padding">
		<div class="user-register__body contact-us-padding-bottom">
			<spring:theme code="contactus.deliverynumbersearch.prompt" />
		</div>
	</div>
</div>

<div>
	<spring:theme code="discrepancy.delivery.message" />
</div>


<!---- Discrepancy Matrix Header ---->

<div class="discrepancy-div-padding">
	<br>
	<spring:theme code="deliveryissue.instruction.message" />
</div>

<!---- TBP: Invoice Discrepancy Matrix ---->

	<div class="discrepancy-order-form">
		<iframe src="/medias/exemplarpdf.pdf?context=bWFzdGVyfGltYWdlc3wyMDY4NDh8YXBwbGljYXRpb24vcGRmfGltYWdlcy9oNTIvaDEwLzg4MTYxNDcxMDM3NzQucGRmfDdkOWEwMDljNzIyMWYwMTE3ZWM1YzFhMDZjMzlkZjljOGIyNTAwMzUxODM0MWFlOWU2OTdmNTBlZWQ3ODkxYzg" 
		style="width: 80%; height: 100%;">
		</iframe>
	</div>

<!---- Discrepancy fields ---->

<div class="row">
	<div class="col-lg-3 col-md-3 col-sm-3 col-xs-12 discrepancy-field-length">
		<div class="form-group">
			<formElement:formInputBox idKey="contactNumber"
				labelKey="discrepancygrid.materialno.header" path="contactNumber"
				maxlength="${inputMaxSize}" inputCSS="form-control" mandatory="true" />
		</div>
	</div>

	<div class="col-lg-3 col-md-3 col-sm-3 col-xs-12 discrepancy-field-length">
		<div class="form-group">
			<formElement:formInputBox idKey="contactNumber"
				labelKey="deliveryissuegrid.qtywithissue.header" path="contactNumber"
				maxlength="${inputMaxSize}" inputCSS="form-control" mandatory="true" />
		</div>
	</div>

	<div class="qty-selector input-group js-cart-qty-selector discrepancy-report-number">

		<span id="remove-report-row" class="input-group-btn">
			<button id="remove-report-row-button" class="btn btn-default js-cart-qty-selector-minus" type="button" >
				<span class="glyphicon glyphicon-minus" >
				</span>
			</button>
		</span>

		<span id="edit-report-row" >
			<input id="edit-report-row-input" class="form-control js-bonusproduct-cart-input js-cart-qty-selector-input js-update-entry-quantity-input discrepancy-text-align" type="number" size="1" data-min="1" value="1" />
		</span>	

		<span id="add-report-row" class="input-group-btn">
			<button id="add-report-row-button" class="btn btn-default js-cart-qty-selector-plus" type="button">
				<span class="glyphicon glyphicon-plus">
				</span>
			</button>
		</span>
	</div>
</div>

<!---- Additional Details ---->

<div>
    <div class="form-group" style="margin-left:1.7em margin-top:6em;">
        <formElement:formTextArea idKey="furtherdetail"
            labelKey="contactus.additionalinformation.field" path="furtherDetail"
            areaCSS="textarea form-control" mandatory="false" maxlength="${furtherDetailsMaxSize}" />
    </div>
</div>

<pdf:importPDFContactUsPage/>

<div class="row">
	<div class="col-lg-4 col-md-4 col-sm-6 col-xs-12">
		<div class="form-actions submit-form-padding clearfix">
			<button type="submit"
				class="btn btn-default btn-block btn-vd-primary ">
				<spring:theme code="contactus.send.button" />
			</button>
		</div>
	</div>
</div>

