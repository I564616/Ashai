<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="formElement" tagdir="/WEB-INF/tags/responsive/formElement" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<c:url value="/login" var="loginUrl"/>

<!-- Get close icon url-->
<spring:theme code="img.closeIcon" text="/" var="closeIconPath" />
<c:choose>
	<c:when test="${originalContextPath ne null}">
		<c:url value="${closeIconPath}" context="${originalContextPath}" var="closeIconUrl" />
	</c:when>
	<c:otherwise>
		<c:url value="${closeIconPath}" var="closeIconUrl" />
	</c:otherwise>
</c:choose>

<!-- Email validation Error messages -->
<spring:message code="forgottenPwd.email.null" var="forgottenPwdEmailNull" />
<spring:message code="forgottenPwd.error.message" var="forgottenErrorMessage" />
<spring:message code="forgottenPwd.email.invalid" var="forgottenPwdEmailInvalid" />
<input id="forgottenPwdEmailNull" type="hidden" value="${forgottenPwdEmailNull}" />
<input id="forgottenErrorMessage" type="hidden" value="${forgottenErrorMessage}" />
<input id="forgottenPwdEmailInvalid" type="hidden" value="${forgottenPwdEmailInvalid}" />

<spring:htmlEscape defaultHtmlEscape="true" />

<div class="login-section forgotten-password">
<div class="login-left-content-slot">
	<div class="login-page__headline">
		<c:choose>
			<c:when test="${cmsSite.uid eq 'sga'}">
				<spring:theme code="forgottenPwd.title.asahi"/>
				<c:set value="" var="white" />
			</c:when>
			<c:otherwise>
				<spring:theme code="resetPwd.title.asahi"/>
				<c:set value="white" var="white" />
			</c:otherwise>
		</c:choose>
	</div>
	<div class="description"><spring:theme code="forgottenPwd.description"/></div>
	<form:form method="post" modelAttribute="asahiForgottenPwdForm">
		<div class="login-input-field">
			<ycommerce:testId code="login_forgotPasswordEmail_input">
				<formElement:formInputBox idKey="forgottenPwd.email" labelKey="" path="email" mandatory="true" placeholder="EMAIL" />
			</ycommerce:testId>
			<ycommerce:testId code="login_forgotPasswordSubmit_button">
				<button class="btn btn-primary btn-block btn-vd-primary btn-forgot-btn disable-spinner" type="button" onclick="ACC.forgottenpassword.openModal(this)">
					<spring:theme code="forgottenPwd.title.btn"/> 
				</button>
			</ycommerce:testId>
		</div>

		<div id="forgorpwd-template" class="modal fade" tabindex="-1" role="dialog" aria-labelledby="uploaderTemplateLabel"  aria-hidden="false" data-backdrop="false">
            <div class="modal-dialog recommendation-popup-container">
                <div class="modal-content" style="cursor: auto;">
                    <div class="modal-body ">
                        <h2 class="h3"><strong><spring:theme code="login.link.resetPwd" /></strong></h2>
                        <div class="row mt-25">
                            <div class="col-xs-12">
                                <c:choose>
                                    <c:when test="${cmsSite.uid eq 'sga'}">
                                        <spring:theme code="sga.login.link.resetPwd.popup.alb.text" var="site"/>
                                        <spring:theme code="sga.login.link.resetPwd.popup.cubpb" var="otherSite"/>
                                    </c:when>
                                    <c:otherwise>
                                        <spring:theme code="apb.login.link.resetPwd.popup.cubpb.text" var="site"/>
                                        <spring:theme code="sga.login.link.resetPwd.popup.alb" var="otherSite"/>
                                    </c:otherwise>
                                </c:choose>
                                <p>${site}&nbsp;<spring:theme code="sga.login.link.resetPwd.popup.text1" /></p>
                                <ul style="margin-left: 15px;">
                                    <li>
                                        <spring:theme code="sga.login.link.resetPwd.popup.cub" /> <spring:theme code="sga.login.link.resetPwd.popup.or" />
                                    </li>
                                    <li>${otherSite}</li>
                                </ul>
                                <p><spring:theme code="sga.login.link.resetPwd.popup.text2" /></p>
                            </div>
                        </div>
                    </div>

                    <div class="modal-footer" style="text-align:center; border-top: none;">
                        <c:if test="${cmsSite.uid eq 'apb'}">
                            <button type="submit" class="btn btn-vd-primary ${white}" style="width: 50%">CONFIRM</button>
                        </c:if>
                        <c:if test="${cmsSite.uid eq 'sga'}">
                            <button type="submit" class="btn btn-vd-primary ${white}" style="width: 50%;">CONFIRM</button>
                        </c:if>
                    </div>

                    <div class="popup-close cursor" data-dismiss="modal">
                        <img src="${closeIconUrl}">
                    </div>
                </div>
            </div>
        </div>
	</form:form>
	<div class="forgotten-password-link"> 
		<a href="${loginUrl}" class="site-anchor-link"><spring:theme code="forgottenPwd.backTo.login"/></a>
	</div>
	</div>
</div>