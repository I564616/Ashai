<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="formElement"
	tagdir="/WEB-INF/tags/desktop/formElement"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

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
			<spring:theme code="forgottenPwd.title" />
		</h1>
	</div>
	<div class="col-xs-12">
		<h3>
			<spring:theme code="forgottenPwd.description" />
		</h3>
	</div>
	<div class="col-xs-12 password-instructions">
    		<ul>
            	<li><spring:theme code="forgottenPwd.firstinstruction"/></li>
            	<li><spring:theme code="forgottenPwd.secondinstruction"/></li>
            	<li><spring:theme code="forgottenPwd.thirdinstruction"/></li>
            </ul>
    	</div>
	<div class="col-md-8">
		<form:form id="forgottenPwdForm" method="post"
			modelAttribute="forgottenPwdForm" class="margin-top-30">
			<div class="form-group">
				<label id="forgottenPwd_label" for="exampleInputEmail1"><spring:theme
						code="forgottenPwd.email" /></label>
				<span id="invalidEmail"class="error" style="display: none"><spring:theme
                						code="forgottenPwd.email.invalid" /></span>
                <span id="emailNotFound"class="error" style="display: none"><spring:theme
                                        code="forgottenPwd.email.notfound" /> 
                </span>
				<form:input path="email" class="form-control"
					id="forgottenPwd_email"/>
				<span style="font-size:10px">
				<spring:theme code="forgottenPwd.howto" />
				</span>
			</div>

			<button
			    id="openModal"
			    type="button"
				class="btn btn-primary offset-bottom-small"
				onClick="rm.forgotpassword.emailPresentSubmit()">
				<spring:theme code="forgottenPwd.send" />
			</button>
            <a href="#forgotpwd-popup" id="forgotpwd-popup-link" class="regular-popup hidden">link</a>
		</form:form>
		
		<input type="hidden" id="sendEmailTitle" value="<spring:theme code="forgottenPwd.email.content.title"/>">

	</div>
</div>

<div class="price-popup mfp-hide" id="forgotpwd-popup">
    <h2 class="h1"><spring:theme code="forgottenPwd.send" /></h2>
    <p class="text-justify"><spring:theme code="forgottenPwd.popup.newText" /></p>
    <div class="row text-right">
        <div class="col-xs-12 mt-10 text-center">
            <button id="submit_button" type="button"
                class="btn btn-primary">
                <spring:theme code="forgottenPwd.popup.confirm" />
            </button>
        </div>
    </div>
</div>
