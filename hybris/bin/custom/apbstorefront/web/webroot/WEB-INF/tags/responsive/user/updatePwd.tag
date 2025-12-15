<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="formElement" tagdir="/WEB-INF/tags/responsive/formElement" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>

<spring:htmlEscape defaultHtmlEscape="true" />
<div class="login-section">
	<div class="login-page__headline">
		<spring:theme code="text.account.profile.resetPassword" />
	</div>
	

			<form:form method="post" modelAttribute="asahiUpdatePwdForm">
	<div class="login-input-field">
				<formElement:formPasswordBox idKey="password" labelKey="" path="pwd" inputCSS="form-control" mandatory="true" />
				<formElement:formPasswordBox idKey="updatePwd.checkPwd" labelKey="" path="checkPwd" inputCSS="form-control" mandatory="true" />
			<div id="reset-password-page">
					<spring:theme code="updatePwd.guideline" />
				</div>
				<div class="row login-form-action">
					<div class="col-sm-12">
						<button type="submit" class="btn btn-primary btn-block btn-vd-primary" <c:if test="${tokenInvalid eq true}"><c:out value="disabled"/></c:if>>
							<spring:theme code="updatePwd.submit"/>
						</button>
					</div>
				</div>
				</div>
			</form:form>
	
</div>