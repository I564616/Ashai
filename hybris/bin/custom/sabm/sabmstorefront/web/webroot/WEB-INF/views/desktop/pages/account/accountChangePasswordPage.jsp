<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="formElement" tagdir="/WEB-INF/tags/desktop/formElement" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="user" tagdir="/WEB-INF/tags/desktop/user" %>
<c:url var="profileUrl" value="/your-business/profile" />
<c:url var="yourBusinessUrl" value="/your-business" />
<div id="topMessage" class="alert negative" style="display: none">
	<spring:theme code="form.global.error" />
</div>


<div class="col-sm-9 col-md-7 trim-left trim-right">	
	<div class="accountContentPane update-password clearfix" ng-controller="updatePasswordCtrl">
		<div class="h1"><spring:theme code="text.account.profile.updatePasswordForm" text="Update Password"/></div>

		<form:form action="update-password" method="post" modelAttribute="updatePasswordForm" autocomplete="off" id="updatepasswordform" name="updatePassword">
			
            <div class="form-group relative" ng-init="init()">
                <formElement:formPasswordBox idKey="profile.currentPassword" labelKey="profile.currentPassword" path="currentPassword" inputCSS="text password" mandatory="true"/>

                <div class="help-inline error message"><spring:theme code="profile.password.notblank" text="profile.password.notblank"/></div>
                <div class="checkbox show-password">
                    <input id="show-current" type="checkbox" name="show-current" />
                    <label for="show-current"><spring:theme code="profile.currentPassword.show"/></label>
                </div>
            </div>

            <div class="form-group relative">
                <div class="control-group">
                    <label for="profile-newPassword"><spring:theme code="profile.newPassword" /></label>
                    <input id="profile-newPassword" name="newPassword" ng-model="newPassword" ng-keyup="checkNewPassword()" ng-focus="updatePassword.rules.show = true" class="text password offset-bottom-input form-control" type="password" autocomplete="off"/>
                </div>
                <%--<span class="input-group-addon info" data-toggle="tooltip" data-placement="top" title='<spring:theme code="password.tooltip"/>'><svg class="icon-info"><use xlink:href="#icon-info"></use></svg></span>--%>
                <%--<div class="help-inline error message"><spring:theme code="updatePwd.pwd.invalid" text="updatePwd.pwd.invalid"/></div>--%>
                <div class="checkbox show-password">
                    <input id="show-new" type="checkbox" name="show-new" />
                    <label for="show-new"><spring:theme code="profile.currentPassword.show"/></label>
                </div>
                <div class="password-rules" ng-show="updatePassword.rules.show">
                    <p>To keep your account secure, your password must contain:</p>
                    <div class="password-rules_characters"><span ng-class="{'pass': updatePassword.rules.characters}"></span> A minimum of 8 characters (A-Z, a-z)</div>
                    <div class="password-rules_characters"><span ng-class="{'pass': updatePassword.rules.characters}"></span> At least one lower case letter (a-z)</div>                    
                    <div class="password-rules_numbers"><span ng-class="{'pass': updatePassword.rules.numbers}"></span> At least 1 number (0-9)</div>
                </div>
            </div>

            <div class="form-group">
                <formElement:formPasswordBox idKey="profile.checkNewPassword" labelKey="profile.checkNewPassword" path="checkNewPassword" inputCSS="text password" mandatory="true"/>
                <div class="help-inline error message error_empty"><spring:theme code="profile.password.notblank" text="profile.password.notblank"/></div>
                <div class="help-inline error message error_match"><spring:theme code="validation.checkPwd.equals" text="validation.checkPwd.equals"/></div>
                <div class="checkbox show-password">
                  <input id="show-repeat" type="checkbox" name="show-repeat" />
                  <label for="show-repeat"><spring:theme code="profile.currentPassword.show"/></label>
                </div>
            </div>

            <div class="form-actions col-md-6 col-xs-12 trim-left trim-right margin-top-20 offset-bottom-small">
                <button class="btn btn-primary" type="button" onClick="rm.updatepassword.handleForm()"><spring:theme code="text.account.profile.updatePasswordForm" text="Update Password"/></button>
                <div class="margin-top-20">
                    <span>
                        <a href="javascript:window.location='${yourBusinessUrl}'" class="inline"><spring:theme code="text.account.profile.cancel" text="Cancel"/></a> &nbsp;&nbsp;
                    </span>
                </div>
            </div>

            <!-- Popup dialog -->
            <user:dialog />
		</form:form>
	</div>
</div>