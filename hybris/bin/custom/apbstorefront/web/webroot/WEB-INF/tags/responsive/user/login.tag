<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ attribute name="actionNameKey" required="true" type="java.lang.String"%>
<%@ attribute name="action" required="true" type="java.lang.String"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="formElement" tagdir="/WEB-INF/tags/responsive/formElement"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>

<spring:htmlEscape defaultHtmlEscape="true" />

<c:set var="hideDescription" value="checkout.login.loginAndCheckout" />

<div class="login-page__headline">
	<spring:theme code="login.title" />
</div>



<form:form action="${action}" method="post" modelAttribute="loginForm">
	<c:if test="${not empty message}">
		<span class="has-error"> <spring:theme code="${message}" />
		</span>
	</c:if>	
	   <div class="login-input-field">
		   	
            <formElement:formInputBox idKey="j_username" inputCSS="" labelKey=""
                path="j_username" placeholder="EMAIL" mandatory="true" /> 
            <formElement:formPasswordBox idKey="j_password"
                labelKey="" path="j_password" inputCSS="form-control"
                mandatory="true" />
		</div>
    
       <formElement:formCheckbox idKey="j_rememberMe" labelKey="login.rememberMe" path="j_rememberMe"/> 

		<ycommerce:testId code="loginAndCheckoutButton">
			<button type="submit" class="btn btn-primary btn-block btn-vd-primary">
				<spring:theme code="${actionNameKey}" />
			</button>
		</ycommerce:testId>
		
		<div class="forgotten-password-link">
				<ycommerce:testId code="login_forgotPassword_link">
				<a class="site-anchor-link" href="<c:url value='/login/pw/request/external'/>" data-link="<c:url value='/login/pw/request/external'/>" data-cbox-title="<spring:theme code="forgottenPwd.title"/>">
					<c:choose>
					   <c:when test="${cmsSite.uid eq 'sga'}">
							<spring:theme code="login.link.forgottenPwd" />
					   </c:when>
					   <c:otherwise>
							<spring:theme code="login.link.resetPwd" />
					   </c:otherwise>
					</c:choose>
				</a>
				</ycommerce:testId>
		</div>

	
	<c:if test="${expressCheckoutAllowed}">
		<button type="submit" class="btn btn-default btn-block expressCheckoutButton"><spring:theme code="text.expresscheckout.header" /></button>
		<input id="expressCheckoutCheckbox" name="expressCheckoutEnabled" type="checkbox" class="form left doExpressCheckout display-none" />
	</c:if>

</form:form>




