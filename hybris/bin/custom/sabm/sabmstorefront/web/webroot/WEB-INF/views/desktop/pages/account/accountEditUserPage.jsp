<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="user" tagdir="/WEB-INF/tags/desktop/user" %>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common" %>

<c:url value="/register/editUser" var="editUserActionUrl" />

<div ng-controller="formsCtrl" ng-init="userInit()" ng-cloak>
	<div class="page-message" ng-show="message" ng-class="messageType">
		{{message}}
	</div>
	<user:editUser actionNameKey="register.save" action="${editUserActionUrl}"/>
</div>
