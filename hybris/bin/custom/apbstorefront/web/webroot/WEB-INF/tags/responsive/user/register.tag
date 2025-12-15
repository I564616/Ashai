<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ attribute name="actionNameKey" required="true"
	type="java.lang.String"%>
<%@ attribute name="action" required="true" type="java.lang.String"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="formElement"
	tagdir="/WEB-INF/tags/responsive/formElement"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="user" tagdir="/WEB-INF/tags/responsive/user"%>

<c:set var="samAccessOptions" value="${[{'name': 'Order Only', 'code': 'ORDER_ONLY'}, {'name': 'Pay Only', 'code': 'PAY_ONLY'}, {'name': 'Order & Pay', 'code': 'PAY_AND_ORDER'}]}" />

<!-- Validation Error messages -->
<spring:theme code="form.global.error" var="formGlobalError" />
<spring:message code="register.title.invalid" var="titleInvalid" />
<spring:message code="register.firstName.invalid" var="firstNameInvalid" />
<spring:message code="register.lastName.invalid" var="lastNameInvalid" />
<spring:message code="register.email.invalid" var="emailInvalid" />
<spring:message code="register.email.confirm.match.invalid" var="emailMatchInvalid" />
<spring:message code="register.email.confirm.invalid" var="emailConfirmInvalid" />
<c:choose>
    <c:when test="${cmsSite.uid eq 'sga'}">
        <spring:message code="register.alb.account.id.invalid" var="accountIdInvalid" />
    </c:when>
    <c:otherwise>
        <spring:message code="register.apb.account.id.invalid" var="accountIdInvalid" />
        <spring:message code="register.title.invalid" var="titleInvalid" />
    </c:otherwise>
</c:choose>
<spring:message code="register.request.abn.invalid" var="abnInvalid" />
<spring:message code="register.samAccess.invalid" var="samAccessInvalid" />
<spring:message code="register.pwd.invalid" var="pwdInvalid" />
<spring:message code="validation.checkPwd.equals" var="checkPwdEquals" />
<spring:message code="register.term.condition.check" var="termConditionCheck" />

<input id="formGlobalError" type="hidden" value="${formGlobalError}" />

<div id="validation-error-messages">
    <c:if test="${cmsSite.uid eq 'apb'}">
        <input id="titleInvalid" data-alias="register.title" type="hidden" value="${titleInvalid}" />
    </c:if>
    <input id="firstNameInvalid" data-alias="register.firstName" type="hidden" value="${firstNameInvalid}" />
    <input id="lastNameInvalid" data-alias="register.lastName" type="hidden" value="${lastNameInvalid}" />
    <input id="emailInvalid" data-alias="register.email" type="hidden" value="${emailInvalid}" />
    <input id="emailMatchInvalid" data-alias="register.email.match.invalid" type="hidden" value="${emailMatchInvalid}" />
    <input id="emailConfirmInvalid" data-alias="register.email.confirm" type="hidden" value="${emailConfirmInvalid}" />
    <input id="accountIdInvalid" data-alias="register.apb.account.id" type="hidden" value="${accountIdInvalid}" />
    <input id="abnInvalid" data-alias="register.abn" type="hidden" value="${abnInvalid}" />
    <input id="samAccessInvalid" data-alias="samAccess" type="hidden" value="${samAccessInvalid}" />
    <input id="pwdInvalid" data-alias="password" type="hidden" value="${pwdInvalid}" />
    <input id="checkPwdEquals" data-alias="register.checkPwd" type="hidden" value="${checkPwdEquals}" />
    <input id="termConditionCheck" data-alias="Yes, I agree to the" type="hidden" value="${termConditionCheck}" />
</div>

<div class="user-register__headline">
	<spring:theme code="register.first.time.customer" />
</div>
<p>
	<c:choose>
		<c:when test="${cmsSite.uid eq 'sga'}">
			<spring:theme code="sga.register.description" />
					<c:out value="${customerCareNo}"></c:out>
			<br>
			<spring:theme code="sga.register.description1" />
			<spring:theme code="sga.register.error.message" />
		</c:when>
		<c:otherwise>
			<spring:theme code="register.description" />
					<c:out value="${customerCareNo}"></c:out>
			<br>
			<spring:theme code="register.description1" />
			<spring:theme code="register.error.message" />

		</c:otherwise>
	</c:choose>
</p>

<form:form method="post" modelAttribute="apbRegisterForm"
	action="${action}" data-alias="account-check">

    <div class="row form-row-margin">
        <div class="checkout_subheading row-margin-fix">
            <spring:theme code="register.your.information.heading" />
        </div>
    </div>

	<div style="display: none">
		<formElement:formCheckbox idKey="registerCustomerTypeYes"
			labelKey="registerCustomerTypeYes" path="customerType" />
	</div>

	<c:if test="${cmsSite.uid ne 'sga'}">
		<div class="row">
			<div class="col-md-3 col-sm-6">
				<formElement:formSelectBox idKey="register.title"
					labelKey="register.title" selectCSSClass="form-control"
					path="titleCode" mandatory="true" skipBlank="false"
					skipBlankMessageKey="form.select.empty" items="${titles}" />
			</div>
		</div>
	</c:if>
	<div class="row">
		<div class="col-md-3 col-sm-6">
			<formElement:formInputBox idKey="register.firstName"
				labelKey="register.firstName" path="firstName"
				inputCSS="form-control" mandatory="true" />
		</div>
		<div class="col-md-3 col-sm-6">
			<formElement:formInputBox idKey="register.lastName"
				labelKey="register.lastName" path="lastName" inputCSS="form-control"
				mandatory="true" />
		</div>
	</div>

	<div class="row">
		<div class="col-md-3 col-sm-6">
			<formElement:formInputBox idKey="register.email"
				labelKey="register.email" path="email" inputCSS="form-control"
				maxlength="${emailMaxSize}" mandatory="true" />
		</div>

		<div class="col-md-3 col-sm-6">
			<formElement:formInputBox idKey="register.email.confirm"
				labelKey="register.email.confirm" path="confirmEmail"
				maxlength="${emailMaxSize}" inputCSS="form-control" mandatory="true" />
		</div>
	</div>
	<c:if test="${cmsSite.uid ne 'sga'}">
		<div class="row">

			<div class="col-md-3 col-sm-6">

				<formElement:formSelectBox idKey="role" labelKey="role"
					selectCSSClass="form-control" path="role" mandatory="false"
					skipBlank="true" skipBlankMessageKey="form.select.empty"
					items="${asahiRole}" />

			</div>
			<div style="display: none">
				<div class="col-md-3 col-sm-6">
					<formElement:formInputBox idKey="registerRoleOtherTemp"
						labelKey="registerRoleOtherTemp" path="roleOtherTemp"
						inputCSS="form-control" mandatory="true" />
				</div>
			</div>
			<div style="display: none" id="registerRoleOther">
				<div class="col-md-3 col-sm-6">
					<formElement:formInputBox idKey="registerRoleOther"
						labelKey="registerRoleOther" path="roleOther"
						inputCSS="form-control" mandatory="true" />
				</div>
			</div>
		</div>
	</c:if>
    <div class="row form-row-margin">
        <div class="checkout_subheading row-margin-fix">
            <spring:theme code="register.company.information.heading" />
        </div>
    </div>

	<c:if test="${cmsSite.uid eq 'sga'}">
        <c:set var="samAccess" value="ORDER_ONLY"/>
        <c:if test="${apbRegisterForm.samAccess ne null}">
            <c:set var="samAccess" value="${apbRegisterForm.samAccess}"/>
        </c:if>

		<br>
		<div class="row">
			<div class="col-md-12 col-sm-12">
			    <p>Enter the details for each account you&#39;d like to register with ALB connect. At least one account must be provided.</p>
			    <br />
                <strong><p>Permission definitions</p></strong>
				<p><spring:theme code="sga.register.access.type.radio.button.order"/></p>
				<p><spring:theme code="sga.register.access.type.radio.button.pay"/></p>
				<p><spring:theme code="sga.register.access.type.radio.button.orderandpay"/></p>
			</div>
		</div>
		<br>
	</c:if>

	<div class="row">
	    <c:choose>
	        <c:when test="${cmsSite.uid ne 'sga'}">
	            <div class="col-md-3 col-sm-6">
                    <formElement:formInputBox idKey="register.apb.account.id"
                        labelKey="register.apb.account.id"
                        path="abnAccountId"
                        inputCSS="form-control mb-0" maxlength="${abnMaxSize}" mandatory="true" />
                </div>

                <div class="col-md-3 col-sm-6">
                    <formElement:formInputBox idKey="register.abn"
                        labelKey="register.abn"
                        path="abnNumber" inputCSS="form-control mb-0"
                        mandatory="true" maxlength="${abnMaxSize}" />
                </div>
	        </c:when>
	        <c:otherwise>
	            <fieldset class="col-xs-12" style="position: relative;">
                    <div class="col-xs-10 pl-0 hidden-xs">
                        <div class="col-xs-4 pl-0"><strong>Account Number</strong></div>
                        <div class="col-xs-4"><strong>ABN</strong></div>
                        <div class="col-xs-4 pr-0"><strong>Permission</strong></div>
                    </div>
                    <div class="border-line-top clear hidden-xs"></div>

                    <div id="fields-wrapper" class="fields-wrapper">
                        <c:choose>
                            <c:when test="${apbRegisterForm.albCompanyInfoData == null}">
                                <user:registerMultiAccountFields
                                    index="0"
                                    register="${register}"
                                    items="${asahiRole}"
                                    dataList="${apbRegisterForm.albCompanyInfoData}"
                                    dropdownListOptions="${samAccessOptions}"
                                    maxlength="${abnMaxSize}" />
                            </c:when>
                            <c:otherwise>
                                <c:forEach items="${apbRegisterForm.albCompanyInfoData}" var="item" varStatus="loop">
                                    <user:registerMultiAccountFields
                                        index="${loop.index}"
                                        register="${register}"
                                        items="${asahiRole}"
                                        dataList="${apbRegisterForm.albCompanyInfoData}"
                                        dropdownListOptions="${samAccessOptions}"
                                        maxlength="${abnMaxSize}" />
                                </c:forEach>
                            </c:otherwise>
                        </c:choose>
                    </div>
                    <div class="border-line-bottom clear hidden-xs"></div>
                </fieldset>

                <div class="col-md-3 col-sm-6 col-xs-12 mt-25">
                    <button type="button" id="add-row" class="btn btn-default btn-block btn-primary uppercase disable-spinner" onclick="ACC.registration.addAccount(); return false;">Add additional account</button>
                </div>
	        </c:otherwise>
	    </c:choose>
	</div>

    <div class="row form-row-margin">
        <div class="checkout_subheading row-margin-fix">
            <spring:theme code="register.set.your.password.heading" />
        </div>
    </div>

	<div class="row">
		<div class="col-md-3 col-sm-6">

		<input type="password" name="noprefilled" style="display:none"/>
			<formElement:formPasswordBox idKey="password" labelKey="register.pwd"
				path="pwd" inputCSS="form-control password-strength"
				mandatory="true" />
				<c:if test="${not empty label1 && label1 eq 'register.pwd.max.length'}">
						<span class="user-register__headline_error_message">
						</span>
				</c:if>
		</div>

		<div class="col-md-3 col-sm-6">
			<formElement:formPasswordBox idKey="register.checkPwd"
				labelKey="register.checkPwd" path="checkPwd" inputCSS="form-control"
				mandatory="true" />
		</div>
	</div>

	<div class="row">
		<div class="col-md-6 col-sm-12">
		<spring:theme code="customer.register.guideline" />
		<c:out value="${pwdMaxLen} "/>
		<spring:theme code="customer.register.guideline1" />
		</div>
		<div class="col-md-3 col-sm-6">

		</div>
	</div>

    <div class="row form-row-margin">
        <div class="checkout_subheading row-margin-fix">
            <spring:theme code="register.terms.and.conditions.heading" />
        </div>
    </div>

	<formElement:formCheckboxRegistration idKey="register.terms.and.conditions"
		labelKey="register.terms.and.conditions" path="termsCondition" labelCSS=""/>

	<input type="hidden" id="recaptchaChallangeAnswered"
		value="${requestScope.recaptchaChallangeAnswered}" />
	<div
		class="form_field-elements control-group js-recaptcha-captchaaddon"></div>

	<div class=row>
		<div class="col-md-3 col-sm-6">
			<div class="form-actions clearfix">
				<ycommerce:testId code="register_Register_button">
					<button type="button" class="btn btn-default btn-block btn-vd-primary disable-spinner" onClick="ACC.dialog.openRegisterModal(); return false;">
						<spring:theme code='${actionNameKey}' />
					</button>
				</ycommerce:testId>
			</div>
		</div>
	</div>

    <!-- Popup dialog -->
    <spring:message code="sga.login.link.resetPwd.popup.register" var="register" />
    <spring:message code="header.link.registration" var="title" />
	<user:dialog title="${title}" message="${register}"/>
</form:form>
