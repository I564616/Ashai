<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template" %>
<%@ taglib prefix="formElement" tagdir="/WEB-INF/tags/responsive/formElement"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="org-common" tagdir="/WEB-INF/tags/addons/apbcommorgaddon/responsive/common" %>
<%@ taglib prefix="customFormElement" tagdir="/WEB-INF/tags/addons/apbcommorgaddon/responsive/customFormElement" %>
<%@ taglib prefix="user" tagdir="/WEB-INF/tags/responsive/user"%>

<spring:htmlEscape defaultHtmlEscape="false" />
<c:set value="${cmsSite.uid eq 'sga'}" var="isSga" />
<c:set value="${not empty b2BCustomerForm.uid}" var="disabled" />
<c:choose>
    <c:when test="${isSga && not empty b2BCustomerForm.uid}">
        <spring:message code="text.account.profile.email.tooltip" var="info"/>
    </c:when>
    <c:otherwise>
        <c:set value="${null}" var="info" />
    </c:otherwise>
</c:choose>

<c:choose>
    <c:when test="${not empty b2BCustomerForm.uid}">
        <spring:message code="sga.login.link.resetPwd.popup.edituser" var="popupMessage" />
    </c:when>
    <c:otherwise>
        <spring:message code="sga.login.link.resetPwd.popup.register" var="popupMessage" />
    </c:otherwise>
</c:choose>

<c:choose>
    <c:when test="${not empty selectedRadio}">
        <c:set value="${selectedRadio}" var="permissions" />
    </c:when>
    <c:otherwise>
        <c:set value="ORDER_ONLY" var="permissions" />
    </c:otherwise>
</c:choose>

<!-- Validation Error messages -->
<spring:theme code="form.global.error" var="formGlobalError" />
<spring:message code="profile.firstName.invalid" var="firstNameInvalid" />
<spring:message code="profile.lastName.invalid" var="lastNameInvalid" />
<spring:message code="profile.email.blank" var="emailBlank" />
<spring:message code="profile.email.invalid" var="emailInvalid" />
<input id="formGlobalError" type="hidden" value="${formGlobalError}" />
<input id="firstNameInvalid" type="hidden" value="${firstNameInvalid}" />
<input id="lastNameInvalid" type="hidden" value="${lastNameInvalid}" />
<input id="emailBlank" type="hidden" value="${emailBlank}" />
<input id="emailInvalid" type="hidden" value="${emailInvalid}" />
<c:set value="${request.getParameter('user')}" var="userEmail"/>

<c:if test="${empty saveUrl}">
	<c:choose>
		<c:when test="${not empty b2BCustomerForm.uid}">
			<spring:url value="/my-company/organization-management/manage-users/edit" var="saveUrl" htmlEscape="false">
				<spring:param name="user" value="${b2BCustomerForm.uid}"/>
			</spring:url>
		</c:when>
		<c:otherwise>
			<spring:url value="/my-company/organization-management/manage-users/create" var="saveUrl" htmlEscape="false"/>
		</c:otherwise>
	</c:choose>
</c:if>

<c:if test="${empty cancelUrl}">
	<c:choose>
		<c:when test="${not empty b2BCustomerForm.uid}">
			<spring:url value="/my-company/organization-management/manage-users/details" var="cancelUrl" htmlEscape="false">
				<spring:param name="user" value="${b2BCustomerForm.uid}"/>
			</spring:url>
		</c:when>
		<c:otherwise>
			<spring:url value="/my-company/organization-management/manage-users" var="cancelUrl" htmlEscape="false"/>
		</c:otherwise>
	</c:choose>
</c:if>

<template:page pageTitle="${pageTitle}">
    <div class="account-section">
        <div>
            <c:choose>
                <c:when test="${not empty b2BCustomerForm.uid}">
                    <org-common:headline url="${cancelUrl}" labelKey="text.company.${action}.edit.title"
                                         labelArguments="${fn:escapeXml(b2BCustomerForm.parentB2BUnit)}"/>
                </c:when>
                <c:otherwise>
                    <org-common:headline url="${cancelUrl}" labelKey="text.company.${action}.users.new.title"
                                         labelArguments="${fn:escapeXml(param.unit)}"/>
                </c:otherwise>
            </c:choose>
        </div>
        <div class="account-section-content">
        <p class="account-field-req"><spring:theme code="text.create.edit.page.required.message"/></p>
            <form:form action="${saveUrl}" data-alias="account-check" method="post" modelAttribute="b2BCustomerForm">
            	<c:if test="${not empty b2BCustomerForm.uid}">
            		<form:hidden path="email" value="${b2BCustomerForm.email}"/>
            	</c:if>
            	<form:hidden path="parentB2BUnit" value="${parentB2Bunit}"/>
                <div class="row">
                	<c:if test="${!isSga}">
                    <div class="col-xs-12 col-sm-4 col-md-3">
                        <formElement:formSelectBox idKey="user.title" labelKey="user.title" path="titleCode"
                                                   mandatory="true"
                                                   skipBlank="false"
                                                   skipBlankMessageKey="form.select.empty"
                                                   selectCSSClass="form-control"
                                                   items="${titleData}"/>
                    </div>
                    </c:if>  
                    <form:input type="hidden" name="uid" path="uid" id="uid"/>
                    
                    <div class="col-xs-12 col-sm-4 col-md-3 manageUserFirstname">
                        <formElement:formInputBox idKey="user.firstName" labelKey="user.firstName" path="firstName"
                                                  inputCSS="text" mandatory="true"/>
                    </div>
					<c:if test="${!isSga}">
						<div class="clearfix"></div>
					</c:if>
                    <div class="col-xs-12 col-sm-4 col-md-3 manageUserLastname">
                        <formElement:formInputBox idKey="user.lastName" labelKey="user.lastName" path="lastName"
                                                  inputCSS="text" mandatory="true"/>
                    </div>

					<c:if test="${isSga}">
						<div class="clearfix"></div>
						<div class="col-xs-12 col-sm-4 col-md-3">
                            <formElement:formInputBox idKey="user.mobileNumber" labelKey="user.mobileNumber" path="mobileNumber"
                            	                      inputCSS="text" mandatory="false"/>
                        </div>
					</c:if>
                    <div class="col-xs-12 col-sm-4 col-md-3">
                        <formElement:formInputBox idKey="user.email" labelKey="user.email" path="email"
                                                  inputCSS="text" mandatory="true" info="${info}" disabled="${disabled}"/>
                    </div>
                    <div class="clearfix"></div>

                    <c:choose>
                        <c:when test="${isSga}">
                            <div class="col-xs-12 col-sm-4 col-md-3 hide">
                                <customFormElement:formRadiobuttons
                                    idKey="text.company.user.asahiroles"
                                    labelKey="text.company.user.asahiroles"
                                    path="asahiCustomRoles"
                                    items="${asahiCustomRoles}"
                                    disabled="${not empty param.unit and not empty param.asahiCustomRole}"/>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="col-xs-12 col-sm-4 col-md-3">
                                <customFormElement:formRadiobuttons
                                    idKey="text.company.user.asahiroles"
                                    labelKey="text.company.user.asahiroles"
                                    path="asahiCustomRoles"
                                    items="${asahiCustomRoles}"
                                    disabled="${not empty param.unit and not empty param.asahiCustomRole}"/>
                            </div>
                            <div class="col-xs-12 col-sm-4 col-md-3">
                                 <customFormElement:formRadiobuttons
                                    idKey="text.company.user.roles"
                                    labelKey="text.company.user.roles"
                                    path="role"
                                    items="${roles}"
                                    disabled="${not empty param.unit and not empty param.role}"/>
                            </div>
                        </c:otherwise>
                    </c:choose>

                    <div class="clearfix"></div>

					<c:if test="${isSga}">
						<div class="row no-margin">
							<div class="col-md-12 col-sm-12 manage-profile-permissions">
								<legend><spring:theme code="text.manage.profiles.permission" /></legend>
								<div>
									<input id="orderOnly" name="samAccess" type="radio" <c:if test='${permissions eq "ORDER_ONLY"}'>checked</c:if> value="ORDER_ONLY" >
									<div class="radio-button"><spring:theme code="text.manage.profiles.order.only"/></div>
								</div>

								<div><input id="payOnly" name="samAccess" type="radio" <c:if test='${permissions eq "PAY_ONLY"}'>checked</c:if> value="PAY_ONLY" >
									<c:choose>
										<c:when test="${showPayPendingMessage}">
											<div class="radio-button pending-access">
												<spring:theme code="text.manage.profiles.pay.only.pending"/>
											</div>
										</c:when>
										<c:otherwise>
											<div class="radio-button">
												<spring:theme code="text.manage.profiles.pay.only"/>
											</div>
										</c:otherwise>
									</c:choose>
								</div>

								<div><input id="orderAndPay" name="samAccess" type="radio" <c:if test='${permissions eq "PAY_AND_ORDER"}'>checked</c:if> value="PAY_AND_ORDER" >
									<c:choose>
										<c:when test="${showOrderPayPendingMessage}">
											<div class="radio-button pending-access">
												<spring:theme code="text.manage.profiles.order.and.pay.pending"/>
											</div>
										</c:when>
										<c:otherwise>
											<div class="radio-button">
												<spring:theme code="text.manage.profiles.order.and.pay"/>
											</div>
										</c:otherwise>
									</c:choose>
								</div>
								<div>
									<c:choose>
										<c:when test="${approvalEmailId eq null}">
											<spring:theme code="manage.admin.sips.access.new.profile.message.validation" />
										</c:when>
										<c:otherwise>
											<spring:theme code="manage.admin.sips.access.new.profile.message" arguments="${approvalEmailId}" />
										</c:otherwise>
									</c:choose>
								</div>
							</div>
						</div>
						<br>
					</c:if>                    
                    <div class="col-xs-12">
                        <div>
                            <div class="row">

                                <c:choose>
                                    <c:when test="${not empty b2BCustomerForm.uid}">
                                        <spring:message code="text.company.editUser.button" var="saveButton"/>
                                    </c:when>
                                    <c:otherwise>
                                        <spring:message code="text.company.save.button" var="saveButton"/>
                                    </c:otherwise>
                                </c:choose>
                                <div class="col-sm-4 col-md-3">
                                    <ycommerce:testId code="User_Save_button">
                                        <button type="button" class="btn btn-block btn-primary btn-vd-primary save disable-spinner" onClick="ACC.dialog.openProfileModal('email'); return false;">${saveButton}</button>
                                    </ycommerce:testId>
                                </div>
                                 <div class="clearfix"></div>
                                 <div class="accountActions-bottom">
                                <div class="col-sm-4 col-md-3">
                                    <ycommerce:testId code="User_Cancel_button">
                                        <a href="${cancelUrl}" class="cancel">
                                           <c:choose>
		                                        <c:when test="${not empty b2BCustomerForm.uid}">
		                                        	<spring:theme code="text.company.back.to.profiles.page.button"/>
		                                        </c:when>
															<c:otherwise>
																<spring:theme code="text.company.backtomanageprofile.button"/>
															</c:otherwise>                                        
	                                        </c:choose>
                                        </a>
                                    </ycommerce:testId>
                                </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Popup dialog -->
                <spring:message code="text.company.manageUser.title" var="title" />
                <user:dialog title="${title}" message="${popupMessage}"/>
            </form:form>
        </div>
    </div>
</template:page>