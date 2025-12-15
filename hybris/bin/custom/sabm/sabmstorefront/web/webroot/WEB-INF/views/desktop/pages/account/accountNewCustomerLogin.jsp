<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="user" tagdir="/WEB-INF/tags/desktop/user" %>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common" %>

<div id="globalMessages">
	<c:if test="${not empty param.message}">
         <div class="alert neutral">
             ${param.message }
         </div>
     </c:if>
</div> 

<c:url value="/register/saveUser" var="createUserActionUrl" />

<div ng-controller="formsCtrl" ng-init="userInit()" ng-cloak>
	<div class="page-message" ng-show="message" ng-class="messageType">
		{{message}}
	</div>
	<user:register actionNameKey="register.save" action="${createUserActionUrl}"/>
</div>

