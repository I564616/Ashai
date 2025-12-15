<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template" %>
<%@ taglib prefix="formElement" tagdir="/WEB-INF/tags/desktop/formElement" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<div id="topMessage" class="alert negative" style="display: none">
	<spring:theme code="form.global.error" />
</div>
<div class="row">
	<div class="col-xs-12">
		<h1><spring:theme code="updatePwd.title"/></h1>
	</div>
  	<div class="col-md-4">
		<p><spring:theme code="updatePwd.description"/></p>
		<form:form id="updatePwdForm" method="post" modelAttribute="updatePwdForm">
	        <div class="form-group">
				<label for="profile-newPassword"><spring:theme code="profile.newPassword" /></label>
				<div class="input-icon-group">
					<input id="updatePwd-pwd" name="pwd" class="text password offset-bottom-input form-control js_password" type="password" autocomplete="off"/>
					<span class="input-group-addon info" data-toggle="tooltip" data-placement="top" title='<spring:theme code="password.tooltip"/>'><svg class="icon-info"><use xlink:href="#icon-info"></use></svg></span>
				</div>
				<span class="error error_security" style="display: none"><spring:theme code="updatePwd.pwd.invalid"/></span>
          	</div>
	        <div id="confirmPwd" class="form-group">
	          <formElement:formPasswordBox idKey="updatePwd.checkPwd" labelKey="updatePwd.checkPwd" path="checkPwd" inputCSS="text password form-control checkPwd" mandatory="true" errorPath="updatePwdForm"/>
	          <span class="error error_match" style="display: none"><spring:theme code="updatePwd.match"/></span>
	        </div>
	        <button id="updatePwd_button" type="submit" class="btn btn-primary btn-large"><spring:theme code="updatePwd.submit"/></button>
		</form:form>
		<div class="links-list">
			<p>
		  		<c:url value="/login" var="loginUrl" scope="request"/>
        		<a href="${loginUrl}"><spring:theme code="updatePwd.cancel" /></a>
		  	</p>
	  	</div>
  	</div>
</div>