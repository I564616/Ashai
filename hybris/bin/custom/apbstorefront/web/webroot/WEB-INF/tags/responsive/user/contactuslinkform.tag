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


<div class="user-register__body form-row-margin">
	<spring:theme code="linkform.registration.message" />
</div>

<div class="user-register__body form-row-margin">
	<spring:theme code="linkform.asahiregistration.link" />
</div>

<div class="user-register__body form-row-margin">
	<spring:theme code="linkform.registration.assistance" />
</div>

<div class="user-register__body form-row-margin">
	<spring:theme code="linkform.registration.required" />
</div>


<!-------------------- COMPANY INFORMATION --------------------------------------> 

<div class="row form-row-margin">
    <div class="checkout_subheading row-margin-fix">
        <spring:theme code="contactus.company.information" />
    </div>
</div>
        



<div class="row form-row-margin">
    <div class="checkout_subheading row-margin-fix">
        <spring:theme code="contactus.heading.attachments" />
    </div>
</div>


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


