<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<div id="deleteUserPopup" class="delete-user-popup mfp-hide">
	<h2 class="h1">
		<spring:theme code="register.editUser.delete.popop.header" />
	</h2>
	<p>
		<spring:theme code="register.editUser.delete.popop.text" />
	</p>
	<p>
		<spring:theme code="register.editUser.delete.popop.confirm" />
	</p>
	<button onclick="$.magnificPopup.close()"
		class="btn btn-secondary margin-top-10">
		<spring:theme code="register.editUser.delete.button.no" />
	</button>
	<button id="confirm-delete-user"
		class="btn btn-primary margin-top-10 pull-right">
		<spring:theme code="register.editUser.delete.button.yes" />
	</button>
	<div class="clearfix"></div>
	<c:url value="/your-business/deleteUser" var="deleteUserUrl" />
	<form:form action="${deleteUserUrl}" method="post" id="deleteUserForm">
		<input id="deleteUserPopupCustomerUid" type="hidden" name="uid" value="${uId}" />
		<input id="deleteUserPopupB2bUnitUid" type="hidden" name="b2bUnitId" value="${b2bUnitid}" />
	</form:form>
</div>