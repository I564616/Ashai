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
<c:set value="/asahiStaffPortal/_ui/responsive/common/images/icon-email-asahigrey.svg" var="emailIcon" />
<c:set value="/asahiStaffPortal/_ui/responsive/common/images/icon-lock-asahigrey.svg" var="lockIcon" />
<spring:message code="login.error.account.not.found.title" var="accountNotFound"/>
<spring:message code="login.field.required.message" var="required"/>
<input type="hidden" name="fieldRequired" value="${required}" />
<input type="hidden" name="emailFormatError" value="<spring:message code="login.error.username.format.error" />" />

<div ng-controller="customerLoginCtrl">
    <div class="alert-danger ng-cloak" ng-show="globalAlert">
        ${accountNotFound}
    </div>

    <div class="login-page__headline">
        <spring:theme code="text.staff.portal.login.title"/>
    </div>

    <form:form action="${action}" method="post" modelAttribute="loginForm">
        <c:if test="${not empty message}">
            <span class="errors">
                <spring:theme code="${message}"/>
            </span>
        </c:if>
        <c:if test="${loginError}">
            <div class="form_field_error"></div>
        </c:if>

        <div class="login-input-field">
            <formElement:formInputBox
                idKey="j_username"
                inputCSS="form-control"
                labelKey=""
                path="j_username"
                icon="${emailIcon}"
                placeholder="EMAIL"
                autocomplete="off"
                errorMessage="${required}"
                mandatory="true"
                validateModel="usernameInvalid"
                ngFocus="focus($event)"
                ngModel="j_username" />
            <formElement:formPasswordBox
                idKey="j_password"
                labelKey=""
                path="j_password"
                inputCSS="form-control"
                icon="${lockIcon}"
                placeholder="PASSWORD"
                errorMessage="${required}"
                mandatory="true"
                validateModel="passwordInvalid"
                ngFocus="focus($event)"
                ngModel="j_password" />
        </div>
        <br />
        <br />
        <div class="form-actions clearfix">
            <ycommerce:testId code="login_Login_button">
                <button
                    type="submit"
                    class="btn btn-vd-primary btn-block expressCheckoutButton"
                    ng-click="submit($event)"><spring:theme code="text.staff.portal.login.button" /></button>
            </ycommerce:testId>
        </div>

        <p class="mt-15"><span><spring:theme code="text.staff.portal.login.StaticText" />&nbsp;<a class="text-underline" href="mailto:ALBConnectSupport@asahi.com.au"><b>ALBConnectSupport@asahi.com.au</b></a></span></p>
    </form:form>
</div>