<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<spring:theme code="text.personal.assistance.form.msg.email" var="email"/>
<spring:theme code="text.personal.assistance.form.msg.invalid.details" var="invalidDetails"/>

<div class="row margin-top-20" ng-if="pa.type == 'user'">
	<div class="col-xs-12">
      	<div class="row">
      		<div class="col-md-4 form-group">
	       		<label for="user-email">${email}</span></label>
				<div class="input-icon-group">
					<input id="user-email" ng-pattern="string" name="user_email" class="form-control" minlength="3" type="text" ng-model="$parent.user_email" ng-required="pa.type == 'user'">
				</div>
				<span class="error" ng-show="paRequest.user_email.$error.email && $parent.submitted">${invalidDetails}</span>
			</div>
      	</div>
	</div>
</div>



