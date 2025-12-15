<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="formElement" tagdir="/WEB-INF/tags/responsive/formElement" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="user" tagdir="/WEB-INF/tags/responsive/user"%>

<c:choose>
    <c:when test="${cmsSite.uid eq 'sga'}">
        <c:set var="padding" value="pr-md-0" />
        <c:set var="readonly" value="true" />
        <c:set var="optionalTextCSS" value="capitalize" />
        <c:set var="maxwidth" value="col-lg-4 maxwidth-500" />
        <spring:message code="text.account.profile.email.tooltip" var="info"/>
    </c:when>
    <c:otherwise>
        <c:set var="padding" value="pl-0" />
        <c:set var="readonly" value="false" />
        <c:set var="info" value="${null}" />
        <c:set var="optionalTextCSS" value="" />
        <c:set var="maxwidth" value="" />
    </c:otherwise>
</c:choose>

<c:choose>
    <c:when test="${cmsSite.uid eq 'sga'}">
        <spring:message code="text.account.profile.update.title" var="popupTitle" />
    </c:when>
    <c:otherwise>
        <spring:message code="text.company.manageUser.userDetails" var="popupTitle" />
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

<div class="user-register__headline">
	<c:choose>
		<c:when test="${cmsSite.uid eq 'sga'}">
			<b><spring:theme code="text.account.profile.update.title"/></b>
		</c:when>
		<c:otherwise>
			<spring:theme code="text.account.profile.updatePersonalDetails"/>
		</c:otherwise>
	</c:choose>
</div>

<div class="row">
    <div class="container-fluid col-md-12">
        <c:choose>
            <c:when test="${cmsSite.uid eq 'sga'}">
                <p><spring:theme code="text.alb.account.profile.updatePersonalDetails.guideline"/></p>
            </c:when>
            <c:otherwise>
                <p><spring:theme code="text.account.profile.updatePersonalDetails.guideline"/></p>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<!-- SGA only -->
<c:if test="${cmsSite.uid eq 'sga'}">
    <div class="checkout_subheading row-margin-fix">
        <div><spring:theme code="text.alb.account.profile.section1.heading" /></div>
    </div>
</c:if>

<div class="row">
    <div class="container-fluid col-md-12">
        <div class="account-section-content">
            <div class="account-section-form">
                <form:form
                    action="${action}"
                    method="post"
                    modelAttribute="apbUpdateProfileForm"
                    data-notifications="${notifications}"
                    data-alias="account-check">
                    <div class="row">
                        <div class="col-xs-12 col-md-6 flex-wrap">
                            <c:if test="${cmsSite.uid ne 'sga'}">
                                <div class="col-sm-6 pl-0 pr-xs-0" >
                                    <formElement:formSelectBox idKey="profile.title" labelKey="profile.title" path="titleCode" mandatory="true" skipBlank="false" skipBlankMessageKey="form.select.empty" items="${titleData}" selectCSSClass="form-control"/>
                                 </div>
                             </c:if>
                            <div class="col-xs-12 col-sm-6 px-0">
                                <!-- <formElement:formInputBox idKey="profile.firstName" labelKey="profile.firstName" path="firstName" inputCSS="text col-md-6" mandatory="true"/> -->
                                <!-- Removing the input CSS from the form tag file since bootstrap coloumns have already been defined in this .jsp file and it messes with the alignment. -->
                                <formElement:formInputBox idKey="profile.firstName" labelKey="profile.firstName" path="firstName" mandatory="true" maxlength="75"/>
                            </div>

                            <div class="col-xs-12 col-sm-6 pl-xs-0 pr-xs-0 ${padding}">
                                <!-- <formElement:formInputBox idKey="profile.lastName" labelKey="profile.lastName" path="lastName" inputCSS="text col-md-6" mandatory="true"/>-->
                                <!-- Removing the input CSS from the form tag file since bootstrap coloumns have already been defined in this .jsp file and it messes with the alignment. -->
                                <formElement:formInputBox idKey="profile.lastName" labelKey="profile.lastName" path="lastName" mandatory="true" maxlength="75"/>
                            </div>
                            <div class="col-xs-12 col-sm-6 pl-0 pr-0">
                                <formElement:formInputBox idKey="profile.mobileNumber" labelKey="profile.mobileNumber" optionalTextCSS="${optionalTextCSS}" path="mobileNumber" mandatory="false" inputCSS="text col-md-6"  />
                            </div>

                            <c:if test="${cmsSite.uid eq 'sga'}">
                                <div class="col-xs-12 col-sm-6 pl-xs-0 pr-0">
                                    <formElement:formInputBox idKey="profile.emailAddress" labelKey="profile.emailAddress" path="emailAddress"  mandatory="true" inputCSS="text col-md-6" info="${info}" readonly="${readonly}" />
                                </div>
                            </c:if>

                            <%-- Popup need --%>
                            <c:if test="${cmsSite.uid eq 'apb'}">
                                <form:hidden path="emailAddress" />
                            </c:if>
                        </div>
                    </div>

                    <!-- SGA only - Manage Notification -->
                    <c:if test="${cmsSite.uid eq 'sga'}">
                        <div class="notifications mt-25">
                            <div class="checkout_subheading row-margin-fix">
                                <div><spring:theme code="text.alb.account.profile.section2.heading" /></div>
                            </div>
                            <div class="row mt-25">
                                <div class="col-xs-12">
                                    <p><spring:theme code="text.alb.account.profile.notification.section.info"/></p>
                                    <br />
                                </div>
                                <div class="col-xs-12 col-sm-6 ${maxwidth}">
                                    <p class="control-label"><spring:theme code="text.alb.account.profile.notification.section.emailOptin" /></p>

                                    <div class="form-group">
                                        <label for="selectAll" class="text-init-transform text-underline cursor">
                                            <input id="selectAll" class="hidden" type="checkbox" onclick="ACC.userNotifications.selectAll(this);" />
                                            <span class="checkmark pull-left hidden"></span>
                                            <span id="select-text" class="font-normal">Select all</span>
                                        </label>
                                    </div>
									
									<div class="mt-10 mb-35 mb-xs-20">
                                         <!-- <p class="control-label notification-type"><spring:theme code="text.alb.account.profile.notification.group1" /></p> -->

                                        <input type="hidden" name="notificationPrefs[0].notificationType" value="PUBLIC_HOLIDAY_ALT_CALL_DELIVERY" />
                                        <formElement:formCheckboxNotification
                                            idKey="PUBLIC_HOLIDAY_ALT_CALL_DELIVERY"
                                            labelKey="text.alb.account.profile.notification.orderday.change"
                                            labelCSS="display-flex pt-0 pl-0 text-init-transform hidden"
                                            inputCSS="m-0 hidden"
                                            path="notificationPrefs[0].emailEnabled"
                                            mandatory="false" />

                                        <input type="hidden" name="notificationPrefs[1].notificationType" value="PUBLIC_HOLIDAY_ALT_DELIVERY" />
                                        <formElement:formCheckboxNotification
                                            idKey="PUBLIC_HOLIDAY_ALT_DELIVERY"
                                            labelKey="text.alb.account.profile.notification.deliveryday.change"
                                            labelCSS="display-flex pt-0 pl-0 text-init-transform hidden"
                                            inputCSS="m-0 hidden"
                                            path="notificationPrefs[1].emailEnabled"
                                            mandatory="false"/>

                                        <input type="hidden" name="notificationPrefs[2].notificationType" value="PUBLIC_HOLIDAY_NO_DELIVERY" />
                                        <formElement:formCheckboxNotification
                                           idKey="PUBLIC_HOLIDAY_NO_DELIVERY"
                                           labelKey="Notify me about changes to my delivery days"
                                           labelCSS="display-flex pt-0 pl-0 text-init-transform hidden"
                                           inputCSS="m-0 hidden"
                                           path="notificationPrefs[2].emailEnabled"
                                           mandatory="false"/>
                                    </div>
									
                                    <div class="mb-35 mb-xs-20">
                                        <p class="control-label notification-type"><spring:theme code="text.alb.account.profile.notification.group2" /></p>

                                        <input type="hidden" name="notificationPrefs[3].notificationType" value="CONTACT_US" />
                                        <formElement:formCheckboxNotification
                                            idKey="CONTACT_US"
                                            labelKey="text.alb.account.profile.notification.contactus"
                                            labelCSS="display-flex pt-0 pl-0 text-init-transform"
                                            inputCSS="m-0"
                                            path="notificationPrefs[3].emailEnabled"
                                            mandatory="false"/>
                                    </div>

                                    <div class="mb-35 mb-xs-20">
                                        <p class="control-label notification-type"><spring:theme code="text.alb.account.profile.notification.group3" /></p>

                                        <input type="hidden" name="notificationPrefs[4].notificationType" value="ORDER_CONFIRMATION" />
                                        <formElement:formCheckboxNotification
                                            idKey="ORDER_CONFIRMATION"
                                            labelKey="text.alb.account.profile.notification.order.confirmation"
                                            labelCSS="display-flex pt-0 pl-0 text-init-transform"
                                            inputCSS="m-0"
                                            path="notificationPrefs[4].emailEnabled"
                                            mandatory="false"/>

                                        <input type="hidden" name="notificationPrefs[5].notificationType" value="PAYMENT_CONFIRMATION" />
                                        <formElement:formCheckboxNotification
                                            idKey="PAYMENT_CONFIRMATION"
                                             labelKey="text.alb.account.profile.notification.payment.confirmation"
                                             labelCSS="display-flex pt-0 pl-0 text-init-transform"
                                             inputCSS="m-0"
                                             path="notificationPrefs[5].emailEnabled"
                                             mandatory="false"/>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </c:if>

                    <div class="row">
                        <div class="col-xs-12 col-sm-6 ${maxwidth}">
                            <div id="profileEditSaveButton" class="accountActions pt-xs-10">
                                <ycommerce:testId code="personalDetails_savePersonalDetails_button">
                                    <button type="button" class="btn btn-primary btn-vd-primary btn-block disable-spinner"
                                        onClick="ACC.dialog.openProfileModal('emailAddress'); return false;">
                                    <c:choose>
                                    	<c:when test="${cmsSite.uid eq 'sga'}">
                                        	<spring:theme code="text.alb.account.profile.saveUpdates" text="Save Updates"/>
                                        </c:when>
                                        <c:otherwise>
                                        	<spring:theme code="text.account.profile.saveUpdates" text="Save Updates"/>
                                        </c:otherwise>
                                      </c:choose>
                                    </button>
                                </ycommerce:testId>
                            </div>
                        </div>
                    </div>

                    <!-- Popup dialog -->
                    <spring:message code="sga.login.link.resetPwd.popup.edituser" var="popupMessage" />
                    <user:dialog title="${popupTitle}" message="${popupMessage}"/>
                </form:form>
            </div>
        </div>
    </div>
</div>