<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="jakarta.tags.functions"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="company" tagdir="/WEB-INF/tags/addons/apbcommorgaddon/responsive/company"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="org-common" tagdir="/WEB-INF/tags/addons/apbcommorgaddon/responsive/common"%>
<spring:htmlEscape defaultHtmlEscape="false" />
<spring:url value="/my-company/organization-management/manage-users/" var="backToManageUsersUrl" htmlEscape="false" />
<spring:url value="/my-company/organization-management/manage-users/edit" var="editUserUrl" htmlEscape="false">
    <spring:param name="user" value="${customerData.uid}" />
</spring:url>
<spring:url value="/my-company/organization-management/manage-users/disable" var="disableUserUrl" htmlEscape="false">
    <spring:param name="user" value="${customerData.uid}" />
</spring:url>
<spring:url value="/my-company/organization-management/manage-users/enable" var="enableUserUrl" htmlEscape="false">
    <spring:param name="user" value="${customerData.uid}" />
</spring:url>
<spring:url value="/my-company/organization-management/manage-users/resetpassword" var="resetPasswordUrl" htmlEscape="false">
    <spring:param name="user" value="${customerData.uid}" />
</spring:url>
<spring:url value="/my-company/organization-management/manage-users/approvers" var="approversUrl" htmlEscape="false">
    <spring:param name="user" value="${customerData.uid}" />
</spring:url>
<spring:url value="/my-company/organization-management/manage-users/permissions" var="permissionsUrl" htmlEscape="false">
    <spring:param name="user" value="${customerData.uid}" />
</spring:url>
<spring:url value="/my-company/organization-management/manage-users/usergroups" var="usergroupsUrl" htmlEscape="false">
    <spring:param name="user" value="${customerData.uid}" />
</spring:url>
<spring:url value="/my-company/organization-management/manage-units/details" var="unitDetailsUrl" htmlEscape="false">
    <spring:param name="unit" value="${customerData.unit.uid}" />
</spring:url>
<spring:url value="/my-company/organization-management/manage-users/remove_customer" var="removeUserUrl" htmlEscape="false">
    <spring:param name="user" value="${customerData.uid}" />
</spring:url>

<c:set value="${cmsSite.uid eq 'sga'}" var="isSga" />
<c:set value="${fn:escapeXml(customerData.firstName)}&nbsp;${fn:escapeXml(customerData.lastName)}" var="fullName" />

<template:page pageTitle="${pageTitle}">
    <div class="account-section">
        <div>
            <org-common:headline url="${backToManageUsersUrl}" labelKey="text.company.manageUser.userDetails" />
        </div>

        <div class="account-section-content">
            <div class="well well-lg well-tertiary manage-users-well-tertiary">
                <div class="row">
                    <div class="col-sm-12 col-no-padding">
                        <div class="row">
                            <div class="col-xs-6 col-sm-4 col-md-3">
                                
                                <div class="item-group">
                                    <span class="item-label">
                                        <spring:theme code="text.company.manage.units.user.name" />
                                    </span>
                                    <span class="item-value">
                                    	<c:if test="${!isSga && not empty customerData.titleCode}">
                                                <spring:theme code="text.company.user.${customerData.titleCode}.name" />&nbsp;
                                        </c:if>
                                        ${fullName}
                                    </span>
                                </div>
                            
                                
                                <c:if test="${!isSga}">
                                    <div class="item-group">
                                        <span class="item-label">
                                            <spring:theme code="text.company.manageUser.roles" />
                                        </span>
                                        <span class="item-value">
                                           ${fn:escapeXml(customerData.asahiRole.name)}
                                        </span>
                                    </div>
                                </c:if>
                                
                                <div class="item-group">
									<span class="item-label">
										<spring:theme code="text.company.user.userEnabledStatus" />
									</span>
									<span class="item-value">
										<c:choose>
											<c:when test="${customerData.active}">
												<spring:theme code="text.company.manage.unit.user.enable" />
											</c:when>
											<c:otherwise>
												<spring:theme code="text.company.manage.unit.user.disable" />
											</c:otherwise>
										</c:choose>
									</span>
                                </div> 
								
								<c:if test="${isSga}">
									<div class="item-group">
										<span class="item-label">
											<spring:theme code="text.manage.profiles.permission" />
										</span>
										<span class="item-value">
											<c:if test="${customerData.samAccess eq 'ORDER_ONLY'}">
												<spring:theme code="manage.admin.sip.access.order.only.message" />
											</c:if>
											<c:if test="${customerData.samAccess eq 'PAY_ONLY'}">
												<c:if test="${customerData.pendingApproval eq false && customerData.accessDenied eq false}">
													<spring:theme code="manage.admin.sip.access.pay.only.message" />
												</c:if>
												<c:if test="${customerData.pendingApproval eq true && customerData.accessDenied eq false}">
													<spring:theme code="manage.admin.sip.access.pay.pending.message" />
												</c:if>
												<c:if test="${customerData.accessDenied eq true}">
													None
												</c:if>
											</c:if>
											<c:if test="${customerData.samAccess eq 'PAY_AND_ORDER'}">
												<c:if test="${customerData.pendingApproval eq false && customerData.accessDenied eq false}">
													<spring:theme code="manage.admin.sip.access.order.pay.message" />
												</c:if>
												<c:if test="${customerData.pendingApproval eq true && customerData.accessDenied eq false}">
													<spring:theme code="manage.admin.sip.access.order.only.message" /> <br>
													<spring:theme code="manage.admin.sip.access.pay.pending.message" />
												</c:if>
												<c:if test="${customerData.accessDenied eq true}">
													<spring:theme code="manage.admin.sip.access.order.only.message" />
												</c:if>
											</c:if>
										</span>
									</div>
								</c:if>
                                
                            </div>
                            <%-- <div class="col-sm-4">
                                <c:if test="${not empty customerData.contactNumber}">
                                    <div class="item-group">
                                    <span class="item-label">
                                        <spring:theme code="text.company.unit.contactNumber" />
                                    </span>
                                    <span class="item-value">
                                            ${fn:escapeXml(customerData.contactNumber)}
                                    </span>
                                    </div>
                                </c:if>
                                <div class="item-group">
                                    <span class="item-label">
                                        <spring:theme code="text.company.user.parentBusinessUnit" />
                                    </span>
                                    <span class="item-value">
                                        <a href="${unitDetailsUrl}">${fn:escapeXml(customerData.unit.name)}</a>
                                    </span>
                                </div>
                            </div> --%>
                            
                            
                            <div class="col-xs-6 col-sm-4 col-md-3">

                                <c:if test="${isSga}">
                               		 <div class="item-group">                         
		                                    <span class="item-label">
		                                        <spring:theme code="text.company.user.mobileNumber" />
		                                    </span>
		                                    <span class="item-value">
		                                            ${fn:escapeXml(customerData.mobileNumber)}
		                                    </span>    
		                             </div>
	                            </c:if>
                            
                            	<div class="item-group">
                                    <span class="item-label">
                                        <spring:theme code="text.company.user.email" />
                                    </span>
                                    <span class="item-value">
                                            ${fn:escapeXml(customerData.displayUid)}
                                    </span>
                                </div>

                                <c:if test="${!isSga}">
                                    <div class="item-group">
                                        <span class="item-label">
                                            <spring:theme code="text.company.manageUser.asahiAccess" />
                                        </span>
                                        <span class="item-value">
                                              <c:choose>
                                                  <c:when test="${customerData.isAdminUser}">
                                                      <spring:theme code="b2busergroup.b2badmingroup.name"/><br/>
                                                  </c:when>
                                                  <c:otherwise>
                                                      <spring:theme code="b2busergroup.b2bcustomergroup.name"/><br/>
                                                  </c:otherwise>
                                              </c:choose>
                                        </span>
                                    </div>
                                </c:if>
                                
                            </div>
                        </div>
                    </div>
                  
                </div>
                <div class="row manage-user-btn">
                    <div class="col-sm-3">
                        <div class="item-action">
                             <a href="${resetPasswordUrl}" id="manageResetPassword" class="button edit btn btn-block btn-primary btn-edit-button account-disable-link">
                        		<spring:theme code="text.company.user.resetPassword" />
                    		</a>
                        </div>
                    </div>
                    <c:if test="${!isSga}">
                        <div class="col-sm-3">
                            <div class="item-action">
                                <c:choose>
                                    <c:when test="${customerData.active}">
                                        <div class="js-action-confirmation-modal"><!-- disable-link -->

                                            <form:form action="${disableUserUrl}">
                                                <button type="submit" class="button edit btn btn-block btn-primary account-disable-btn">
                                                    <spring:theme code="text.company.manageusers.button.disableuser" />
                                                </button>
                                            </form:form>
                                            <input id="accountDisabledVal" type="hidden" value="${customerData.active}" />
                                        </div>
                                    </c:when>
                                    <c:otherwise>
                                        <c:choose>
                                            <c:when test="${customerData.unit.active}">
                                                <div class="item-action">
                                                    <form:form action="${enableUserUrl}">
                                                        <button type="submit" class="button edit btn btn-block btn-primary account-disable-btn">
                                                            <spring:theme code="text.company.manageusers.button.enableuser" />
                                                        </button>
                                                    </form:form>
                                                </div>
                                            </c:when>
                                            <c:otherwise>
                                                <div class="enable-link">
                                                    <button type="button" disabled>
                                                        <spring:theme code="text.company.manageusers.button.enableuser" />
                                                    </button>
                                                </div>
                                            </c:otherwise>
                                        </c:choose>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </div>
                    </c:if>
                    <div class="col-sm-3">
                        <div class="item-action">
                            <a href="${editUserUrl}"  id="manageUserEdit" class="button edit btn btn-block btn-vd-primary btn-primary btn-edit-button account-disable-link">
                                <spring:theme code="text.company.manageUser.button.edit" />
                            </a>
                        </div>
                    </div>
                </div>
            </div>
            <%-- <div class="accountActions-link">
                <div class="enable-link">
                    <a href="${resetPasswordUrl}">
                        <spring:theme code="text.company.user.resetPassword" />
                    </a>
                </div>
                
            </div> --%>
            
            <company:actionConfirmationModal id="disable" targetUrl="${disableUserUrl}" messageKey="text.company.manageuser.disableuser.confirmation" messageArguments="${customerData.uid}"/>
            <%--  <div class="account-list">
                Approvers
                <org-common:selectEntityHeadline url="${approversUrl}" labelKey="text.company.manage.units.header.approvers" />
                <c:if test="${not empty customerData.approvers}">
                    <div class="account-cards">
                        <div class="row">
                            <c:forEach items="${customerData.approvers}" var="user">
                                <spring:url value="/my-company/organization-management/manage-users/details" var="approverUrl" htmlEscape="false">
                                    <spring:param name="user" value="${user.email}" />
                                </spring:url>
                                <div class="col-xs-12 col-sm-6 col-md-4 card">
                                    <ul class="pull-left">
                                        <li>
                                            <ycommerce:testId code="user_name_link_details">
                                                <a href="${approverUrl}">${fn:escapeXml(user.name)}</a>
                                            </ycommerce:testId>
                                        </li>
                                        <li>
                                            <ycommerce:testId code="user_email">
                                                ${fn:escapeXml(user.email)}
                                            </ycommerce:testId>
                                        </li>
                                    </ul>
                                    <div class="account-cards-actions">
                                        <span class="js-action-confirmation-modal remove">
                                            <a href="#" data-action-confirmation-modal-title="<spring:theme code="text.company.users.remove.confirmation.title.b2bapprovergroup"/>"
                                               data-action-confirmation-modal-id="removeApprover-${ycommerce:normalizedCode(user.uid)}">
                                                <span class="glyphicon glyphicon-remove"></span>
                                            </a>
                                        </span>
                                        <spring:url value="/my-company/organization-management/manage-users/approvers/remove/" var="removeApproverUrl" htmlEscape="false">
                                            <spring:param name="user" value="${customerData.uid}" />
                                            <spring:param name="approver" value="${user.uid}" />
                                        </spring:url>
                                        <company:actionConfirmationModal id="removeApprover-${ycommerce:normalizedCode(user.uid)}" targetUrl="${removeApproverUrl}"
                                                                         messageKey="text.company.users.remove.confirmation.b2bapprovergroup" messageArguments="${user.uid}, ${customerData.uid}"
                                                                         actionButtonLabelKey="text.company.delete.button" />
                                    </div>
                                </div>
                            </c:forEach>
                        </div>
                    </div>
                </c:if>
                Permissions
                <org-common:selectEntityHeadline url="${permissionsUrl}" labelKey="text.company.manageUser.permission.title" />
                <c:if test="${not empty customerData.permissions}">
                    <div class="account-cards">
                        <div class="row">
                            <c:forEach items="${customerData.permissions}" var="permission">
                                <div class="col-xs-12 col-sm-6 col-md-4 card">
                                    <company:permissionCardDetails permission="${permission}" action="permission" listCSSClass="pull-left"/>
                                    <div class="account-cards-actions">
                                        <span class="js-action-confirmation-modal remove">
                                            <a href="#" data-action-confirmation-modal-title="<spring:theme code="text.company.users.remove.confirmation.title.permission"/>"
                                               data-action-confirmation-modal-id="removePermission-${ycommerce:normalizedCode(permission.code)}">
                                                <span class="glyphicon glyphicon-remove"></span>
                                            </a>
                                        </span>
                                        <spring:url value="/my-company/organization-management/manage-users/permissions/remove/" var="removePermissionUrl" htmlEscape="false">
                                            <spring:param name="user" value="${customerData.uid}" />
                                            <spring:param name="permission" value="${permission.code}" />
                                        </spring:url>
                                        <company:actionConfirmationModal id="removePermission-${ycommerce:normalizedCode(permission.code)}" targetUrl="${removePermissionUrl}"
                                                                         messageKey="text.company.users.remove.confirmation.permission" messageArguments="${permission.code}, ${customerData.uid}"
                                                                         actionButtonLabelKey="text.company.delete.button" />
                                    </div>
                                </div>
                            </c:forEach>
                        </div>
                    </div>
                </c:if>
                User Groups
                <org-common:selectEntityHeadline url="${usergroupsUrl}" labelKey="text.company.manageUser.usergroups.title" />
                <c:if test="${not empty customerData.permissionGroups}">
                    <div class="account-cards">
                        <div class="row">
                            <c:forEach items="${customerData.permissionGroups}" var="group">
                                <div class="col-xs-12 col-sm-6 col-md-4 card">
                                    <ul class="pull-left">
                                        <li>
                                            <ycommerce:testId code="permissiongroup_id_link">
                                                <spring:url value="/my-company/organization-management/manage-usergroups/details/" var="permissionGroupUrl" htmlEscape="false">
                                                    <spring:param name="usergroup" value="${group.uid}" />
                                                </spring:url>
                                                <c:choose>
                                                    <c:when test="${group.editable}">
                                                        <a href="${permissionGroupUrl}">${fn:escapeXml(group.uid)}</a>
                                                    </c:when>
                                                    <c:otherwise>
                                                        ${fn:escapeXml(group.uid)}
                                                    </c:otherwise>
                                                </c:choose>
                                            </ycommerce:testId>
                                        </li>
                                        <li>
                                            <ycommerce:testId code="permissiongroup_name_link">
                                                ${fn:escapeXml(group.name)}
                                            </ycommerce:testId>
                                        </li>
                                        <li>
                                            <ycommerce:testId code="permissiongroup_parentunit_link">
                                                <spring:url value="/my-company/organization-management/manage-units/details" var="parentUnitUrl" htmlEscape="false">
                                                    <spring:param name="unit" value="${group.unit.uid}" />
                                                </spring:url>
                                                <c:choose>
                                                    <c:when test="${group.editable}">
                                                        <a href="${parentUnitUrl}">${fn:escapeXml(group.unit.name)}</a>
                                                    </c:when>
                                                    <c:otherwise>
                                                        ${fn:escapeXml(group.unit.name)}
                                                    </c:otherwise>
                                                </c:choose>
                                            </ycommerce:testId>
                                        </li>
                                    </ul>
                                    <div class="account-cards-actions">
                                        <span class="js-action-confirmation-modal remove">
                                            <a href="#" data-action-confirmation-modal-title="<spring:theme code="text.company.users.remove.confirmation.title.usergroup"/>"
                                               data-action-confirmation-modal-id="removeUserGroup-${ycommerce:normalizedCode(group.uid)}">
                                                <span class="glyphicon glyphicon-remove"></span>
                                            </a>
                                        </span>
                                        <spring:url value="/my-company/organization-management/manage-users/usergroups/remove/" var="removeUserGroupUrl" htmlEscape="false">
                                            <spring:param name="user" value="${customerData.uid}" />
                                            <spring:param name="usergroup" value="${group.uid}" />
                                        </spring:url>
                                        <company:actionConfirmationModal id="removeUserGroup-${ycommerce:normalizedCode(group.uid)}" targetUrl="${removeUserGroupUrl}"
                                                                         messageKey="text.company.users.remove.confirmation.permission" messageArguments="${group.uid}, ${customerData.uid}"
                                                                         actionButtonLabelKey="text.company.delete.button" />
                                    </div>
                                </div>
                            </c:forEach>
                        </div>
                    </div>
                </c:if>
            </div> --%>
            <div class="row mb-35">
                <div class="col-xs-12 col-sm-5 col-md-4">
                    <div class="accountActions-bottom">
                        <org-common:back cancelUrl="${backToManageUsersUrl}" displayTextMsgKey="text.company.manageUsers.back.button" />
                    </div>
                </div>
            </div>

            <c:if test="${isSga}">
                <div class="row">
                    <div class="col-xs-12">
                        <div class="border-top py-20">
                            <div class="h5 mb-5"><strong>Temporarily disable a profile from this Customer Account</strong></div>
                            <p>When a profile is disabled, they will be unable to log into ALB Connect for the current Customer Account (<strong>${accountId}</strong> - <strong>${accountName}</strong>).</p>
                        </div>
                    </div>
                    <div class="col-sm-6 col-md-3 mb-35">
                        <c:choose>
                            <c:when test="${customerData.active}">
                                <div class="js-action-confirmation-modal"><!-- disable-link -->
                                    <form:form action="${disableUserUrl}">
                                        <button type="submit" class="button edit btn btn-block btn-primary account-disable-btn">
                                            <spring:theme code="text.company.manageusers.button.disableuser" />
                                        </button>
                                    </form:form>
                                    <input id="accountDisabledVal" type="hidden" value="${customerData.active}" />
                                </div>
                            </c:when>
                            <c:otherwise>
                                <c:choose>
                                    <c:when test="${customerData.unit.active}">
                                        <div class="item-action">
                                            <form:form action="${enableUserUrl}">
                                                <button type="submit" class="button edit btn btn-block btn-primary account-disable-btn">
                                                    <spring:theme code="text.company.manageusers.button.enableuser" />
                                                </button>
                                            </form:form>
                                        </div>
                                    </c:when>
                                    <c:otherwise>
                                        <div class="enable-link">
                                            <button type="button" disabled>
                                                <spring:theme code="text.company.manageusers.button.enableuser" />
                                            </button>
                                        </div>
                                    </c:otherwise>
                                </c:choose>
                            </c:otherwise>
                        </c:choose>
                    </div>

                    <div class="clear"></div>

                   <%--  <c:if test="${removeUserEnabled}">
                        <div class="col-xs-12">
                            <div class="border-top py-20">
                                <div class="h5 mb-5"><strong>Permanently remove a profile from this Customer Account</strong></div>
                                <p>When a profile is removed, they will no longer be associated with the current Customer Account (<strong>${accountId}</strong> - <strong>${accountName}</strong>). This action cannot be undone.</p>
                            </div>
                        </div>

                        <div class="col-sm-6 col-md-3 mb-35">
                            <form:form action="${removeUserUrl}">
                                <button type="submit" class="button edit btn btn-block btn-primary account-disable-btn uppercase">
                                    <spring:theme code="text.company.manageusers.button.removeuser" />
                                </button>
                            </form:form>
                        </div>
                    </c:if> --%>
                </div>
            </c:if>
        </div>
    </div>
</template:page>