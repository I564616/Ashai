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

<div class="user-register__body">
	<spring:theme code="contactus.heading.content" />
</div>

<div class="user-register__body">
	<br>
	<spring:theme code="sga.contactus.fields.required" />
</div>


<!-------------------- COMPANY INFORMATION --------------------------------------> 
    
<div class="row form-row-margin">
    <div class="checkout_subheading row-margin-fix">
        <spring:theme code="contactus.company.information" />
    </div>
</div>
        
<div class="row">
	<div class="col-lg-3 col-md-3 col-sm-6 col-xs-12">
		<div class="form-group">
			<formElement:formInputBox idKey="accountNo"
				labelKey="contactus.account.number" path="accountNumber"
				inputCSS="form-control" mandatory="false" maxlength="${inputACCNoMaxSize}" />
		</div>
	</div>

	<div class="col-lg-3 col-md-3 col-sm-6 col-xs-12">
		<div class="form-group">
			<formElement:formInputBox idKey="companyName"
				labelKey="contactus.company.name" path="companyName"
				inputCSS="form-control" mandatory="false" maxlength="${inputCompNameMaxSize}" />
		</div>
	</div>
</div>
    
<div class="row form-row-margin">
    <div class="checkout_subheading row-margin-fix">
        <spring:theme code="contactus.heading.account.details" />
    </div>
</div>

<div class="row">
	<div class="col-lg-3 col-md-3 col-sm-6 col-xs-12">
		<div class="form-group">
			<formElement:formInputBox idKey="name" labelKey="contactus.name"
				path="name" inputCSS="form-control" mandatory="true" maxlength="${inputNameMaxSize}" />
		</div>
	</div>

	<div class="col-lg-3 col-md-3 col-sm-6 col-xs-12">
		<div class="form-group">
			<formElement:formInputBox idKey="emailAddress"
				labelKey="contactus.email.address" path="emailAddress"
				maxlength="${emailMaxSize}" inputCSS="form-control" mandatory="true" />
		</div>
	</div>
</div>
<div class="row">
	<div class="col-lg-3 col-md-3 col-sm-6 col-xs-12">
		<div class="form-group">
			<formElement:formInputBox idKey="contactNumber"
				labelKey="contactus.contact.number" path="contactNumber"
				maxlength="${inputMaxSize}" inputCSS="form-control" mandatory="true" />
		</div>
	</div>
</div>

<div class="row form-row-margin">
    <div class="checkout_subheading row-margin-fix">
        <spring:theme code="contactus.heading.message.details" />
    </div>
</div>


<div class="row" id ="otherSubjectFlag" style="display:none">
	<div class="col-lg-3 col-md-3 col-sm-6 col-xs-12">
			<formElement:formInputBox idKey="subjectFlag"
				labelKey="" path="subjectFlag"
				 inputCSS="form-control"  />
	</div>
</div>

<div class="row">
	<div class="col-lg-3 col-md-3 col-sm-6 col-xs-12">
		<formElement:formSelectBox idKey="subject"
			labelKey="contactus.subject" selectCSSClass="form-control"
			path="subject" mandatory="false" skipBlank="true"
			skipBlankMessageKey="form.select.empty" items="${subjects}" />
	</div>
</div>

<div class="row" id ="otherSubject">
	<div class="col-lg-3 col-md-3 col-sm-6 col-xs-12">
			<formElement:formInputBox idKey="subjectOther"
				labelKey="contactus.subject.other" path="subjectOther"
				maxlength="${otherMaxSize}" inputCSS="form-control" mandatory="true" />
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


<pdf:importPDFContactUsPage/>

<div class="row">
	<div class="col-lg-4 col-md-4 col-sm-6 col-xs-12">
		<div class="form-actions clearfix">
			<button type="submit"
				class="btn btn-default btn-block btn-vd-primary">
				<spring:theme code="contactus.send.button" />
			</button>
		</div>
	</div>
</div>

