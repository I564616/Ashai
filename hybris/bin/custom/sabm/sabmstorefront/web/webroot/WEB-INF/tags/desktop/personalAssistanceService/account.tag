<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<spring:theme code="text.personal.assistance.form.msg.account.no" var="accountNo"/>
<spring:theme code="text.personal.assistance.form.msg.invalid.details" var="invalidDetails"/>

<div class="row margin-top-20" ng-if="pa.type == 'account'">
	<div class="col-xs-12">
      	<div class="row">
      		<div class="col-md-4 form-group">
	       		<label for="account-no">${accountNo}</span></label>
				<div class="input-icon-group">
					<input id="account-no" ng-pattern="/^\d+$/" name="account_no" class="form-control" minlength="3" type="text" ng-model="$parent.account_no" ng-required="pa.type == 'account'">
				</div>

				<span class="error" ng-show="paRequest.account_no.$error.pattern">${invalidDetails}</span>
			</div>
      	</div>
	</div>
</div>