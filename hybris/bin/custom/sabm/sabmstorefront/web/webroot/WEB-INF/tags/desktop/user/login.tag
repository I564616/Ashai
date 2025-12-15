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

<%-- <div class="headline"><spring:theme code="login.title"/></div> --%>
<div class="login-right-title">
    <h1><spring:theme code="login.title"/></h1>
</div>


<p><spring:theme code="login.description"/></p>

<form:form action="${action}" method="post" modelAttribute="loginForm">
	<c:if test="${not empty message}">
		<span class="errors">
			<spring:theme code="${message}"/>
		</span>
	</c:if>
	<c:if test="${loginError}">
		<div class="form_field_error"></div>
	</c:if>
	<div class="form_field-elements clearfix">
		<%-- <formElement:formInputBox idKey="j_username" labelKey="login.email" path="j_username" inputCSS="text" mandatory="true"/>
		<formElement:formPasswordBox idKey="j_password" labelKey="login.password" path="j_password" inputCSS="text password" mandatory="true"/> --%>
		
		<c:url value="/login/pw/forgot/request" var="forgotPasswordUrl" />
		<%-- set error message --%>
		<input id="loginError_topMessage" type="hidden" value="<spring:theme code="login.error.account.not.found.title"/>">
		<input id="username_incorrect" type="hidden" value="<spring:theme code="Please enter a valid email address"/>">
		<input id="loginError" type="hidden" value="${loginError}">			
		<div class="form-group">
       		<label for="exampleInputEmail1"><spring:theme code="login.input.email.title"/></label>
       		<form:input cssClass="form-control" id="j_username" pattern="^\w+([-+.']\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$" path="j_username" tabindex="${tabindex}" autocomplete="off"/>
       		<span class="error" id="username_common"></span>
       		
     	</div>
     	<div class="form-group">
       		<label for="exampleInputPassword1"><spring:theme code="login.input.password.title"/></label>
       		<form:password cssClass="form-control" id="j_password" path="j_password" data-toggle="popover"
             data-content="<p>Forgotten your Email or Password? <a href='${forgotPasswordUrl}'>Click here</a></p>"
             />
             <span class="hidden" id="loginAttempts">${loginAttempts}</span>
       		<input type="text" name="targetUrl" class="hidden" value="${targetUrl}" autocomplete="off"/>
       		<span class="error" id="password_common"></span>
       		<ul class="links-list">
  				<li><b><a href="${forgotPasswordUrl}"><spring:theme code="login.link.forgottenPwd"/></a></b></li><br/>
			</ul>
       		</div>
		<div class="checkbox">
         		<input id="check1" type="checkbox" name="_spring_security_remember_me"  <c:if test="${remember_me}">checked="checked"</c:if>)>
         		<label for="check1"><spring:theme code="login.rememberme"/></label>
     	</div>
	</div>
	<c:if test="${loginError}">
		<div></div>
	</c:if>
	<c:if test="${expressCheckoutAllowed}">
		<div class="expressCheckoutLogin">
			<div class="headline"><spring:theme text="Express Checkout" code="text.expresscheckout.header"/></div>

			<div class="description"><spring:theme text="Benefit from a faster checkout by:" code="text.expresscheckout.title"/></div>

			<ul>
				<li><spring:theme text="setting a default Delivery Address in your account" code="text.expresscheckout.line1"/></li>
				<li><spring:theme text="setting a default Payment Details in your account" code="text.expresscheckout.line2"/></li>
				<li><spring:theme text="a default shipping method is used" code="text.expresscheckout.line3"/></li>
			</ul>

			<div class="expressCheckoutCheckbox clearfix">
				<label for="expressCheckoutCheckbox"><input id="expressCheckoutCheckbox" name="expressCheckoutEnabled"  type="checkbox" class="form left doExpressCheckout"/>
					<spring:theme text="I would like to Express checkout" code="cart.expresscheckout.checkbox"/></label>
			</div>
		</div>
	</c:if>

	<div class="form-actions clearfix margin-top-10-xs">
		<ycommerce:testId code="login_Login_button">
			<button type="button" class="btn btn-primary btn-medium offset-bottom-small"><spring:theme code="${actionNameKey}"/></button>
		</ycommerce:testId>
		<p class="terms-link"><spring:theme code="login.link.term"/></p>
	</div>
</form:form>
<c:url value="/register/registration-form" var="registrationRequestUrl" />
<ul class="links-list">
  	<li><a href="${registrationRequestUrl}" title="<spring:theme code="login.link.register.title"/>"><spring:theme code="login.link.notregistered"/></a></li><br>
</ul>
<div><ul class="links-list"><li class="login-conditionSale"><b><spring:theme code="login.link.cub.conditionSale"/></b></li></ul></div>