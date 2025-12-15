<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template"%>
<%@ taglib prefix="formElement" tagdir="/WEB-INF/tags/responsive/formElement"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="user" tagdir="/WEB-INF/tags/responsive/user"%>

<spring:htmlEscape defaultHtmlEscape="true" />
<spring:url value="/my-company/organization-management/manage-users/resetpassword" var="resetpasswordUrl">
    <spring:param name="user" value="${customerResetPasswordForm.uid}" />
</spring:url>

<spring:url value="/my-company/organization-management/manage-users/details/" var="viewUserUrl" htmlEscape="false">
    <spring:param name="user" value="${customerResetPasswordForm.uid}"/>
</spring:url>

<!-- Validation Error messages -->
<spring:theme code="form.global.error" var="formGlobalError" />
<spring:message code="updatePwd.pwd.invalid" var="updatePwdInvalid" />
<spring:message code="validation.checkPwd.equals" var="validationCheckPwdEquals" />
<input id="formGlobalError" type="hidden" value="${formGlobalError}" />
<input id="updatePwdInvalid" type="hidden" value="${updatePwdInvalid}" />
<input id="validationCheckPwdEquals" type="hidden" value="${validationCheckPwdEquals}" />
<c:set value="${request.getParameter('user')}" var="userEmail"/>

<template:page pageTitle="${pageTitle}">
    <div class="account-section">
        <div class="account-section-header">
            <div class="row">
                <div class="col-sm-6 col-md-6">
                	<div class="manage_users_headline">
                    	<spring:theme code="text.account.profile.updatePasswordManageUser" />
                    </div>
                </div>
            </div>
        </div>
        <div class="row">
            <div class="col-sm-6 col-md-3">
                <div class="account-section-content">
                    <div class="account-section-form">
                        <form:form action="${resetpasswordUrl}" data-alias="account-check" method="post" modelAttribute="customerResetPasswordForm" autocomplete="off">
                        	<input type="password" name="noprefilled" style="display:none"/>
                            <form:input type="hidden" name="uid" path="uid" id="uid" />
                            <formElement:formPasswordBox idKey="profile-newPassword" labelKey="profile.newPassword" labelCSS="control-label" path="newPassword" inputCSS="form-control text password strength" mandatory="true" />
                            <formElement:formPasswordBox idKey="profile.checkNewPassword" labelKey="profile.checkNewPassword" labelCSS="control-label" path="checkNewPassword" inputCSS="form-control text password" mandatory="true" />
                            <spring:theme code="updatePwd.guideline" />
                            <div class="accountActions">
                                <button type="button" class="btn btn-primary btn-block btn-vd-primary disable-spinner" onClick="ACC.dialog.openPwdModal('${userEmail}'); return false;">
                                    <spring:theme code="text.account.profile.updatePasswordManageUser" />
                                </button>
                            </div>

                            <!-- Popup dialog -->
                            <user:dialog />
                        </form:form>
                    </div>
                </div>
            </div>

            <div class="accountActions-bottom">
                <div class="col-sm-3">
                    <ycommerce:testId code="User_Cancel_button">
                        <a href="${viewUserUrl}" id="backToProfileLink" class="cancel"> <spring:theme
                                code="text.company.backtoprofiledetails" />
                        </a>
                    </ycommerce:testId>
                </div>
            </div>

        </div>
    </div>
</template:page>