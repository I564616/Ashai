<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<div id="deactivateUserPopup" class="delete-user-popup mfp-hide">
	<h2 class="h1">
		<spring:theme code="register.editUser.disable.popop.header" />
	</h2>
	<br />
	<p>
		<spring:theme code="register.editUser.disable.popop.text" />
	</p>
	<spring:theme code="register.editUser.disable.popop.checkbox.message" />
	<br />
	<button onclick="$.magnificPopup.close()"
		class="btn btn-secondary col-xs-5 margin-top-10">
		<spring:theme code="register.editUser.disable.button.no" />
	</button>
	<button id="confirm-delete-user"
		class="btn btn-primary margin-top-10 col-xs-5 pull-right"
		ng-click="closeDisableModal(false, user.states)">
		<spring:theme code="register.editUser.disable.button.yes"/>
	</button>
	<div class="clearfix"></div>
</div>