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


<!-------------------- INVOICE DISCREPANCY FORM --------------------------------------> 
    


<div class="row">

	<!---- Delivery Number field ---->
	<!--- TBD: replace path with delivery number value ---->
	<div class="col-lg-3 col-md-3 col-sm-4 col-xs-12 delivery-field-length">
		<div class="form-group">
			<formElement:formInputBox idKey="deliveryNumber" 
				labelKey="contactus.deliverynumber.field" path="deliveryNumber" 
				maxlength="${inputMaxSize}" inputCSS="form-control" mandatory="true" />
		</div>
		<div>
			<spring:theme code="discrepancy.delivery.message" />
		</div>
	</div>

	<!---- Delivery Number Browse button ---->

	<div class="col-lg-1 col-md-4 col-sm-6 col-xs-12 delivery-browse-button-padding">
		<div class="form-group">
			<button id="deliverybrowsebutton" class="delivery-browse-wrapper btn btn-primary btn-upload btn-block "type="button">
				<spring:theme code="import.pdf.request.register.chooseFile" />
			</button>	
		</div>
	</div>

	<div class="col-lg-3 col-md-4 col-sm-6 col-xs-12 delivery-browse-info-padding">
		<div class="user-register__body contact-us-padding-bottom">
			<spring:theme code="contactus.deliverynumbersearch.prompt" />
		</div>
	</div>
</div>

<!---- Invoice Discrepancy Header ---->
<div class=" col-lg-3 col-md-3 col-sm-6 col-xs-12 discrepancy-field-length">
	<div id="discrepancyHeader" class="row discrepancy-div-padding">
		<spring:theme code="contactus.discrepancyinst.header" />
	</div>
</div>
<!---- TBP: Invoice Discrepancy Matrix ---->

<div class="discrepancy-order-form">
	<iframe src="" 
	style="width: 80%; height: 100%;">
	</iframe>
</div>


<div id = "mobile-pdf" class="col-md-4 col-sm-6 mobile-pdf" style="margin-bottom:3em; margin-top:1em;">
	<div class="form-actions clearfix pdf-display">
		<button type="button" class="btn btn-default btn-primary btn-block" id="additional-address">
			<spring:theme code='company.detail.add.delivery.address' />
		</button>
	</div>
</div>

<!---- Discrepancy fields ---->
<!--- TBD: replace paths for reported values ---->

<div id="discrepancyrow5" class="row discrepancy-row-margin" style="display:none">

	<div id="row5field1" class="col-lg-3 col-md-3 col-sm-3 col-xs-12">
		<div class="form-group">
			<formElement:formInputBox idKey="contactNumber" 
				labelKey="discrepancygrid.materialno.header" path="contactNumber"
				maxlength="${inputMaxSize}" inputCSS="form-control" mandatory="true" />
		</div>
	</div>

	<div id="row5field2" class="col-lg-3 col-md-3 col-sm-3 col-xs-12 expected-total-payable-margin">
		<div class="form-group">
			<formElement:formInputBox idKey="contactNumber" 
				labelKey="discrepancygrid.expectedtotalpayable.header" path="contactNumber"
				maxlength="${inputMaxSize}" inputCSS="form-control" mandatory="true" />
		</div>
	</div>
</div>

<div id="discrepancyrow4" class="row discrepancy-row-margin" style="display:none">

	<div id="row4field1" class="col-lg-3 col-md-3 col-sm-3 col-xs-12">
		<div class="form-group">
			<formElement:formInputBox idKey="contactNumber" 
				labelKey="discrepancygrid.materialno.header" path="contactNumber"
				maxlength="${inputMaxSize}" inputCSS="form-control" mandatory="true" />
		</div>
	</div>

	<div id="row4field2" class="col-lg-3 col-md-3 col-sm-3 col-xs-12 expected-total-payable-margin hidden">
		<div class="form-group">
			<formElement:formInputBox idKey="contactNumber" 
				labelKey="discrepancygrid.expectedtotalpayable.header" path="contactNumber"
				maxlength="${inputMaxSize}" inputCSS="form-control" mandatory="true" />
		</div>
	</div>
</div>

<div id="discrepancyrow3" class="row discrepancy-row-margin" style="display:none">
	<div id="row3field1" class="col-lg-3 col-md-3 col-sm-3 col-xs-12">
		<div class="form-group">
			<formElement:formInputBox idKey="contactNumber" 
				labelKey="discrepancygrid.materialno.header" path="contactNumber"
				maxlength="${inputMaxSize}" inputCSS="form-control" mandatory="true" />
		</div>
	</div>

	<div id="row3field2" class="col-lg-3 col-md-3 col-sm-3 col-xs-12 expected-total-payable-margin">
		<div class="form-group">
			<formElement:formInputBox idKey="contactNumber" 
				labelKey="discrepancygrid.expectedtotalpayable.header" path="contactNumber"
				maxlength="${inputMaxSize}" inputCSS="form-control" mandatory="true" />
		</div>
	</div>
</div>

<div id="discrepancyrow2" class="row discrepancy-row-margin" style="display:none">

	<div id="row2field1" class="col-lg-3 col-md-3 col-sm-3 col-xs-12 style="margin-top:2em;">
		<div class="form-group">
			<formElement:formInputBox idKey="contactNumber" 
				labelKey="discrepancygrid.materialno.header" path="contactNumber"
				maxlength="${inputMaxSize}" inputCSS="form-control" mandatory="true" />
		</div>
	</div>

	<div id="row2field2" class="col-lg-3 col-md-3 col-sm-3 col-xs-12 expected-total-payable-margin">
		<div class="form-group">
			<formElement:formInputBox idKey="contactNumber" 
				labelKey="discrepancygrid.expectedtotalpayable.header" path="contactNumber"
				maxlength="${inputMaxSize}" inputCSS="form-control" mandatory="true" />
		</div>
	</div>
</div>
 
<div class="row" style="display:none">

	<div id="row1field1" class="col-lg-3 col-md-3 col-sm-3 col-xs-12">
		<div class="form-group">
			<formElement:formInputBox idKey="contactNumber" 
				labelKey="discrepancygrid.materialno.header" path="contactNumber"
				maxlength="${inputMaxSize}" inputCSS="form-control" mandatory="true" />
		</div>
	</div>

	<div id="row1field2" class="col-lg-3 col-md-3 col-sm-3 col-xs-12">
		<div class="form-group">
			<formElement:formInputBox idKey="contactNumber" 
				labelKey="discrepancygrid.expectedquantity.header" path="contactNumber"
				maxlength="${inputMaxSize}" inputCSS="form-control" mandatory="true" />
		</div>
	</div>
</div>





<div class="row" id="clone-row">
	<div class="discrepancy-forms-wrapper">
		<div id="discrepancy-form-0" class="discrepancy-form">
			<div class="row form-row-margin">
				<div class="checkout_subheading row-margin-fix">
					<div id="span_" class="add-additional-address-text">
						<span> <b> PRODUCT #1 </b> </span>
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
					labelKey="discrepancygrid.expectedquantity.header" path="discrepancies[0].qtyWithDelIssue"
					maxlength="${inputMaxSize}" inputCSS="form-control" mandatory="true" />
				</div>
			</div>


			<div id="row1field3" class="col-lg-3 col-md-3 col-sm-3 col-xs-12 expected-total-payable-margin">
				<div class="form-group">
					<formElement:formInputBox idKey="expectedTotalPay"
					labelKey="discrepancygrid.expectedtotalpayable.header" path="discrepancies[0].expectedTotalPay"
					maxlength="${inputMaxSize}" inputCSS="form-control" mandatory="true" />
				</div>
			</div>

			<div id="row1field4" style="display:none" class="col-lg-3 col-md-3 col-sm-3 col-xs-12 expected-total-payable-margin">
				<div class="form-group">
					<formElement:formInputBox idKey="expectedQty"
					labelKey="discrepancygrid.expectedtotalpayable.header" path="discrepancies[0].expectedQty"
					maxlength="${inputMaxSize}" inputCSS="form-control" mandatory="true" />
				</div>
			</div>
		</div>

		<div class="input-group discrepancy-report-number">
			<span id="add-report-row" class="input-group-btn">
				<button id="add-report-row-button" class="btn btn-default" type="button">
					<span class="glyphicon glyphicon-plus"></span>
				</button>
			</span>
		</div>
	</div>
</div>

<div id="delete-button-spawner" class="hidden">
    <div class="qty-selector input-group discrepancy-report-number">
		<span id="delete-report-row" class="input-group-btn">
			<button id="delete-report-row-button" class="btn btn-default" type="button">
				<span class="glyphicon glyphicon-minus"></span>
			</button>
		</span>
        <div id=divContainer></div>
    </div>
</div>

<!---- Additional Details ---->

<div>
    <div class="form-group" style="margin-left:1.7em margin-top:6em;">
        <formElement:formTextArea idKey="addInfo" 
            labelKey="contactus.additionalinformation.field" path="addInfo"
            areaCSS="textarea form-control" mandatory="false" maxlength="${furtherDetailsMaxSize}" />
    </div>
</div>

<div class="row">
	<div class="col-lg-6 col-md-6 col-sm-6 col-xs-12">
		<div class="form-group">
			<formElement:formTextArea idKey="furtherdetail"  
				labelKey="contactus.further.detail" path="furtherDetail"
				areaCSS="textarea form-control" mandatory="true" maxlength="${furtherDetailsMaxSize}" />
		</div>
	</div>
</div>

<div class="row form-row-margin">
    <div class="checkout_subheading row-margin-fix">
        <spring:theme code="contactus.heading.attachments" />
    </div>
</div>





