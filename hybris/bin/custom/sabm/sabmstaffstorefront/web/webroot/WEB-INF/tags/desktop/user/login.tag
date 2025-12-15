<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="actionNameKey" required="true" type="java.lang.String" %>
<%@ attribute name="action" required="true" type="java.lang.String" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="formElement" tagdir="/WEB-INF/tags/desktop/formElement" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="cart" tagdir="/WEB-INF/tags/desktop/cart" %>

<div class="col-sm-12">
	<h1><spring:theme code="text.staff.portal.login.title"/></h1>
	<p class="sub-line"><spring:theme code="text.staff.portal.login.Subtitle"/></p>
	<div class="row">
		<form:form action="${action}" method="post" modelAttribute="loginForm">
			<c:if test="${not empty message}">
				<span class="errors">
					<spring:theme code="${message}"/>
				</span>
			</c:if>
			<c:if test="${loginError}">
				<div class="form_field_error"></div>
			</c:if>
			<div class="col-sm-6 col-md-4">
				<%-- set error message --%>
				<input id="loginError_topMessage" type="hidden" value="<spring:theme code="login.error.account.not.found.title"/>">
				<input id="username_incorrect" type="hidden" value="<spring:theme code="login.error.username.format.error"/>">
				<input id="loginError" type="hidden" value="${loginError}">
				<div class="form-group">
					<label for="j_username"><spring:theme code="text.staff.portal.login.email"/></label>
					<form:input cssClass="form-control" id="j_username" path="j_username" tabindex="${tabindex}" autocomplete="off"/>
					<input type="text" name="targetUrl" class="hidden" value="${targetUrl}" />
					<span class="error" id="username_common"></span>
				</div>
				<div class="form-group offset-bottom-large">
					<label for="j_password"><spring:theme code="text.staff.portal.login.password"/></label>
					<form:password cssClass="form-control" id="j_password" path="j_password"/>
					<span class="error" id="password_common"></span>
				</div>
				<div class="form-actions clearfix margin-top-10-xs">
					<ycommerce:testId code="login_Login_button">
						<button type="button" class="btn btn-primary btn-large btn-flex-fixed offset-bottom-small"><spring:theme code="text.staff.portal.login.button"/></button>
					</ycommerce:testId>
				</div>
			</div>
		</form:form>
	</div>
	<div class="row">
		<div class="col-xs-12">
			<p class="margin-top-8"><spring:theme code="text.staff.portal.login.StaticText"/></p>
		</div>
	</div>
</div>

