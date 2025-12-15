<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="formElement"
	tagdir="/WEB-INF/tags/desktop/formElement"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!--[if lt IE 8]>
    <p class="browsehappy">You are using an <strong>outdated</strong> browser. Please <a href="http://browsehappy.com/">upgrade your browser</a> to improve your experience.</p>
    <![endif]-->
<!-- <div class="container">
	<header class="navbar navbar-default">
		<div class="container-fluid">
			<div class="navbar-header">
				<a href="/" class="navbar-brand">SAB Miller</a>
			</div>
		</div>
	</header>
</div> -->
<div class="row">
	<div class="col-xs-12">
		<h1>
			<spring:theme code="forgottenEmail.title" />
		</h1>
	</div>
	<div class="col-xs-12">
		<h3>
			<spring:theme code="forgottenEmail.description" />
		</h3>
	</div>

	<div class="col-md-12">
        <form>
            <div class="form-group">
                <label><spring:theme code="forgottenEmail.email" /></label>
                 <p><spring:theme code="forgottenEmail.email.value" /></p>
                 <div class="row">
                    <div class="col-xs-12 col-md-5">
                        <label><spring:theme code="forgottenEmail.callus" /></label>
                        <p><spring:theme code="forgottenEmail.callus.value" /></p>
                    </div>
                    <div class="col-xs-12 col-md-7">
                        <label><spring:theme code="forgottenEmail.businesshours" /></label>
                        <p class="forgotmail-p"><spring:theme code="forgottenEmail.businesshours.mfvalue" /></p>                       
                        <p><spring:theme code="forgottenEmail.businesshours.satvalue" /></p>
                        <p><spring:theme code="forgottenEmail.businesshours.sunvalue" /></p>
                    </div>
                 </div>
                 <label><spring:theme code="forgottenEmail.writetous" /></label>
                  <p><spring:theme code="forgottenEmail.writetous.value" /></p>
            </div>
        </form>
		<input type="hidden" id="sendEmailTitle" value="<spring:theme code="forgottenPwd.email.content.title"/>">

	</div>
</div>


