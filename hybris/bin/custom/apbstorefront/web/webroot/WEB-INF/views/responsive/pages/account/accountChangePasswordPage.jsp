<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="formElement" tagdir="/WEB-INF/tags/responsive/formElement"%>

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
<spring:theme code="form.global.error" var="formGlobalError" />
<spring:message code="profile.currentPassword.invalid" var="currentPasswordInvalid" />
<spring:message code="updatePwd.pwd.invalid" var="updatePwdInvalid" />
<spring:message code="validation.checkPwd.equals" var="validationCheckPwdEquals" />
<input id="formGlobalError" type="hidden" value="${formGlobalError}" />
<input id="currentPasswordInvalid" type="hidden" value="${currentPasswordInvalid}" />
<input id="updatePwdInvalid" type="hidden" value="${updatePwdInvalid}" />
<input id="validationCheckPwdEquals" type="hidden" value="${validationCheckPwdEquals}" />

<c:choose>
    <c:when test="${cmsSite.uid eq 'sga'}">
        <c:set value="" var="white" />
    </c:when>
    <c:otherwise>
        <c:set value="white" var="white" />
    </c:otherwise>
</c:choose>

<div class="account-section-header user-register__headline secondary-page-title">
	<div class="row">
		<div class="container-fluid col-md-6">
			<spring:theme code="text.account.profile.updatePasswordForm"/>
		</div>
	</div>
</div>
<div class="row">
	<div class="container-fluid col-md-6">
		<div class="account-section-content">
			<p class="account-field-req">
			<spring:theme code="text.account.profile.updatePasswordForm.message"/>
			</p>
			<div class="account-section-form update-password">
				<form:form action="${action}" data-alias="account-check" method="post" modelAttribute="updatePasswordForm">
                    <div class="row">
                    	<div class="col-md-6" >
                    	<input type="password" name="noprefilled" style="display:none"/>
					<formElement:formPasswordBox idKey="currentPassword"
												 labelKey="profile.currentPassword" path="currentPassword" inputCSS="form-control"
												 mandatory="true" />
						</div>
					</div>		
                    <div class="row">
                    	<div class="col-md-6" >					
					<formElement:formPasswordBox idKey="newPassword"
												 labelKey="profile.newPassword" path="newPassword" inputCSS="form-control"
												 mandatory="true" />
						</div>						 
                    	<div class="col-md-6" >	
					<formElement:formPasswordBox idKey="checkNewPassword"
												 labelKey="profile.checkNewPassword" path="checkNewPassword" inputCSS="form-control"
												 mandatory="true" />
                        </div>
                      </div>  
					<spring:theme code="updatePwd.guideline" />
					<div class="row">
						<div class="col-sm-6">
							<div class="accountActions">
								<button class="btn btn-primary btn-block btn-vd-primary disable-spinner" onclick="ACC.dialog.openPwdModal('${userEmailId}'); return false;">
									<spring:theme code="updatePwd.submit" text="Update Password" />
								</button>
							</div>
						</div>
						<%-- <div class="col-sm-6 col-sm-pull-6">
							<div class="accountActions">
								<button type="button" class="btn btn-default btn-block backToHome">
									<spring:theme code="text.button.cancel" text="Cancel" />
								</button>
							</div>
						</div> --%>
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
                                    <button type="submit" class="btn btn-vd-primary ${white}" style="width: 50%">CONFIRM</button>
                                </div>

                                <div class="popup-close cursor" data-dismiss="modal">
                                    <img src="${closeIconUrl}">
                                </div>
                            </div>
                        </div>
                    </div>
				</form:form>
			</div>
		</div>
	</div>
</div>